package services.aggregator.nodeAggregator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import models.geography.BoundingBox;
import models.geography.Grid;
import models.graph.Edge;
import models.graph.Node;
import models.graph.ResultGraph;
import models.trip.GeoLocation;
import models.trip.Trip;
import play.Logger;

/**
 * Aggregate nodes based on grids.
 */
public class NodeAggregator {
  private static final Logger.ALogger LOGGER = Logger.of(NodeAggregator.class);

  public ResultGraph aggregateNodes(BoundingBox boundingBox, int numGrids, boolean extendedBox,
      List<? extends Trip> trips, boolean isDirected) {
    return aggregateNodes(boundingBox.grids(numGrids, extendedBox), trips, isDirected);
  }

  public ResultGraph aggregateNodes(List<Grid> grids, List<? extends Trip> trips,
      boolean isDirected) {

    long start = System.currentTimeMillis();
    LOGGER.info("Node aggregation started.");

    Map<String, Node> nodeMap = new HashMap<>();
    Map<String, Edge> edgeMap = new HashMap<>();
    for (Trip trip : trips) {

      Optional<Node> startNodeOptional = getNodeForLocation(grids, trip.getStartPoint());
      Optional<Node> endNodeOptional = getNodeForLocation(grids, trip.getEndPoint());

      if (startNodeOptional.isPresent() && endNodeOptional.isPresent()
          && !startNodeOptional.get().getId().contentEquals(endNodeOptional.get().getId()))

        updateOrAddEdge(isDirected, nodeMap, edgeMap, trip, startNodeOptional, endNodeOptional);
      else {
        LOGGER.debug("Out of bound for location or Start and end points in same grid.");
      }
    }

    LOGGER.info("Node aggreegation finished in : " + (System.currentTimeMillis() - start) / 1000
        + " seconds.");
    return new ResultGraph(nodeMap, edgeMap, grids);
  }

  private void updateOrAddEdge(boolean isDirected, Map<String, Node> nodeMap,
      Map<String, Edge> edgeMap, Trip trip, Optional<Node> startNodeOptional,
      Optional<Node> endNodeOptional) {

    Node startNode = startNodeOptional.get();
    Node endNode = endNodeOptional.get();

    nodeMap.computeIfAbsent(startNode.getId(), a -> startNode).addStartPoint(trip.getStartPoint());
    nodeMap.computeIfAbsent(endNode.getId(), a -> endNode).addEndPoint(trip.getEndPoint());

    Edge edge = new Edge(nodeMap.get(startNode.getId()), nodeMap.get(endNode.getId()));

    // if this is un-directed graph, and edge does not exists already, we try the reverse direction.
    if (!isDirected && !edgeMap.containsKey(edge.getId())) {
      Edge reverseEdge = new Edge(nodeMap.get(endNode.getId()), nodeMap.get(startNode.getId()));
      edgeMap.computeIfAbsent(reverseEdge.getId(), a -> reverseEdge).increaseEdgeWeight();
    } else {
      edgeMap.computeIfAbsent(edge.getId(), a -> edge).increaseEdgeWeight();
    }
  }

  private Optional<Node> getNodeForLocation(List<Grid> grids, GeoLocation location) {

    for (Grid grid : grids) {
      if (grid.isPointInside(location)) {
        return Optional.of(new Node(grid));
      }
    }
    return Optional.empty();
  }
}
