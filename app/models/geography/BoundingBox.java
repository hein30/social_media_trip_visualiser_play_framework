package models.geography;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.GeodeticCalculator;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import models.graph.Edge;
import models.trip.GeoLocation;
import models.trip.Trip;
import play.libs.Json;
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

  public Grid[][] gridsArrays(int numGrids, boolean extendedBox) {
    Grid[][] gridArray = new Grid[numGrids][numGrids];

    List<List<Grid>> partitionedList =
        Lists.reverse(Lists.partition(grids(numGrids, extendedBox), numGrids));

    int row = 0;
    for (List<Grid> rowList : partitionedList) {
      for (int col = 0; col < rowList.size(); col++) {
        final Grid grid = rowList.get(col);
        grid.setId("FixedId: row: " + row + " col: " + col);
        gridArray[row][col] = grid;
      }
      row++;
    }
    return gridArray;
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

      grids.addAll(
          calculateGridsForOneRow(calculator, numGrids, xDistance, currentSW, newRowSW, row));

      currentSW = newRowSW;
    }
    return grids;
  }

  private List<Grid> calculateGridsForOneRow(GeodeticCalculator calculator, int numGrids,
      double xDistance, GeoLocation currentSW, GeoLocation newRowSW, int rowNum) {
    List<Grid> grids = new ArrayList<>();

    GeoLocation currentNE = newRowSW.clone();
    grids.addAll(calculateColumnGridsForThisRow(calculator, numGrids, xDistance, currentSW,
        currentNE, rowNum));

    return grids;
  }

  /**
   * Calculate column grids for this row. <br>
   * If we want to move horizontally,calculate new coordinates at 0 degree and distance, and take
   * only new longitude. (because taking new lat/long causes gaps in the grid due to the spherical
   * shape of the world.
   * 
   * @param calculator
   * @param numGrids - number of columns
   * @param xDistance - distance on x-axis.
   * @param currentSW - starting south west point for this row.
   * @param currentNE -starting north east point for this row.
   * @return
   */
  private List<Grid> calculateColumnGridsForThisRow(GeodeticCalculator calculator, int numGrids,
      double xDistance, GeoLocation currentSW, GeoLocation currentNE, int rowNum) {
    List<Grid> grids = new ArrayList<>();
    for (int col = 0; col < numGrids; col++) {
      GeoLocation newLongitudeCoordinates = getNewGeoLocation(calculator, currentNE, xDistance, 90);
      GeoLocation newNE =
          new GeoLocation(currentNE.getLatitude(), newLongitudeCoordinates.getLongitude());
      BoundingBox bb = new BoundingBox(currentSW.clone(), newNE.clone());
      final String name = "row:" + rowNum + " col:" + +col;
      Grid grid = new Grid(name, bb);
      grids.add(grid);

      currentNE = newNE;
      // currentSW = getNewGeoLocation(calculator, currentSW, xDistance, 90);
      currentSW = new GeoLocation(currentSW.getLatitude(), currentNE.getLongitude());
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

  public boolean crossBoundary(Edge edge) {
    return !(this.isLocationInBox(edge.getFrom().getCenterLocation())
        && this.isLocationInBox(edge.getTo().getCenterLocation())) && isEdgeIntersect(edge);
  }

  public boolean crossBoundary(Trip trip) {
    return !(this.isLocationInBox(trip.getStartPoint()) && this.isLocationInBox(trip.getEndPoint()))
        && isEdgeIntersect(trip);
  }

  private boolean isEdgeIntersect(Coordinate[] polygonPoints, Coordinate[] linePoints) {
    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    LinearRing ring = geometryFactory.createLinearRing(polygonPoints);
    Polygon polygon = geometryFactory.createPolygon(ring, null);

    LineString line = geometryFactory.createLineString(linePoints);

    return polygon.intersects(line);
  }

  private boolean isEdgeIntersect(Edge edge) {

    Coordinate[] line = new Coordinate[] {edge.getFrom().getCenterLocation().getCoordinate(),
        edge.getTo().getCenterLocation().getCoordinate()};
    return isEdgeIntersect(getBoundingBoxPolygonCoords(), line);
  }

  private boolean isEdgeIntersect(Trip trip) {

    Coordinate[] line =
        new Coordinate[] {trip.getStartPoint().getCoordinate(), trip.getEndPoint().getCoordinate()};

    return isEdgeIntersect(getBoundingBoxPolygonCoords(), line);
  }

  private Coordinate[] getBoundingBoxPolygonCoords() {
    return new Coordinate[] {southWest.getCoordinate(), southEast.getCoordinate(),
        northEast.getCoordinate(), northWest.getCoordinate(), southWest.getCoordinate()};
  }

  public BoundingBox clone() {
    return Json.fromJson(Json.toJson(this), BoundingBox.class);
  }
}
