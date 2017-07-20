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
  private List<GeoLocation> locations;
  private int incoming;
  private int outgoing;

  public Node(Grid grid) {
    this.grid = grid;
    locations = new ArrayList<>();
    setId(grid);
  }

  public void addStartPoint(GeoLocation start) {
    locations.add(start);
    outgoing++;
  }

  public void addEndPoint(GeoLocation end) {
    locations.add(end);
    incoming++;
  }

  private void setId(Grid grid) {
    final GeoLocation southWest = grid.getBoundingBox().getSouthWest();
    final GeoLocation northEast = grid.getBoundingBox().getNorthEast();
    this.id = String.valueOf(southWest.getLatitude()) + String.valueOf(southWest.getLongitude())
        + String.valueOf(northEast.getLatitude()) + String.valueOf(northEast.getLongitude());
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
}
