package services.aggregator.edgeAggregator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.geography.Grid;
import models.graph.Edge;
import utils.KdeAngle;

/**
 * Calculate the dominant angel of each grid.
 */
public class DominantAngleCalculator {

  private Grid[][] grids;
  private List<Edge> edges;


  public DominantAngleCalculator(Grid[][] grids, List<Edge> edges) {
    this.grids = grids;
    this.edges = edges;
  }

  public void calculate() {
    for (int row = 0; row < grids.length; row++) {
      for (int col = 0; col < grids.length; col++) {

        Grid grid = grids[row][col];
        calculateDominantAngleForOneGrid(grid);
      }
    }
  }

  private void calculateDominantAngleForOneGrid(Grid grid) {
    final Map<Double, Integer> entries = getAllCrossingEdgeAngles(grid);
    grid.setDominantAngel(KdeAngle.getDominantDirection(entries));
  }

  private Map<Double, Integer> getAllCrossingEdgeAngles(Grid grid) {
    final Map<Double, Integer> angles = new HashMap<>();

    for (Edge edge : edges) {
      if (grid.crossBoundary(edge)) {

        final double angle = edge.getAngle();

        if (angles.containsKey(angle)) {
          angles.put(angle, angles.get(angle) + edge.getWeight());
        } else {
          angles.put(angle, edge.getWeight());
        }
      }
    }

    return angles;
  }
}
