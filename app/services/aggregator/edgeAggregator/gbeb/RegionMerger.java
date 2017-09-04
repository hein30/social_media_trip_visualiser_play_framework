package services.aggregator.edgeAggregator.gbeb;

import java.util.List;

import models.geography.Grid;
import models.geography.RegionGrid;
import services.aggregator.edgeAggregator.BundlingParameters;

/**
 * Merge regions based on the parameters.
 */
public abstract class RegionMerger {

  private Grid[][] grids;
  private BundlingParameters parameters;

  public RegionMerger(Grid[][] grids, BundlingParameters parameters) {
    this.grids = grids;
    this.parameters = parameters;
  }

  protected boolean shouldMerge(Grid currentGrid, Grid gridToMerge) {
    return gridToMerge != null && gridToMerge.getDominantAngle().isPresent()
        && Math.abs(gridToMerge.getDominantAngle().get()
            - currentGrid.getDominantAngle().get()) <= parameters.getAngularDifferenceThreshold()
        && !gridToMerge.isMerged();
  }

  // merge regions.
  public abstract List<RegionGrid> mergeRegions();

  public Grid[][] getGrids() {
    return grids;
  }

  public void setGrids(Grid[][] grids) {
    this.grids = grids;
  }

  public BundlingParameters getParameters() {
    return parameters;
  }

  public void setParameters(BundlingParameters parameters) {
    this.parameters = parameters;
  }
}
