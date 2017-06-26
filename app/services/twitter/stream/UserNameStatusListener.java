package services.twitter.stream;

import java.util.List;

import com.typesafe.config.ConfigFactory;

import models.tweets.TwitterUser;
import play.Logger;
import play.libs.Json;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * Basic implementation of @link {@link StatusListener}.
 *
 * Simply saves {@link models.tweets.TwitterUser} into database if the users' tweets have geo
 * location attached.
 * 
 * @author Hein Min Htike
 */
public class UserNameStatusListener implements StatusListener {

  private static final Logger.ALogger LOGGER = Logger.of(UserNameStatusListener.class);

  private static final List<Double> POINTS_LIST =
      ConfigFactory.load().getDoubleList("twitter.filter.coordinates");

  @Override
  public void onStatus(Status status) {

    if (status.getGeoLocation() != null) {
      TwitterUser user = new TwitterUser(status, POINTS_LIST);
      user.save();
    }
  }

  @Override
  public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    LOGGER.info("Received deletion notice: {}" + Json.toJson(statusDeletionNotice));
  }

  @Override
  public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    LOGGER.info("Received on track limitation notice: {}", Json.toJson(numberOfLimitedStatuses));
  }

  @Override
  public void onScrubGeo(long userId, long upToStatusId) {
    LOGGER.info("Received onScrubGeo alert for id: {}, upToStatusId: {}", userId, upToStatusId);
  }

  @Override
  public void onStallWarning(StallWarning warning) {
    LOGGER.info("Stall warning notice: {}", Json.toJson(warning));
  }

  @Override
  public void onException(Exception ex) {
    LOGGER.error("Received exception: ", ex);
  }
}
