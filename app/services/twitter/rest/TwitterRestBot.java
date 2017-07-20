package services.twitter.rest;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;

import com.typesafe.config.ConfigFactory;

import models.trip.GeoLocation;
import models.geography.BoundingBox;
import models.tweets.RateLimitException;
import models.tweets.TwitterUser;
import mongo.MorphiaHelper;
import play.Logger;
import services.twitter.TwitterBot;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by Hein Min Htike on 6/23/2017.
 */
class TwitterRestBot extends TwitterBot {

  private static final Logger.ALogger LOGGER = Logger.of(TwitterRestBot.class);
  private static final List<Double> POINTS_LIST =
      ConfigFactory.load().getDoubleList("twitter.filter.coordinates");

  public static final BoundingBox DEFAULT_BOX = new BoundingBox(POINTS_LIST.get(0),
      POINTS_LIST.get(1), POINTS_LIST.get(2), POINTS_LIST.get(3));

  private final Twitter twitter;

  public TwitterRestBot() {
    twitter = new TwitterFactory(buildConfig()).getInstance();
  }

  public static void main(String[] args) {
    TwitterRestBot bot = new TwitterRestBot();
    bot.processUsers();
  }

  public void processUsers() {

    long start = System.currentTimeMillis();

    List<TwitterUser> userQuery = MorphiaHelper.getDatastore().createQuery(TwitterUser.class)
        .field("nextProcessTime").lessThanOrEq(new Date()).asList();

    LOGGER.info("Twitter rest bot process started. Number of users to process: {}",
        userQuery.size());

    saveTweetsForUsers(userQuery);

    LOGGER.info("Twitter rest bot processing finished. Time taken: "
        + (System.currentTimeMillis() - start) / 1000 + " seconds.");
  }

  private void saveTweetsForUsers(List<TwitterUser> users) {

    usersLoop: for (TwitterUser user : users) {
      try {
        saveTweetsForUser(user);
      } catch (TwitterException e) {
        LOGGER.error("Error occurred while saving tweet for user: {}", user.getUserName(), e);
        break usersLoop;
      } catch (RateLimitException e) {
        LOGGER.info("Rate limit reached. Stopping operations.");
        break usersLoop;
      }
    }
  }

  private void saveTweetsForUser(TwitterUser user) throws TwitterException, RateLimitException {
    try {
      ResponseList<Status> allStatuses = getTweets(user);

      int saved = saveTweets(allStatuses);

      updateUser(user, allStatuses);

      LOGGER.debug("Saved {} tweets for user: {}", saved, user.getUserName());
      final RateLimitStatus rateLimitStatus = allStatuses.getRateLimitStatus();
      if (rateLimitStatus != null && rateLimitStatus.getRemaining() < 1) {
        throw new RateLimitException(
            "Rate limit reached. Reset in " + rateLimitStatus.getSecondsUntilReset() + " seconds.");
      }

    } catch (TwitterException e) {
      if (e.getMessage().contains("401") || e.getMessage().contains("404")) {
        LOGGER.error("Error occurred: ", e);
        user.setNextProcessTime(DateUtils.addDays(new Date(), 2));
        user.update();
      } else {
        throw e;
      }
    }
  }

  private void updateUser(TwitterUser user, ResponseList<Status> allStatuses) {
    user.setNextProcessTime(DateUtils.addDays(new Date(), 2));
    if (!allStatuses.isEmpty()){
      user.setLastTweetId(String.valueOf(allStatuses.get(0).getId()));
    }
    user.update();
  }

  private int saveTweets(ResponseList<Status> allStatuses) {
    List<Status> statusesToSave = allStatuses.stream().filter(s -> useTweet(s, new TwitterUser()))
        .collect(Collectors.toList());

    statusesToSave.stream().map(models.tweets.Status::new).collect(Collectors.toList())
        .forEach(s -> s.save());

    return statusesToSave.size();
  }

  private ResponseList<Status> getTweets(TwitterUser user) throws TwitterException {
    Paging paging = new Paging();
    paging.setCount(200); // 200 is maximum number of tweets one can get in a call.

    Optional.ofNullable(user.getLastTweetId())
        .ifPresent(id -> paging.setSinceId(Long.parseLong(id)));
    return twitter.getUserTimeline(user.getUserName(), paging);
  }

  private boolean useTweet(Status status, TwitterUser user) {
    return status.getGeoLocation() != null && isInBox(status, user);
  }

  private boolean isInBox(Status status, TwitterUser user) {
    final GeoLocation location = new GeoLocation(status.getGeoLocation());

    boolean isInBox = false;
    if (!user.getBoundingBoxes().isEmpty()) {
      for (BoundingBox box : user.getBoundingBoxes()) {
        if (box.isLocationInBox(location)) {
          isInBox = true;
          break;
        }
      }
    } else {
      isInBox = DEFAULT_BOX.isLocationInBox(location);
    }
    return isInBox;
  }
}
