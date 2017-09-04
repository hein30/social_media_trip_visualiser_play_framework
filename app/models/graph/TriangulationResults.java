package models.graph;

import java.util.ArrayList;
import java.util.List;

import models.geography.RegionGrid;
import services.aggregator.edgeAggregator.GBEB;

public class TriangulationResults {

  private final List<RegionGrid> regionGridList;
  private final List<Edge> constrainedEdgeList;
  private final List<Edge> graphResult;


  public TriangulationResults(GBEB aggregator) {
    regionGridList = aggregator.getRegionGridList();
    constrainedEdgeList = aggregator.getDelaunayEdges();
    graphResult = new ArrayList<>(aggregator.getEdgeMap().values());
  }

  public List<RegionGrid> getRegionGridList() {
    return regionGridList;
  }

  public List<Edge> getConstrainedEdgeList() {
    return constrainedEdgeList;
  }

  public List<Edge> getGraphResult() {
    return graphResult;
  }
}
