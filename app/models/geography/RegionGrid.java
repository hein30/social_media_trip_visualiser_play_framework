package models.geography;

import java.util.ArrayList;
import java.util.List;

public class RegionGrid {

  private List<Grid> grids;

  public RegionGrid() {
    grids = new ArrayList<>();
  }

  public void mergeRegionsWith(RegionGrid regionGrid) {
    grids.addAll(regionGrid.getGrids());
  }

  public void addGrid(Grid grid) {
    grids.add(grid);
  }

  public List<Grid> getGrids() {
    return grids;
  }

  public void setGrids(List<Grid> grids) {
    this.grids = grids;
  }
}
