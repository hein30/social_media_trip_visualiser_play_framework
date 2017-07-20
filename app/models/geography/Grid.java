package models.geography;

public class Grid {

  private BoundingBox boundingBox;
  private int numNodes;

  public Grid(BoundingBox boundingBox) {
    this.boundingBox = boundingBox;
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
}
