package models.trip;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import models.geography.Area;
import models.socialmedia.Source;
import mongo.MorphiaHelper;
import utils.HaversineCalculator;

/**
 * A data structure representing a trip. To be extended by specific API's trips to include more
 * information.
 *
 * @author Hein Min Htike
 */
@Entity(value = "genericTrips")
public abstract class Trip {

  @Id
  private String id;
  private GeoLocation startPoint;
  private GeoLocation endPoint;
  private double distanceInMeter;
  @Indexed
  private Area area;
  private Source source;

  public Trip() {
    // default constructor.
  }

  public abstract boolean existsInDatabase();

  public void save() {
    if (!existsInDatabase()) {
      MorphiaHelper.getDatastore().save(this);
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GeoLocation getStartPoint() {
    return startPoint;
  }

  public void setStartPoint(GeoLocation startPoint) {
    this.startPoint = startPoint;
  }

  public GeoLocation getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(GeoLocation endPoint) {
    this.endPoint = endPoint;
  }

  public double getDistanceInMeter() {
    return distanceInMeter;
  }

  public void setDistanceInMeter(double distanceInMeter) {
    this.distanceInMeter = distanceInMeter;
  }

  public void setDistanceInMeter() {
    this.distanceInMeter =
        HaversineCalculator.getHaverSineDistanceInMeter(getStartPoint(), getEndPoint());
  }

  public Area getArea() {
    return area;
  }

  public void setArea(Area area) {
    this.area = area;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }
}
