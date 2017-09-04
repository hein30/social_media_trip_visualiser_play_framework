package models.graph;

import models.trip.GeoLocation;

/**
 * Reprsents a node with bare minimum information.
 */
public class LightWeightNode {

  private String id;
  private GeoLocation centerLocation;

  public LightWeightNode(Node node) {
    this.id = node.getId();
    this.centerLocation = node.getCenterLocation();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GeoLocation getCenterLocation() {
    return centerLocation;
  }

  public void setCenterLocation(GeoLocation centerLocation) {
    this.centerLocation = centerLocation;
  }
}
