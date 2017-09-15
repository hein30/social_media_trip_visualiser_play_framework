package services.aggregator.edgeAggregator.gbeb;


import java.util.ArrayList;
import java.util.List;

import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;

import models.graph.Edge;
import models.graph.Node;
import models.trip.GeoLocation;
import play.Logger;

public class SimpleGBEB extends GBEB {
  private static final Logger.ALogger LOGGER = Logger.of(SimpleGBEB.class);

  private List<Node> nodes;

  public SimpleGBEB(List<Edge> edges) {
    super(edges);
  }

  public SimpleGBEB withNodes(List<Node> nodes) {
    this.nodes = nodes;
    return this;
  }

  @Override
  public void process() {
    generateConstraints();
    bundleEdges();
    buildGraph();
  }

  private void generateConstraints() {
    LOGGER.debug("Constrained-Delaunay triangulation started.");
    long start = System.currentTimeMillis();

    ConstrainedMesh constrainedMesh = new ConstrainedMesh();

    // add points as input
    nodes.forEach(node -> {
      try {
        constrainedMesh.addPoint(buildDelaunayPoint(node.getCenterLocation()));
      } catch (DelaunayError delaunayError) {
        LOGGER.error("Failed to add constraint for a region.", delaunayError);
      }
    });

    generateConstrainedMesh(constrainedMesh);

    processDelaunayMesh(constrainedMesh);

    LOGGER.debug("Constrained-Delaunay triangulation finished in "
        + (System.currentTimeMillis() - start) + "ms.");
  }

  private DPoint buildDelaunayPoint(GeoLocation location) throws DelaunayError {
    return new DPoint(location.getCoordinate().x, location.getCoordinate().y, 0);
  }
}
