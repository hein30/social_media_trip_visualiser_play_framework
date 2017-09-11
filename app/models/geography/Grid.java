package models.geography;

import java.util.Optional;

import models.graph.Edge;
import models.trip.GeoLocation;
import play.libs.Json;
import utils.GeoCalculationHelper;

public class Grid {

  private String id;
  private BoundingBox boundingBox;
  private GeoLocation midPoint;
  private Optional<Double> dominantAngel;
  private boolean merged;

  public Grid() {
    // default constructor for json parser
  }

  public Grid(String id, BoundingBox boundingBox) {
    this.id = id;
    this.boundingBox = boundingBox;

    this.midPoint = GeoCalculationHelper.calculateMidpoint(boundingBox);
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

  public Optional<Double> getDominantAngle() {
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

  public Grid clone() {
    Grid temp = Json.fromJson(Json.toJson(this), this.getClass());
    temp.setDominantAngel(this.dominantAngel);
    return temp;
  }
}
