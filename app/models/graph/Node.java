package models.graph;

import java.util.ArrayList;
import java.util.List;

import models.geography.Grid;
import models.trip.GeoLocation;

/**
 * Created by Hein Min Htike on 7/20/2017.
 */
public class Node {

  private String id;
  private Grid grid;
  private GeoLocation centerLocation;
  private GeoLocation averageLocation;
  private List<GeoLocation> locations;
  private int incoming;
  private int outgoing;

  public Node(Grid grid) {
    this.grid = grid;
    locations = new ArrayList<>();
    this.id = grid.getId();
    this.centerLocation = grid.getMidPoint();
  }

  public Node(GeoLocation geoLocation, String id) {
    locations = new ArrayList<>();
    this.id = id;
    this.centerLocation = geoLocation;
  }

  public void addStartPoint(GeoLocation start) {
    locations.add(start);
    outgoing++;
  }

  public void addEndPoint(GeoLocation end) {
    locations.add(end);
    incoming++;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Grid getGrid() {
    return grid;
  }

  public void setGrid(Grid grid) {
    this.grid = grid;
  }

  public List<GeoLocation> getLocations() {
    return locations;
  }

  public void setLocations(List<GeoLocation> locations) {
    this.locations = locations;
  }

  public int getIncoming() {
    return incoming;
  }

  public void setIncoming(int incoming) {
    this.incoming = incoming;
  }

  public int getOutgoing() {
    return outgoing;
  }

  public void setOutgoing(int outgoing) {
    this.outgoing = outgoing;
  }

  public GeoLocation getCenterLocation() {
    return centerLocation;
  }

  public void setCenterLocation(GeoLocation centerLocation) {
    this.centerLocation = centerLocation;
  }

  public GeoLocation getAverageLocation() {
    return averageLocation;
  }

  public void setAverageLocation(GeoLocation averageLocation) {
    this.averageLocation = averageLocation;
  }

  public LightWeightNode lightWeightNode() {
    return new LightWeightNode(this);
  }
}
