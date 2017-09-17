package services.aggregator.edgeAggregator.gbeb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import models.geography.RegionGrid;
import models.graph.Edge;
import models.graph.Node;
import models.trip.GeoLocation;
import play.Logger;
import services.aggregator.edgeAggregator.BundlingParameters;
import services.aggregator.edgeAggregator.EdgeAggregator;
import utils.HaversineCalculator;

/**
 * Geography based edge aggregation
 */
public abstract class GBEB implements EdgeAggregator {

  private static final Logger.ALogger LOGGER = Logger.of(GBEB.class);
  private final Map<String, Edge> edgeMap;
  private Map<String, Node> nodeMap;
  private List<Edge> edges;
  private List<Edge> delaunayEdges;
  private BundlingParameters parameters;
  private List<RegionGrid> regionGridList;


  public GBEB(List<Edge> edges) {
    this.edges = Collections.synchronizedList(edges);
    delaunayEdges = new ArrayList<>();

    nodeMap = new HashMap<>();
    edgeMap = new HashMap<>();
    regionGridList = new ArrayList<>();
  }

  @Override
  public abstract void process();

  protected void bundleEdges() {
    LOGGER.debug("Edge bundling started.");

    GeometryFactory factory = new GeometryFactory();
    AtomicInteger counter = new AtomicInteger();

    long start = System.currentTimeMillis();

    delaunayEdges.parallelStream()
        .forEach(meshEdge -> generateControlPointAndIntersectWithEdges(factory, counter, meshEdge));

    LOGGER.debug(
        "Edge bundling finished in " + ((System.currentTimeMillis() - start) / 1_000) + "s.");
  }

  protected void buildGraph() {
    LOGGER.debug("Graph building started.");
    final long start = System.currentTimeMillis();

    for (Edge edge : edges) {
      nodeMap.computeIfAbsent(edge.getFrom().getId(), a -> edge.getFrom());
      nodeMap.computeIfAbsent(edge.getTo().getId(), a -> edge.getTo());

      String currentNodeId = edge.getFrom().getId();
      List<Node> untraversedNodes = new ArrayList<>(edge.getSubNodes());
      untraversedNodes.add(edge.getTo());

      while (untraversedNodes.size() > 0) {
        final Node currentNode = nodeMap.get(currentNodeId);
        Node shortestDistanceNode = untraversedNodes.stream().reduce((a,
            b) -> HaversineCalculator.getHaverSineDistanceInMeter(currentNode.getCenterLocation(),
                a.getCenterLocation()) < HaversineCalculator.getHaverSineDistanceInMeter(
                    currentNode.getCenterLocation(), b.getCenterLocation()) ? a : b)
            .get();

        nodeMap.computeIfAbsent(shortestDistanceNode.getId(), a -> shortestDistanceNode);

        Edge bundledEdge =
            new Edge(nodeMap.get(currentNodeId), nodeMap.get(shortestDistanceNode.getId()));

        // if the edge map doesn't contain current edge, try the reverse direction.
        if (!edgeMap.containsKey(bundledEdge.getId())) {
          Edge reverseEdge =
              new Edge(nodeMap.get(shortestDistanceNode.getId()), nodeMap.get(currentNodeId));
          edgeMap.computeIfAbsent(reverseEdge.getId(), a -> reverseEdge)
              .increaseEdgeWeight(edge.getWeight());
        } else {
          edgeMap.get(bundledEdge.getId()).increaseEdgeWeight(edge.getWeight());
        }

        currentNodeId = shortestDistanceNode.getId();

        // remove the node from list.
        untraversedNodes.remove(shortestDistanceNode);
      }
    }

    LOGGER.debug(
        "Graph building finished in " + ((System.currentTimeMillis() - start) / 1_000) + "s.");
  }

  private void generateControlPointAndIntersectWithEdges(GeometryFactory factory,
      AtomicInteger counter, Edge meshEdge) {
    List<Coordinate> coordinates = new ArrayList<>();
    List<Edge> intersectingEdges = new ArrayList<>();
    for (Edge edge : edges) {
      try {
        Geometry intersections =
            meshEdge.getLineString(factory).intersection(edge.getLineString(factory));

        if (intersections.getCoordinates().length == 1) {
          coordinates.add(intersections.getCoordinates()[0]);
          intersectingEdges.add(edge);
        }
        //catching NPE, intermittently getting this.
      } catch (RuntimeException e) {
        LOGGER.error("problem while calculating intersection", e);
      }
    }

    double avgX =
        coordinates.stream().mapToDouble(coordinate -> coordinate.x).sum() / coordinates.size();
    double avgY =
        coordinates.stream().mapToDouble(coordinate -> coordinate.y).sum() / coordinates.size();

    Node controlPoint = new Node(new GeoLocation(avgY, avgX), "ctl" + counter.getAndAdd(1));
    meshEdge.getSubNodes().add(controlPoint);
    intersectingEdges.forEach(intersectingEdge -> intersectingEdge.getSubNodes().add(controlPoint));
  }

  /**
   * Generate the mesh using input.
   *
   * @param constrainedMesh
   */
  protected void generateConstrainedMesh(ConstrainedMesh constrainedMesh) {
    try {
      constrainedMesh.forceConstraintIntegrity();
      constrainedMesh.processDelaunay();
    } catch (DelaunayError delaunayError) {
      LOGGER.error("Failed to perform Constrained-Delaunay triangulation.", delaunayError);
    }
  }

  /**
   * Process generated deleauny mesh
   *
   * @param constrainedMesh
   */
  protected void processDelaunayMesh(ConstrainedMesh constrainedMesh) {
    List<DPoint> meshPoints = new ArrayList<>();
    List<Node> meshNodes = new ArrayList<>();

    for (DPoint p : constrainedMesh.getPoints()) {
      meshPoints.add(p);
      meshNodes.add(new Node(new GeoLocation(p.getY(), p.getX()), "meshNode: " + meshNodes.size()));
    }

    for (DEdge e : constrainedMesh.getEdges()) {
      Node startNode = meshNodes.get(meshPoints.indexOf(e.getStartPoint()));
      Node endNode = meshNodes.get(meshPoints.indexOf(e.getEndPoint()));
      getDelaunayEdges().add(new Edge(startNode, endNode));
    }
  }


  public List<RegionGrid> getRegionGridList() {
    return regionGridList;
  }

  public void setRegionGridList(List<RegionGrid> regionGridList) {
    this.regionGridList = regionGridList;
  }

  public List<Edge> getDelaunayEdges() {
    return delaunayEdges;
  }

  public void setDelaunayEdges(List<Edge> delaunayEdges) {
    this.delaunayEdges = delaunayEdges;
  }

  public Map<String, Edge> getEdgeMap() {
    return edgeMap;
  }

  public Map<String, Node> getNodeMap() {
    return nodeMap;
  }

  public void setNodeMap(Map<String, Node> nodeMap) {
    this.nodeMap = nodeMap;
  }

  public BundlingParameters getParameters() {
    return parameters;
  }

  public void setParameters(BundlingParameters parameters) {
    this.parameters = parameters;
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public void setEdges(List<Edge> edges) {
    this.edges = edges;
  }
}
