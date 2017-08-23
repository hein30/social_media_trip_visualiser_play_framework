package models.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

/**
 * Created by Hein Min Htike on 7/20/2017.
 */
public class Edge {

  private LineString lineString;
  private String id;
  private Node from;
  private Node to;
  private int weight;
  private double angle;
  private List<Node> subNodes;

  public Edge(Node from, Node to) {
    this.from = from;
    this.to = to;
    this.id = from.getId() + to.getId();
    angle = get180Angle();

    subNodes = Collections.synchronizedList(new ArrayList<>());
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

  public List<Node> getSubNodes() {
    return subNodes;
  }

  public void setSubNodes(List<Node> subNodes) {
    this.subNodes = subNodes;
  }


  public LineString getLineString(GeometryFactory factory) {
    if (lineString == null) {
      lineString =
          factory.createLineString(new Coordinate[] {this.from.getCenterLocation().getCoordinate(),
              this.to.getCenterLocation().getCoordinate()});
    }

    return lineString;
  }
}
