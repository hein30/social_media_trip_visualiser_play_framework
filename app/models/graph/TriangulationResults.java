package models.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.geography.RegionGrid;
import services.aggregator.edgeAggregator.GBEB;

public class TriangulationResults {

  private final List<RegionGrid> regionGridList;
  private final List<Edge> constrainedEdgeList;
  private final List<LightWeightEdge> edges;
  private final Map<String, LightWeightNode> nodeMap;


  public TriangulationResults(GBEB aggregator) {
    regionGridList = aggregator.getRegionGridList();
    constrainedEdgeList = aggregator.getDelaunayEdges();

    final Collection<Edge> edgeValues = aggregator.getEdgeMap().values();
    this.edges = lightWeightEdges(edgeValues);

    final Collection<Node> nodeValues = aggregator.getNodeMap().values();
    nodeMap = lightWeightNodes(nodeValues);
  }

  public TriangulationResults(ResultGraph graph) {
    regionGridList = new ArrayList<>();
    constrainedEdgeList = new ArrayList<>();
    nodeMap = lightWeightNodes(graph.getNodeMap().values());
    edges = lightWeightEdges(graph.getEdgeList());
  }

  private Map<String, LightWeightNode> lightWeightNodes(Collection<Node> nodeValues) {
    return nodeValues.stream().map(Node::lightWeightNode)
        .collect(Collectors.toMap(LightWeightNode::getId, n -> n));
  }

  private List<LightWeightEdge> lightWeightEdges(Collection<Edge> values) {
    return values.stream().map(Edge::lightWeightEdge).collect(Collectors.toList());
  }

  public List<RegionGrid> getRegionGridList() {
    return regionGridList;
  }

  public List<Edge> getConstrainedEdgeList() {
    return constrainedEdgeList;
  }


  public Map<String, LightWeightNode> getNodeMap() {
    return nodeMap;
  }

  public List<LightWeightEdge> getEdges() {
    return edges;
  }
}
