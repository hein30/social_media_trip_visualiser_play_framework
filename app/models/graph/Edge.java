package models.graph;

/**
 * Created by Hein Min Htike on 7/20/2017.
 */
public class Edge {
  private String id;
  private Node from;
  private Node to;
  private int weight;

  public Edge(Node from, Node to) {
    this.from = from;
    this.to = to;
    this.id = from.getId() + to.getId();
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
}
