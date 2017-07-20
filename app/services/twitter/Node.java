package services.twitter;

import models.trip.GeoLocation;

/**
 * Created by Hein Min Htike on 7/18/2017.
 */
public class Node {

  private String id;
  private double lat;
  private double lon;

  public Node(GeoLocation location) {
    this.id = String.valueOf(location.getLatitude()) + String.valueOf(location.getLongitude());
    this.lat = location.getLatitude();
    this.lon = location.getLongitude();
  };

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }
}
