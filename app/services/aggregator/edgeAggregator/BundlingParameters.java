package services.aggregator.edgeAggregator;

/**
 * Parameters used to bundle edges.
 */
public class BundlingParameters {
  private String area;
  private String source;
  private String bundler;
  private int numGridsForNodeBundling;
  private int numGridsForEdgeBundling;
  private int angularDifferenceThreshold;
  private boolean useCache;

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public int getNumGridsForNodeBundling() {
    return numGridsForNodeBundling;
  }

  public void setNumGridsForNodeBundling(int numGridsForNodeBundling) {
    this.numGridsForNodeBundling = numGridsForNodeBundling;
  }

  public int getNumGridsForEdgeBundling() {
    return numGridsForEdgeBundling;
  }

  public void setNumGridsForEdgeBundling(int numGridsForEdgeBundling) {
    this.numGridsForEdgeBundling = numGridsForEdgeBundling;
  }

  public int getAngularDifferenceThreshold() {
    return angularDifferenceThreshold;
  }

  public void setAngularDifferenceThreshold(int angularDifferenceThreshold) {
    this.angularDifferenceThreshold = angularDifferenceThreshold;
  }

  public boolean isUseCache() {
    return useCache;
  }

  public void setUseCache(boolean useCache) {
    this.useCache = useCache;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getBundler() {
    return bundler;
  }

  public void setBundler(String bundler) {
    this.bundler = bundler;
  }
}
