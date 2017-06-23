package services.twitter.stream;

import play.Logger;
import play.libs.Json;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * Basic implementation of @link {@link StatusListener}.
 *
 * Simply saves tweets into database if it has geo data.
 * 
 * @author Hein Min Htike
 */
public class PersistingStatusListener implements StatusListener {

  private static final Logger.ALogger LOGGER = Logger.of(PersistingStatusListener.class);

  private int count;

  @Override
  public void onStatus(Status status) {

    if (status.getGeoLocation() != null) {
      models.tweets.Status genericStatus = new models.tweets.Status(status);
      genericStatus.save();
      count++;
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

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
