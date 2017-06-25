package models.trip;

/**
 * Created by Hein Min Htike on 6/23/2017.
 */
public class GeoLocation {

  private double latitude;
  private double longitude;

  public GeoLocation() {
    // default constructor
  }

  public GeoLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public GeoLocation(twitter4j.GeoLocation geoLocation) {
    this.latitude = geoLocation.getLatitude();
    this.longitude = geoLocation.getLongitude();
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}
