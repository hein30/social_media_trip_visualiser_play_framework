package services.aggregator.edgeAggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.geography.Grid;
import models.geography.RegionGrid;
import models.graph.Edge;
import play.Logger;
import utils.KdeAngle;

/**
 * Geography based edge aggregation
 */
public class GBEB implements EdgeAggregator {

  private static final Logger.ALogger LOGGER = Logger.of(GBEB.class);

  int mb = 1024 * 1024;
  private List<RegionGrid> regionGridList;
  private List<Edge> edges;
  private Grid[][] grids;
  private double angularDiffThreshold;
  private java.lang.Runtime runTime;
  private int count;

  public GBEB(List<Edge> edges) {
    runTime = Runtime.getRuntime();
    this.edges = edges;
    angularDiffThreshold = 15;
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
  }

  private void mergeRegions() {
    LOGGER.debug("Regions merging started.");

    for (int row = 0; row < grids[0].length; row++) {
      for (int col = 0; col < grids[0].length; col++) {

        Grid currentGrid = grids[row][col];

        if (!currentGrid.isMerged() && currentGrid.getDominantAngel().isPresent()) {
          RegionGrid regionGrid = new RegionGrid();
          regionGridList.add(regionGrid);

          mergeNeiboringGrids(row, col, currentGrid, regionGrid);
        }
      }
    }

    LOGGER.debug("Regions merging finished.");
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
    return gridToMerge != null && gridToMerge.getDominantAngel().isPresent()
        && Math.abs(gridToMerge.getDominantAngel().get()
            - currentGrid.getDominantAngel().get()) <= angularDiffThreshold
        && !gridToMerge.isMerged();
  }

  private void findIntersectionsAndDominantAngel() {
    for (int row = 0; row < grids[0].length; row++) {
      for (int col = 0; col < grids[0].length; col++) {

        if (row ==10 && col ==0){
          System.out.println("Stop here");
        }

        LOGGER.debug("Processing grid: " + row + " - " + col);
        Grid grid = grids[row][col];
        getAllCrossingEdgeAngles(grid);
        final Map<Double, Integer> entries = grid.getAngles();
        grid.setDominantAngel(KdeAngle.getDominantDirection(entries));
      }
    }
  }

  private void getAllCrossingEdgeAngles(Grid grid) {
    for (Edge edge : edges) {
      if (grid.crossBoundary(edge)) {
        grid.addAngle(edge.getAngle(), edge.getWeight());
      }
    }
  }

  public List<RegionGrid> getRegionGridList() {
    return regionGridList;
  }

  public void setRegionGridList(List<RegionGrid> regionGridList) {
    this.regionGridList = regionGridList;
  }
}
