package models.geography;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.geotools.referencing.GeodeticCalculator;

import models.graph.Edge;
import models.trip.GeoLocation;

public class Grid {

  private String id;
  private BoundingBox boundingBox;
  private GeoLocation midPoint;
  private Map<Double, Integer> angles;
  private Optional<Double> dominantAngel;
  private boolean merged;

  public Grid(String id, BoundingBox boundingBox) {
    this.id = id;
    this.boundingBox = boundingBox;

    this.midPoint = calculateMidpoint(boundingBox);
    angles = new HashMap<>();
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

  public boolean crossBoundary(Edge edge) {
    return this.boundingBox.crossBoundary(edge);
  }

  public Map<Double, Integer> getAngles() {
    return angles;
  }

  public void setAngles(Map<Double, Integer> angles) {
    this.angles = angles;
  }

  public void addAngle(double angle, int weight) {

    if (angles.containsKey(angle)) {
      angles.put(angle, angles.get(angle) + weight);
    } else {
      angles.put(angle, weight);
    }
  }

  public Optional<Double> getDominantAngel() {
    return dominantAngel;
  }

  public void setDominantAngel(Optional<Double> dominantAngel) {
    this.dominantAngel = dominantAngel;
  }

  public boolean isMerged() {
    return merged;
  }

  public void setMerged(boolean merged) {
    this.merged = merged;
  }
}
