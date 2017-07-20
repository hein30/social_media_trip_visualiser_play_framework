package models.geography;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;

import models.trip.GeoLocation;
import utils.HaversineCalculator;

/**
 * Created by Hein Min Htike on 6/26/2017.
 */
public class BoundingBox {

  private GeoLocation southWest;
  private GeoLocation northEast;
  private GeoLocation southEast;
  private GeoLocation northWest;

  public BoundingBox() {
    // for morphia
  }

  public BoundingBox(GeoLocation southWest, GeoLocation northEast) {
    this(southWest.getLongitude(), southWest.getLatitude(), northEast.getLongitude(),
        northEast.getLatitude());
  }

  public BoundingBox(double southWestLong, double southWestLat, double northEastLong,
      double northEastLat) {
    this.southWest = new GeoLocation(southWestLat, southWestLong);
    this.northEast = new GeoLocation(northEastLat, northEastLong);
    this.northWest = new GeoLocation(northEastLat, southWestLong);
    this.southEast = new GeoLocation(southWestLat, northEastLong);
  }

  public boolean isLocationInBox(GeoLocation location) {
    boolean isLatitudeInside = southWest.getLatitude() <= location.getLatitude()
        && location.getLatitude() <= northEast.getLatitude();

    boolean isLongitudeInside = southWest.getLongitude() <= location.getLongitude()
        && location.getLongitude() <= northEast.getLongitude();

    return isLatitudeInside && isLongitudeInside;
  }

  public GeoLocation getSouthWest() {
    return southWest;
  }

  public void setSouthWest(GeoLocation southWest) {
    this.southWest = southWest;
  }

  public GeoLocation getNorthEast() {
    return northEast;
  }

  public void setNorthEast(GeoLocation northEast) {
    this.northEast = northEast;
  }

  public GeoLocation getSouthEast() {
    return southEast;
  }

  public void setSouthEast(GeoLocation southEast) {
    this.southEast = southEast;
  }

  public GeoLocation getNorthWest() {
    return northWest;
  }

  public void setNorthWest(GeoLocation northWest) {
    this.northWest = northWest;
  }

  /**
   * Divide this bounding box into grids of specified size.
   * 
   * @param numGrids number of grids to divide (grid * grid)
   * @param extendedBox true - add 15 km in all directions
   */
  public List<Grid> grids(int numGrids, boolean extendedBox) {
    GeodeticCalculator calculator = new GeodeticCalculator();

    BoundingBox extendedBoundingBox;

    if (extendedBox) {
      GeoLocation extendedSW = getNewGeoLocation(calculator, southWest, 1_000, 225);
      GeoLocation extendedNE = getNewGeoLocation(calculator, northEast, 20_000, 45);

      extendedBoundingBox = new BoundingBox(extendedSW, extendedNE);
    } else {
      extendedBoundingBox = this;
    }

    List<Grid> grids = new ArrayList<>();

    double xDistance = getHorizontalGridSize(extendedBoundingBox, numGrids);
    double yDistance = getVerticalGridSize(extendedBoundingBox, numGrids);

    GeoLocation currentSW = extendedBoundingBox.getSouthWest();


    for (int row = 0; row < numGrids; row++) {
      GeoLocation newRowSW = getNewGeoLocation(calculator, currentSW, yDistance, 0);

      grids.addAll(calculateGridsForOneRow(calculator, numGrids, xDistance, currentSW, newRowSW));

      currentSW = newRowSW;
    }
    return grids;
  }

  private List<Grid> calculateGridsForOneRow(GeodeticCalculator calculator, int numGrids,
      double xDistance, GeoLocation currentSW, GeoLocation newRowSW) {
    List<Grid> grids = new ArrayList<>();

    GeoLocation currentNE = newRowSW.clone();
    grids.addAll(
        calculateColumnGridsForThisRow(calculator, numGrids, xDistance, currentSW, currentNE));

    return grids;
  }

  /**
   * Calculate column grids for this row.
   * 
   * @param calculator
   * @param numGrids - number of columns
   * @param xDistance - distance on x-axis.
   * @param currentSW - starting south west point for this row.
   * @param currentNE -starting north east point for this row.
   * @return
   */
  private List<Grid> calculateColumnGridsForThisRow(GeodeticCalculator calculator, int numGrids,
      double xDistance, GeoLocation currentSW, GeoLocation currentNE) {
    List<Grid> grids = new ArrayList<>();
    for (int col = 0; col < numGrids; col++) {
      GeoLocation newNE = getNewGeoLocation(calculator, currentNE, xDistance, 90);
      BoundingBox bb = new BoundingBox(currentSW.clone(), newNE.clone());
      grids.add(new Grid(bb));

      currentNE = newNE;
      currentSW = getNewGeoLocation(calculator, currentSW, xDistance, 90);
    }

    return grids;
  }

  private GeoLocation getNewGeoLocation(GeodeticCalculator calculator, GeoLocation location,
      double distance, double azimuth) {
    calculator.setStartingGeographicPoint(location.getLongitude(), location.getLatitude());
    calculator.setDirection(azimuth, distance);

    final Point2D destinationGeographicPoint = calculator.getDestinationGeographicPoint();
    return new GeoLocation(destinationGeographicPoint.getY(), destinationGeographicPoint.getX());
  }

  public double getVerticalGridSize(BoundingBox box, int numGrids) {
    return Math.round(
        HaversineCalculator.getHaverSineDistanceInMeter(box.getNorthWest(), box.getSouthWest())
            / numGrids);
  }

  public double getHorizontalGridSize(BoundingBox box, int numGrids) {
    return Math.round(
        HaversineCalculator.getHaverSineDistanceInMeter(box.getNorthWest(), box.getNorthEast())
            / numGrids);
  }
}
