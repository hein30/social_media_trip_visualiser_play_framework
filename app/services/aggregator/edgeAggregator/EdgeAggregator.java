package services.aggregator.edgeAggregator;

import java.util.List;

import models.graph.Edge;

/**
 * Aggregate edges.
 */
public interface EdgeAggregator {

  /**
   * Process the edge aggregation.
   */
  public void process();
}
