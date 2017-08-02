package models.graph;

import org.geotools.referencing.GeodeticCalculator;

/**
 * Created by Hein Min Htike on 7/20/2017.
 */
public class Edge {
  private String id;
  private Node from;
  private Node to;
  private int weight;
  private double angle;

  public Edge(Node from, Node to) {
    this.from = from;
    this.to = to;
    this.id = from.getId() + to.getId();
    angle = get180Angle();
  }

  public Node getFrom() {
    return from;
  }

  public void setFrom(Node from) {
    this.from = from;
  }

  public Node getTo() {
    return to;
  }

  public void setTo(Node to) {
    this.to = to;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void increaseEdgeWeight() {
    increaseEdgeWeight(1);
  }

  public void increaseEdgeWeight(int weightToIncrese) {
    this.weight = weight + weightToIncrese;
  }


  private double get180Angle() {
    double azimuth = getAzimuth();
    return azimuth < 0 ? 180 + azimuth : azimuth;
  }

  /**
   * Returns angles between -180 and 180 degrees.
   * 
   * @return
   */
  private double getAzimuth() {
    GeodeticCalculator calculator = new GeodeticCalculator();
    calculator.setStartingGeographicPoint(from.getCenterLocation().getLongitude(),
        from.getCenterLocation().getLatitude());
    calculator.setDestinationGeographicPoint(to.getCenterLocation().getLongitude(),
        to.getCenterLocation().getLatitude());

    return calculator.getAzimuth();
  }

  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }
}
