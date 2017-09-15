package services.flickr.userPhotos;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.google.common.collect.Sets;

import models.geography.BoundingBox;
import models.trip.GeoLocation;
import models.socialmedia.FlickrUser;
import models.socialmedia.Status;
import mongo.MorphiaHelper;
import play.Logger;
import services.flickr.FlickrBot;

/**
 * Bot to get photos for each users.
 */
public class FlickrUserPhotosBot extends FlickrBot {
  private static final Logger.ALogger LOGGER = Logger.of(FlickrUserPhotosBot.class);
  private static AtomicInteger count;
  private static long start;

  static {
    count = new AtomicInteger(0);
    start = System.currentTimeMillis();
  }

  private final PeopleInterface peopleInterface;
  private final BoundingBox defaultBox;
  private boolean running;

  public FlickrUserPhotosBot() {
    peopleInterface = buildFlickr().getPeopleInterface();

    List<Double> coords = coordinates();
    defaultBox = new BoundingBox(coords.get(0), coords.get(1), coords.get(2), coords.get(3));
  }

  public static void main(String args[]) throws FlickrException {
    FlickrUserPhotosBot photosBot = new FlickrUserPhotosBot();
    photosBot.processUsers();
  }

  public void processUsers() throws FlickrException {
    long startProcess = System.currentTimeMillis();

    resetRateLimitIfNeeded(startProcess);

    // we will start processing only if another process is not running.
    if (!running) {

      List<FlickrUser> userQuery = MorphiaHelper.getDatastore().createQuery(FlickrUser.class)
          .field("processed").equal(false).asList();

      LOGGER.info("Flickr user photos bot started. Number of users to process: {}",
          userQuery.size());

      savePhotosForUsers(userQuery);

      LOGGER.info("Flickr photo bot processing finished. Time taken: {} {}",
          (System.currentTimeMillis() - startProcess) / 1000, " seconds");
    }
  }

  private void resetRateLimitIfNeeded(long startProcess) {
    // reset the counter if time has lapsed for more than an hour.
    if (startProcess - start > 3600000) {
      start = startProcess;
      count.set(0);
    }
  }

  private void savePhotosForUsers(List<FlickrUser> users) throws FlickrException {
    running = true;
    usersLoop: for (FlickrUser user : users) {
      try {
        savePhotosForUser(user);
      } catch (FlickrException e) {
        LOGGER.error("Error occurred while processing user: {}", user.getId(), e);
        throw e;
      }
    }
  }

  private void savePhotosForUser(FlickrUser user) throws FlickrException {
    while (!user.isProcessed() && count.get() < 3600) {
      count.addAndGet(1);

      PhotoList<Photo> photos = null;
      try {
        photos = peopleInterface.getPhotos(user.getId(), null, null, null, null, null, null, null,
            Sets.newHashSet("date_taken", "geo"), 1000, user.getPageNumber());
      } catch (FlickrException e) {
        if (e.getErrorMessage().contains("User deleted")) {
          LOGGER.error("Deleted user", e);
          user.setProcessed(true);
          user.update();
        }
        throw e;
      }


      updateUserInformation(user, photos);

      savePhotos(photos, user);
    }

    // if we have exceeded rate limit, we set flag to stop.
    if (!(count.get() < 3600)) {
      running = false;
    }
  }

  private void savePhotos(PhotoList<Photo> photos, FlickrUser user) {
    List<Photo> photosToSave =
        photos.stream().filter(photo -> usePhoto(photo, user)).collect(Collectors.toList());

    photosToSave.stream().map(photo -> new Status(photo)).forEach(s -> s.save());
  }

  private boolean usePhoto(Photo photo, FlickrUser user) {
    return photo.getGeoData() != null && isInBox(photo, user);
  }

  private boolean isInBox(Photo photo, FlickrUser user) {
    final GeoLocation location =
        new GeoLocation(photo.getGeoData().getLatitude(), photo.getGeoData().getLongitude());

    boolean isInBox = false;

    if (!user.getBoundingBoxes().isEmpty()) {
      for (BoundingBox box : user.getBoundingBoxes()) {
        if (box.isLocationInBox(location)) {
          isInBox = true;
          break;
        }
      }
    } else {
      isInBox = defaultBox.isLocationInBox(location);
    }
    return isInBox;
  }

  private void updateUserInformation(FlickrUser user, PhotoList<Photo> photos) {
    if (user.getPageNumber() < photos.getPages()) {
      user.incrementPageNumber();
    } else {
      user.setProcessed(true);
    }

    user.update();
  }
}
