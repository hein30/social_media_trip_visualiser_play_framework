package models.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.geography.Grid;

/**
 * Created by Hein Min Htike on 7/20/2017.
 */
public class ResultGraph {

  private Map<String, Node> nodeMap;
  private List<Edge> edgeList;
  private List<Grid> grids;
  private int numNodes;
  private int numEdges;

  public ResultGraph(Map<String, Node> nodeMap, Map<String, Edge> edgeMap, List<Grid> grids) {
    this.nodeMap = nodeMap;
    this.edgeList = new ArrayList<>(edgeMap.values());

    this.numNodes = nodeMap.size();
    this.numEdges = edgeList.size();
    this.grids = grids;
  }

  public void stripData() {
    this.nodeMap.entrySet().forEach(entry -> entry.getValue().getLocations().clear());
    this.nodeMap.clear();
  }

  public Map<String, Node> getNodeMap() {
    return nodeMap;
  }

  public void setNodeMap(Map<String, Node> nodeMap) {
    this.nodeMap = nodeMap;
  }

  public List<Edge> getEdgeList() {
    return edgeList;
  }

  public void setEdgeList(List<Edge> edgeList) {
    this.edgeList = edgeList;
  }

  public int getNumNodes() {
    return numNodes;
  }

  public void setNumNodes(int numNodes) {
    this.numNodes = numNodes;
  }

  public int getNumEdges() {
    return numEdges;
  }

  public void setNumEdges(int numEdges) {
    this.numEdges = numEdges;
  }

  public List<Grid> getGrids() {
    return grids;
  }

  public void setGrids(List<Grid> grids) {
    this.grids = grids;
  }
}
