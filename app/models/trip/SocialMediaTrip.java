package models.trip;

import org.mongodb.morphia.annotations.Entity;

import models.geography.Area;
import models.socialmedia.Status;
import mongo.MorphiaHelper;
import utils.CoordinateRandomiser;

/**
 * Created by Hein Min Htike on 6/25/2017.
 */
@Entity(value = "socialMediaTrips")
public class SocialMediaTrip extends Trip {
  static {
    MorphiaHelper.ensureIndex(SocialMediaTrip.class);
  }
  private Status startStatus;
  private Status endStatus;

  public SocialMediaTrip() {
    super();
  }

  public SocialMediaTrip(GeoLocation start, GeoLocation end) {
    super();

    setStartPoint(start);
    setEndPoint(end);
  }

  public SocialMediaTrip(Status startStatus, Status endStatus, double distance) {
    super();

    this.startStatus = startStatus;
    this.endStatus = endStatus;

    setId(startStatus.getId() + " " + endStatus.getId());
    setStartPoint(randomiseCoordinate(startStatus.getGeoLocation()));
    setEndPoint(randomiseCoordinate(endStatus.getGeoLocation()));
    setDistanceInMeter();
    setArea(Area.getAreaForLocation(startStatus.getGeoLocation()));
    setSource(startStatus.getSource());
  }

  private GeoLocation randomiseCoordinate(GeoLocation original) {
    return CoordinateRandomiser.randomise(original, 300, 40);
  }

  @Override
  public boolean existsInDatabase() {
    return MorphiaHelper.getDatastore().get(SocialMediaTrip.class, getId()) != null;
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
