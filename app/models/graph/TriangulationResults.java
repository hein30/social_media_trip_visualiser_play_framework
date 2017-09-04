package models.graph;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.geography.RegionGrid;
import services.aggregator.edgeAggregator.GBEB;

public class TriangulationResults {

  private final List<RegionGrid> regionGridList;
  private final List<Edge> constrainedEdgeList;
  private final List<LightWeightEdge> graphResult;
  private final Map<String, LightWeightNode> nodeMap;


  public TriangulationResults(GBEB aggregator) {
    regionGridList = aggregator.getRegionGridList();
    constrainedEdgeList = aggregator.getDelaunayEdges();

    graphResult = aggregator.getEdgeMap().values().stream().map(Edge::lightWeightEdge)
        .collect(Collectors.toList());

    nodeMap = aggregator.getNodeMap().values().stream().map(Node::lightWeightNode)
        .collect(Collectors.toMap(LightWeightNode::getId, n -> n));
  }

  public List<RegionGrid> getRegionGridList() {
    return regionGridList;
  }

  public List<Edge> getConstrainedEdgeList() {
    return constrainedEdgeList;
  }

  public List<LightWeightEdge> getGraphResult() {
    return graphResult;
  }

  public Map<String, LightWeightNode> getNodeMap() {
    return nodeMap;
  }
}
