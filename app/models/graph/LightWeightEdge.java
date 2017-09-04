package models.graph;

/**
 * Only contains minimum information
 */
public class LightWeightEdge {
  private String id;
  private String nodeIdFrom;
  private String nodeIdTo;
  private int weight;

  public LightWeightEdge(Edge edge) {
    this.id = edge.getId();
    this.nodeIdFrom = edge.getNodeIdFrom();
    this.nodeIdTo = edge.getNodeIdTo();
    this.weight = edge.getWeight();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNodeIdFrom() {
    return nodeIdFrom;
  }

  public void setNodeIdFrom(String nodeIdFrom) {
    this.nodeIdFrom = nodeIdFrom;
  }

  public String getNodeIdTo() {
    return nodeIdTo;
  }

  public void setNodeIdTo(String nodeIdTo) {
    this.nodeIdTo = nodeIdTo;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }
}
