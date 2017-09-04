package services.aggregator.edgeAggregator.gbeb;

import java.util.ArrayList;
import java.util.List;

import models.geography.Grid;
import models.geography.RegionGrid;
import services.aggregator.edgeAggregator.BundlingParameters;

/**
 * Simply merge all the neighboring regions.
 */
public class SimpleRegionMerger extends RegionMerger {

  public SimpleRegionMerger(Grid[][] grids, BundlingParameters parameters) {
    super(grids, parameters);
  }

  @Override
  public List<RegionGrid> mergeRegions() {
    List<RegionGrid> regionGridList = new ArrayList<>();

    for (int row = 0; row < getGrids()[0].length; row++) {
      for (int col = 0; col < getGrids()[0].length; col++) {

        Grid currentGrid = getGrids()[row][col];

        if (!currentGrid.isMerged() && currentGrid.getDominantAngle().isPresent()) {
          RegionGrid regionGrid = new RegionGrid();
          regionGridList.add(regionGrid);

          mergeNeighboringGrids(row, col, currentGrid, regionGrid);
        }
      }
    }
    return regionGridList;
  }

  private void mergeNeighboringGrids(int row, int col, Grid currentGrid, RegionGrid regionGrid) {

    currentGrid.setMerged(true);
    regionGrid.addGrid(currentGrid);

    Grid rightGrid = col < getGrids()[0].length - 1 ? getGrids()[row][col + 1] : null;
    Grid bottomGrid = row < getGrids()[0].length - 1 ? getGrids()[row + 1][col] : null;

    Grid leftGrid = col == 0 ? null : getGrids()[row][col - 1];
    Grid topGrid = row == 0 ? null : getGrids()[row - 1][col];

    if (shouldMerge(currentGrid, rightGrid)) {
      mergeNeighboringGrids(row, col + 1, rightGrid, regionGrid);
    }

    if (shouldMerge(currentGrid, bottomGrid)) {
      mergeNeighboringGrids(row + 1, col, bottomGrid, regionGrid);
    }

    if (shouldMerge(currentGrid, leftGrid)) {
      mergeNeighboringGrids(row, col - 1, leftGrid, regionGrid);
    }

    if (shouldMerge(currentGrid, topGrid)) {
      mergeNeighboringGrids(row - 1, col, topGrid, regionGrid);
    }
  }
}
