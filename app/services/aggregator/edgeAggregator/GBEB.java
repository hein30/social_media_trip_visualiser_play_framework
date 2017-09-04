package services.aggregator.edgeAggregator;

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

import models.geography.Grid;
import models.geography.RegionGrid;
import models.graph.Edge;
import models.graph.Node;
import models.trip.GeoLocation;
import play.Logger;
import utils.HaversineCalculator;

/**
 * Geography based edge aggregation
 */
public class GBEB implements EdgeAggregator {

  private static final Logger.ALogger LOGGER = Logger.of(GBEB.class);
  private final Map<String, Edge> edgeMap;
  private Map<String, Node> nodeMap;
  private List<RegionGrid> regionGridList;
  private List<Edge> edges;
  private List<Edge> delaunayEdges;
  private Grid[][] grids;
  private double angularDiffThreshold;
  private java.lang.Runtime runTime;
  private int count;

  public GBEB(List<Edge> edges) {
    runTime = Runtime.getRuntime();
    this.edges = Collections.synchronizedList(edges);
    delaunayEdges = new ArrayList<>();

    angularDiffThreshold = 15;
    nodeMap = new HashMap<>();
    edgeMap = new HashMap<>();
  }

  public GBEB withGrids(Grid[][] grids) {
    this.grids = grids;
    this.regionGridList = new ArrayList<>();
    return this;
  }

  public GBEB withAngularDiffThreshold(double threshold) {
    angularDiffThreshold = threshold;
    return this;
  }

  @Override
  public void process() {
    findIntersectionsAndDominantAngel();
    mergeRegions();
    generateConstraints();
    bundleEdges();
    buildGraph();
  }

  private void buildGraph() {
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


  private void bundleEdges() {
    LOGGER.debug("Edge bundling started.");

    GeometryFactory factory = new GeometryFactory();
    AtomicInteger counter = new AtomicInteger();

    long start = System.currentTimeMillis();

    delaunayEdges.parallelStream()
        .forEach(meshEdge -> generateControlPointAndIntersectWithEdges(factory, counter, meshEdge));

    LOGGER.debug(
        "Edge bundling finished in " + ((System.currentTimeMillis() - start) / 1_000) + "s.");
  }

  private void generateControlPointAndIntersectWithEdges(GeometryFactory factory,
      AtomicInteger counter, Edge meshEdge) {
    List<Coordinate> coordinates = new ArrayList<>();
    List<Edge> intersectingEdges = new ArrayList<>();
    for (Edge edge : edges) {
      Geometry intersections =
          meshEdge.getLineString(factory).intersection(edge.getLineString(factory));

      if (intersections.getCoordinates().length == 1) {
        coordinates.add(intersections.getCoordinates()[0]);
        intersectingEdges.add(edge);
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

  private void generateConstraints() {
    LOGGER.debug("Constrained-Delaunay triangulation started.");
    long start = System.currentTimeMillis();


    regionGridList.parallelStream().forEach(RegionGrid::generateMidPointAndIntersections);

    ConstrainedMesh constrainedMesh = new ConstrainedMesh();

    regionGridList.stream().forEach(regionGrid -> {
      try {
        // constrainedMesh.addConstraintEdge(buildDelaunayEdge(regionGrid));
        constrainedMesh.addPoint(buildDelaunayEdge(regionGrid).getStartPoint());
        constrainedMesh.addPoint(buildDelaunayEdge(regionGrid).getEndPoint());
      } catch (DelaunayError delaunayError) {
        delaunayError.printStackTrace();
        LOGGER.error("Failed to add constraint for a region.", delaunayError);
      }
    });

    try {
      constrainedMesh.forceConstraintIntegrity();
      constrainedMesh.processDelaunay();
    } catch (DelaunayError delaunayError) {
      LOGGER.error("Failed to perform Constrained-Delaunay triangulation.", delaunayError);
    }

    List<DPoint> meshPoints = new ArrayList<>();
    List<Node> meshNodes = new ArrayList<>();

    for (DPoint p : constrainedMesh.getPoints()) {
      meshPoints.add(p);
      meshNodes.add(new Node(new GeoLocation(p.getY(), p.getX()), "meshNode: " + meshNodes.size()));
    }

    for (DEdge e : constrainedMesh.getEdges()) {
      Node startNode = meshNodes.get(meshPoints.indexOf(e.getStartPoint()));
      Node endNode = meshNodes.get(meshPoints.indexOf(e.getEndPoint()));
      delaunayEdges.add(new Edge(startNode, endNode));
    }

    LOGGER.debug("Constrained-Delaunay triangulation finished in "
        + (System.currentTimeMillis() - start) + "ms.");
  }

  private DEdge buildDelaunayEdge(RegionGrid regionGrid) throws DelaunayError {
    return new DEdge(
        new DPoint(regionGrid.getPlus90Intersection().x, regionGrid.getPlus90Intersection().y, 0),
        new DPoint(regionGrid.getMinus90Intersection().x, regionGrid.getMinus90Intersection().y,
            0));
  }

  private void mergeRegions() {
    LOGGER.debug("Regions merging started.");

    long start = System.currentTimeMillis();

    for (int row = 0; row < grids[0].length; row++) {
      for (int col = 0; col < grids[0].length; col++) {

        Grid currentGrid = grids[row][col];

        if (!currentGrid.isMerged() && currentGrid.getDominantAngle().isPresent()) {
          RegionGrid regionGrid = new RegionGrid();
          regionGridList.add(regionGrid);

          mergeNeiboringGrids(row, col, currentGrid, regionGrid);
        }
      }
    }

    LOGGER.debug("Regions merging finished in " + ((System.currentTimeMillis() - start)) + "ms.");
  }

  private void mergeNeiboringGrids(int row, int col, Grid currentGrid, RegionGrid regionGrid) {

    currentGrid.setMerged(true);
    regionGrid.addGrid(currentGrid);

    Grid rightGrid = col < grids[0].length - 1 ? grids[row][col + 1] : null;
    Grid bottomGrid = row < grids[0].length - 1 ? grids[row + 1][col] : null;

    Grid leftGrid = col == 0 ? null : grids[row][col - 1];
    Grid topGrid = row == 0 ? null : grids[row - 1][col];

    if (shouldMerge(currentGrid, rightGrid)) {
      mergeNeiboringGrids(row, col + 1, rightGrid, regionGrid);
    }

    if (shouldMerge(currentGrid, bottomGrid)) {
      mergeNeiboringGrids(row + 1, col, bottomGrid, regionGrid);
    }

    if (shouldMerge(currentGrid, leftGrid)) {
      mergeNeiboringGrids(row, col - 1, leftGrid, regionGrid);
    }

    if (shouldMerge(currentGrid, topGrid)) {
      mergeNeiboringGrids(row - 1, col, topGrid, regionGrid);
    }
  }

  private boolean shouldMerge(Grid currentGrid, Grid gridToMerge) {
    return gridToMerge != null && gridToMerge.getDominantAngle().isPresent()
        && Math.abs(gridToMerge.getDominantAngle().get()
            - currentGrid.getDominantAngle().get()) <= angularDiffThreshold
        && !gridToMerge.isMerged();
  }

  private void findIntersectionsAndDominantAngel() {
    LOGGER.debug("Grid dominant angle calculation started.");
    long start = System.currentTimeMillis();

    DominantAngleCalculator dominantAngleCalculator = new DominantAngleCalculator(grids, edges);
    dominantAngleCalculator.calculate();

    LOGGER.debug("Grid dominant angle calculation finished in "
        + ((System.currentTimeMillis() - start) / 1_000) + "s.");
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

}
