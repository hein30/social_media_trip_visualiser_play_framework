package models.tweets;

import models.trip.GeoLocation;

/**
 * Created by Hein Min Htike on 6/26/2017.
 */
public class BoundingBox {

  private GeoLocation southWest;
  private GeoLocation northEast;

  public BoundingBox() {
    // for morphia
  }

  public BoundingBox(double southWestLong, double southWestLat, double northEastLong,
      double northEastLat) {
    this.southWest = new GeoLocation(southWestLat, southWestLong);
    this.northEast = new GeoLocation(northEastLat, northEastLong);
  }

  public boolean isLocationInBox(GeoLocation location) {
    boolean isLatitudeInside = southWest.getLatitude() <= location.getLatitude()
        && location.getLatitude() <= northEast.getLatitude();

    boolean isLongitudeInside = southWest.getLongitude() <= location.getLongitude()
        && location.getLongitude() <= northEast.getLongitude();

    return isLatitudeInside && isLongitudeInside;
  }
}
