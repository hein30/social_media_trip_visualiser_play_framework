package services.aggregator.edgeAggregator.gbeb;

import java.util.ArrayList;
import java.util.List;

import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;

import models.geography.Grid;
import models.geography.RegionGrid;
import models.graph.Edge;
import models.graph.Node;
import models.trip.GeoLocation;
import play.Logger;
import services.aggregator.edgeAggregator.BundlingParameters;

public class AdvancedGBEB extends GBEB {
  private static final Logger.ALogger LOGGER = Logger.of(AdvancedGBEB.class);

  private Grid[][] grids;


  public AdvancedGBEB(List<Edge> edges) {
    super(edges);
  }

  public AdvancedGBEB withGrids(Grid[][] grids) {
    this.grids = grids;
    return this;
  }

  public AdvancedGBEB withParameters(BundlingParameters parameters) {
    super.setParameters(parameters);
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

  private void findIntersectionsAndDominantAngel() {
    LOGGER.debug("Grid dominant angle calculation started.");
    long start = System.currentTimeMillis();

    DominantAngleCalculator dominantAngleCalculator =
        new DominantAngleCalculator(grids, getEdges(), getParameters());
    grids = dominantAngleCalculator.calculate();

    LOGGER.debug("Grid dominant angle calculation finished in "
        + ((System.currentTimeMillis() - start) / 1_000) + "s.");
  }

  private void mergeRegions() {
    LOGGER.debug("Regions merging started.");

    long start = System.currentTimeMillis();

    RegionMerger regionMerger = new SimpleRegionMerger(grids, getParameters());
    setRegionGridList(regionMerger.mergeRegions());

    LOGGER.debug("Regions merging finished in " + ((System.currentTimeMillis() - start)) + "ms.");
  }

  private void generateConstraints() {
    LOGGER.debug("Constrained-Delaunay triangulation started.");
    long start = System.currentTimeMillis();


    getRegionGridList().parallelStream().forEach(RegionGrid::generateMidPointAndIntersections);

    ConstrainedMesh constrainedMesh = new ConstrainedMesh();

    // add points as input
    getRegionGridList().stream().forEach(regionGrid -> {
      try {
        constrainedMesh.addPoint(buildDelaunayStartPoint(regionGrid));
        constrainedMesh.addPoint(buildDelaunayEndpoint(regionGrid));
      } catch (DelaunayError delaunayError) {
        LOGGER.error("Failed to add constraint for a region.", delaunayError);
      }
    });

    generateConstrainedMesh(constrainedMesh);

    processDelaunayMesh(constrainedMesh);

    LOGGER.debug("Constrained-Delaunay triangulation finished in "
        + (System.currentTimeMillis() - start) + "ms.");
  }

  private DPoint buildDelaunayStartPoint(RegionGrid regionGrid) throws DelaunayError {
    return new DPoint(regionGrid.getPlus90Intersection().x, regionGrid.getPlus90Intersection().y,
        0);
  }

  private DPoint buildDelaunayEndpoint(RegionGrid regionGrid) throws DelaunayError {
    return new DPoint(regionGrid.getMinus90Intersection().x, regionGrid.getMinus90Intersection().y,
        0);
  }
}
