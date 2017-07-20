package services.aggregator.nodeAggregator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import models.geography.Grid;
import models.graph.Edge;
import models.graph.Node;
import models.trip.GeoLocation;
import models.trip.Trip;
import play.Logger;

/**
 * Agreegate nodes based on grids.
 */
public class NodeAggregator {
  private static final Logger.ALogger LOGGER = Logger.of(NodeAggregator.class);


  public Map<String, Edge> aggregateNodes(List<Grid> grids, List<? extends Trip> trips) {

    Map<String, Node> nodeMap = new HashMap<>();
    Map<String, Edge> edgeMap = new HashMap<>();
    for (Trip trip : trips) {

      Optional<Node> startNodeOptional = getNodeForLocation(grids, trip.getStartPoint());
      Optional<Node> endNodeOptional = getNodeForLocation(grids, trip.getEndPoint());

      if (startNodeOptional.isPresent() && !endNodeOptional.isPresent()) {
        Node startNode = startNodeOptional.get();
        Node endNode = endNodeOptional.get();

        nodeMap.computeIfAbsent(startNode.getId(), zz -> startNode)
            .addStartPoint(trip.getStartPoint());
        nodeMap.computeIfAbsent(endNode.getId(), a -> endNode).addEndPoint(trip.getEndPoint());

        Edge edge = new Edge(nodeMap.get(startNode.getId()), nodeMap.get(endNode.getId()));
        edgeMap.computeIfAbsent(edge.getId(), a -> edge).increseEdgeWeight();

      } else {
        LOGGER.error("Out of bound for location.");
      }
    }

    return edgeMap;
  }

  private Optional<Node> getNodeForLocation(List<Grid> grids, GeoLocation location) {

    for (Grid grid : grids) {
      if (grid.getBoundingBox().isLocationInBox(location)) {
        return Optional.of(new Node(grid));
      }
    }
    return Optional.empty();
  }
}
