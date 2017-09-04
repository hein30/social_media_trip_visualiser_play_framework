package models.trip;

import org.mongodb.morphia.annotations.Entity;

import models.geography.Area;
import models.tweets.Status;
import mongo.MorphiaHelper;

/**
 * Created by Hein Min Htike on 6/25/2017.
 */
@Entity(value = "twitterTrips")
public class TwitterTrip extends Trip {
  static {
    MorphiaHelper.ensureIndex(TwitterTrip.class);
  }
  private Status startStatus;
  private Status endStatus;

  public TwitterTrip() {
    super();
  }

  public TwitterTrip(GeoLocation start, GeoLocation end) {
    super();

    setStartPoint(start);
    setEndPoint(end);
  }

  public TwitterTrip(Status startStatus, Status endStatus, double distance) {
    super();

    this.startStatus = startStatus;
    this.endStatus = endStatus;

    setId(startStatus.getId() + " " + endStatus.getId());
    setStartPoint(startStatus.getGeoLocation());
    setEndPoint(endStatus.getGeoLocation());
    setDistanceInMeter(distance);
    setArea(Area.getAreaForLocation(startStatus.getGeoLocation()));
    setSource(startStatus.getSource());
  }

  @Override
  public boolean existsInDatabase() {
    return MorphiaHelper.getDatastore().get(TwitterTrip.class, getId()) != null;
  }

  public Status getStartStatus() {
    return startStatus;
  }

  public void setStartStatus(Status startStatus) {
    this.startStatus = startStatus;
  }

  public Status getEndStatus() {
    return endStatus;
  }

  public void setEndStatus(Status endStatus) {
    this.endStatus = endStatus;
  }
}
