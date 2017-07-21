package models.geography;

import java.awt.geom.Point2D;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;

import models.trip.GeoLocation;

public class Grid {

  private String id;
  private BoundingBox boundingBox;
  private GeoLocation midPoint;

  public Grid(String id, BoundingBox boundingBox) {
    this.id = id;
    this.boundingBox = boundingBox;

    this.midPoint = calculateMidpoint(boundingBox);
  }

  private GeoLocation calculateMidpoint(BoundingBox boundingBox) {
    GeodeticCalculator calculator = new GeodeticCalculator();
    calculator.setStartingGeographicPoint(boundingBox.getSouthWest().getLongitude(),
        boundingBox.getSouthWest().getLatitude());
    calculator.setDestinationGeographicPoint(boundingBox.getNorthEast().getLongitude(),
        boundingBox.getNorthEast().getLatitude());

    final List<Point2D> geodeticPath = calculator.getGeodeticPath(1);
    return new GeoLocation(geodeticPath.get(1).getY(), geodeticPath.get(1).getX());
  }

  public BoundingBox getBoundingBox() {
    return boundingBox;
  }

  public void setBoundingBox(BoundingBox boundingBox) {
    this.boundingBox = boundingBox;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GeoLocation getMidPoint() {
    return midPoint;
  }

  public boolean isPointInside(GeoLocation location) {
    return this.boundingBox.isLocationInBox(location);
  }
}
