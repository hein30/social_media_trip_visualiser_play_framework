package models.tweets;

import models.trip.GeoLocation;

/**
 * Created by Hein Min Htike on 6/26/2017.
 */
public class BoundingBox {

  private GeoLocation southWest;
  private GeoLocation northEast;
  private GeoLocation southEast;
  private GeoLocation northWest;

  public BoundingBox() {
    // for morphia
  }

  public BoundingBox(double southWestLong, double southWestLat, double northEastLong,
      double northEastLat) {
    this.southWest = new GeoLocation(southWestLat, southWestLong);
    this.northEast = new GeoLocation(northEastLat, northEastLong);
    this.northWest = new GeoLocation(northEastLat, southWestLong);
    this.southEast = new GeoLocation(southWestLat, northEastLong);
  }

  public boolean isLocationInBox(GeoLocation location) {
    boolean isLatitudeInside = southWest.getLatitude() <= location.getLatitude()
        && location.getLatitude() <= northEast.getLatitude();

    boolean isLongitudeInside = southWest.getLongitude() <= location.getLongitude()
        && location.getLongitude() <= northEast.getLongitude();

    return isLatitudeInside && isLongitudeInside;
  }

  public GeoLocation getSouthWest() {
    return southWest;
  }

  public void setSouthWest(GeoLocation southWest) {
    this.southWest = southWest;
  }

  public GeoLocation getNorthEast() {
    return northEast;
  }

  public void setNorthEast(GeoLocation northEast) {
    this.northEast = northEast;
  }

  public GeoLocation getSouthEast() {
    return southEast;
  }

  public void setSouthEast(GeoLocation southEast) {
    this.southEast = southEast;
  }

  public GeoLocation getNorthWest() {
    return northWest;
  }

  public void setNorthWest(GeoLocation northWest) {
    this.northWest = northWest;
  }
}
