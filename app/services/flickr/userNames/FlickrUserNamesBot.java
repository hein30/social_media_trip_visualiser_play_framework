package services.flickr.userNames;

import static com.flickr4java.flickr.photos.SearchParameters.DATE_POSTED_ASC;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;

import models.footprints.FlickrUserNamesBotFootPrint;
import models.tweets.FlickrUser;
import mongo.MorphiaHelper;
import services.flickr.FlickrBot;

/**
 * Bot to scrap flickr user names.
 */
public class FlickrUserNamesBot extends FlickrBot {

  private final PhotosInterface photosInterface;
  private final SearchParameters parameters;
  private final List<Double> coordinates;

  public FlickrUserNamesBot() {
    photosInterface = buildFlickr().getPhotosInterface();

    coordinates = coordinates();
    parameters = new SearchParameters();

    // bounding box to london
    parameters.setBBox(Double.toString(coordinates.get(0)), Double.toString(coordinates.get(1)),
        Double.toString(coordinates.get(2)), Double.toString(coordinates.get(3)));

    parameters.setSort(DATE_POSTED_ASC);
  }

  public static void main(String args[]) throws FlickrException {
    FlickrUserNamesBot bot = new FlickrUserNamesBot();
    bot.getPhotos();
  }

  public PhotoList<Photo> searchPhotos(SearchParameters params, int perPage, int page)
      throws FlickrException {
    PhotoList<Photo> photos = photosInterface.search(params, perPage, page);
    return photos;
  }

  public void getPhotos() throws FlickrException {

    FlickrUserNamesBotFootPrint footPrint = getFlickrUserNamesBotFootPrint();

    // only search if the current search is before today.
    if (footPrint.getMinUploadDate().before(new Date())) {
      parameters.setMinUploadDate(footPrint.getMinUploadDate());
      parameters.setMaxUploadDate(footPrint.getMaxUploadDate());

      PhotoList<Photo> photos = searchPhotos(parameters, 0, footPrint.getPage());

      final Map<String, List<Photo>> map =
          photos.stream().collect(Collectors.groupingBy(photo -> photo.getOwner().getId()));
      map.keySet().forEach(key -> {
        new FlickrUser(key, coordinates).save();
      });

      updateFootPrint(footPrint, photos.getPages());
    }
  }

  private void updateFootPrint(FlickrUserNamesBotFootPrint footPrint, int maxPages) {
    // if footprint's page is larger the total page number, we search for new date.
    if (maxPages > footPrint.getPage()) {
      footPrint.incrementPageNumber();
    } else {
      footPrint.increamentDate();
    }

    footPrint.save();
  }

  private FlickrUserNamesBotFootPrint getFlickrUserNamesBotFootPrint() {
    FlickrUserNamesBotFootPrint footPrint =
        MorphiaHelper.getDatastore().createQuery(FlickrUserNamesBotFootPrint.class).get();

    if (footPrint == null) {
      footPrint = new FlickrUserNamesBotFootPrint();
    }
    return footPrint;
  }
}
