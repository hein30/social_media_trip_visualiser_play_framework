package models.geography;

import java.awt.geom.Point2D;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;

import models.trip.GeoLocation;

public class Grid {

  private String id;
  private BoundingBox boundingBox;
  private int numNodes;
  private GeoLocation midPoint;

  public Grid(BoundingBox boundingBox) {
    this.boundingBox = boundingBox;

    GeodeticCalculator calculator = new GeodeticCalculator();
    calculator.setStartingGeographicPoint(boundingBox.getSouthWest().getLongitude(),
        boundingBox.getSouthWest().getLatitude());
    calculator.setDestinationGeographicPoint(boundingBox.getNorthEast().getLongitude(),
        boundingBox.getNorthEast().getLatitude());

    final List<Point2D> geodeticPath = calculator.getGeodeticPath(1);
    midPoint = new GeoLocation(geodeticPath.get(1).getY(), geodeticPath.get(1).getX());
  }

  public BoundingBox getBoundingBox() {
    return boundingBox;
  }

  public void setBoundingBox(BoundingBox boundingBox) {
    this.boundingBox = boundingBox;
  }

  public int getNumNodes() {
    return numNodes;
  }

  public void setNumNodes(int numNodes) {
    this.numNodes = numNodes;
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

  public void setMidPoint(GeoLocation midPoint) {
    this.midPoint = midPoint;
  }
}
