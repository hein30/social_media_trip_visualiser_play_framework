package services.aggregator.edgeAggregator.gbeb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import models.geography.Grid;
import models.graph.Edge;
import services.aggregator.edgeAggregator.BundlingParameters;
import utils.KdeAngle;

/**
 * Calculate the dominant angel of each grid.
 */
public class DominantAngleCalculator {

  private static final Cache<String, Grid[][]> CACHE;

  static {
    CACHE = CacheBuilder.newBuilder().expireAfterAccess(6, TimeUnit.HOURS)
        .expireAfterWrite(6, TimeUnit.HOURS).build();
  }

  private Grid[][] grids;
  private List<Edge> edges;
  private BundlingParameters parameters;

  public DominantAngleCalculator(Grid[][] grids, List<Edge> edges, BundlingParameters parameters) {
    this.grids = grids;
    this.edges = edges;
    this.parameters = parameters;
  }

  /**
   * Returns a 2D grid array which has dominant angle information.
   * 
   * @return
   */
  public Grid[][] calculate() {

    String cacheKey = buildCacheKey();

    Grid[][] resultGrids = parameters.isUseCache() ? fetchFromCache(cacheKey) : null;

    if (resultGrids == null) {
      performCalculations();
      resultGrids = grids;

      CACHE.put(cacheKey, resultGrids);
    } else {
      Arrays.stream(resultGrids).flatMap(Arrays::stream).forEach(grid -> grid.setMerged(false));
    }

    return resultGrids;
  }

  private Grid[][] fetchFromCache(String cacheKey) {
    return CACHE.getIfPresent(cacheKey);
  }

  private String buildCacheKey() {
    StringBuilder builder = new StringBuilder();
    builder.append(parameters.getArea());
    builder.append(parameters.getNumGridsForEdgeBundling());
    builder.append(parameters.getNumGridsForNodeBundling());

    return builder.toString();
  }

  private void performCalculations() {
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
