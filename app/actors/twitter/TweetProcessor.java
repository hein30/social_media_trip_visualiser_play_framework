package actors.twitter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.mongodb.morphia.query.Query;

import models.trip.GeoLocation;
import models.trip.Trip;
import models.trip.TwitterTrip;
import models.tweets.Status;
import mongo.MorphiaHelper;
import play.Logger;
import utils.HaversineCalculator;

/**
 * Created by Hein Min Htike on 6/25/2017.
 */
class TweetProcessor {
  private static final Logger.ALogger LOGGER = Logger.of(TweetProcessor.class);

  public static void main(String args[]) {

    LOGGER.info("Tweets processing started.");

    long timeStarted = System.currentTimeMillis();

    List<Status> statusList = createTwitterStatusQuery().asList();
    processTweets(statusList);

    long timeEnd = System.currentTimeMillis();
    LOGGER.info("Time Taken: " + (timeEnd - timeStarted) / 1000 + " seconds.");
  }

  private static void processTweets(List<Status> statusList) {
    Map<String, List<Status>> userNameAndTweetsMap = groupTweetsByUser(statusList);

    userNameAndTweetsMap.entrySet().forEach(entry -> processEachUser(entry));
  }

  private static Map<String, List<Status>> groupTweetsByUser(List<Status> statusList) {
    return statusList.stream().collect(Collectors.groupingBy(Status::getScreenName)).entrySet()
        .stream().filter(map -> map.getValue().size() > 1)
        .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
  }

  private static Query<Status> createTwitterStatusQuery() {
    Query<Status> statusQuery = MorphiaHelper.getDatastore().createQuery(Status.class);

    statusQuery.or(statusQuery.criteria("endPointUsed").notEqual(true),
        statusQuery.criteria("startPointUsed").notEqual(true));

    statusQuery.order("createdAt");
    return statusQuery;
  }

  private static void processEachUser(Map.Entry<String, List<Status>> oneMapEntry) {
    // TODO create user before

    for (int i = 0; i < oneMapEntry.getValue().size() - 2; i++) {
      processAPair(oneMapEntry.getValue().get(i), oneMapEntry.getValue().get(i + 1));
    }

    // TODO save user
  }

  private static void processAPair(Status start, Status end) {
    boolean validStatues = !isAnyStatusUsedBefore(start, end);
    boolean validTimeWindow = isValidTimeWindow(start, end);

    if (validStatues && validTimeWindow) {
      final double distance = distanceInMeter(start.getGeoLocation(), end.getGeoLocation());
      if (distance > 100) {
        updateStatuses(start, end);

        Trip trip = new TwitterTrip(start, end, distance);
        trip.save();
      } else {
        updateStatuses(start, end);
      }
    }
  }

  private static void updateStatuses(Status start, Status end) {
    start.setStartPointUsed(true);
    end.setEndPointUsed(true);

    start.save();
    end.save();
  }

  private static double distanceInMeter(GeoLocation startLocation, GeoLocation endLocation) {
    return HaversineCalculator.getHaverSineDistanceInMeter(startLocation, endLocation);
  }

  private static boolean isAnyStatusUsedBefore(Status start, Status end) {
    return start.isStartPointUsed() || end.isEndPointUsed();
  }

  private static boolean isValidTimeWindow(Status start, Status end) {
    Date validEndDate = DateUtils.addHours(start.getCreatedAt(), 24);
    return end.getCreatedAt().before(validEndDate);
  }

  public void processTweets() {
    TweetProcessor.main(new String[] {});
  }
}
