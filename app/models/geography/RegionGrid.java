package models.geography;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import models.trip.GeoLocation;
import utils.GeoCalculationHelper;
import utils.HaversineCalculator;

public class RegionGrid {

  private List<Grid> grids;
  private double averageDominantAngle;

  // midpoint between smallest (x,y) and largest (x,y).
  private GeoLocation midPoint;

  private double minX;
  private double maxX;
  private double minY;
  private double maxY;
  private Coordinate minus90Intersection;
  private Coordinate plus90Intersection;

  public RegionGrid() {
    grids = new ArrayList<>();
    minX = Double.POSITIVE_INFINITY;
    maxX = Double.NEGATIVE_INFINITY;
    minY = Double.POSITIVE_INFINITY;
    maxY = Double.NEGATIVE_INFINITY;
  }

  public void addGrid(Grid grid) {
    grids.add(grid);

    if (minX > grid.getBoundingBox().getSouthWest().getLongitude()) {
      minX = grid.getBoundingBox().getSouthWest().getLongitude();
    }
    if (minY > grid.getBoundingBox().getSouthWest().getLatitude()) {
      minY = grid.getBoundingBox().getSouthWest().getLatitude();
    }
    if (maxX < grid.getBoundingBox().getNorthEast().getLongitude()) {
      maxX = grid.getBoundingBox().getNorthEast().getLongitude();
    }
    if (maxY < grid.getBoundingBox().getNorthEast().getLatitude()) {
      maxY = grid.getBoundingBox().getNorthEast().getLatitude();
    }

    // todo maybe use weighted average angle.
    averageDominantAngle = grids.stream().map(Grid::getDominantAngle).map(Optional::get)
        .mapToDouble(Double::doubleValue).sum() / (float) grids.size();
  }

  public List<Grid> getGrids() {
    return grids;
  }

  public void setGrids(List<Grid> grids) {
    this.grids = grids;
  }

  public double getAverageDominantAngle() {
    return averageDominantAngle;
  }

  public void setAverageDominantAngle(double averageDominantAngle) {
    this.averageDominantAngle = averageDominantAngle;
  }

  public void generateMidPointAndIntersections() {
    getMidPoint();

    generateBoundaryIntersections();
  }

  public GeoLocation getMidPoint() {
    if (midPoint == null) {
      midPoint = GeoCalculationHelper.calculateMidpoint(minX, minY, maxX, maxY);
    }

    return midPoint;
  }

  /**
   * We are using azimuth.
   * 
   *
   * @return
   */
  public void generateBoundaryIntersections() {
    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

    double azimuthMinus90 = averageDominantAngle - 90;
    double azimuthPlus90 = averageDominantAngle + 90;

    Point2D plus90EndPoint = getDestinationPoint(azimuthPlus90);
    Point2D minus90EndPoint = getDestinationPoint(azimuthMinus90);

    LineString plus90LineString = getLineString(geometryFactory, plus90EndPoint);
    LineString minus90LineString = getLineString(geometryFactory, minus90EndPoint);

    List<Coordinate> plus90AllCoordinates = new ArrayList<>();
    List<Coordinate> minus90AllCoordinates = new ArrayList<>();

    for (Grid grid : grids) {
      LinearRing ring =
          geometryFactory.createLinearRing(grid.getBoundingBox().getBoundingBoxPolygonCoords());
      Polygon polygon = geometryFactory.createPolygon(ring, null);

      Coordinate[] plus90Coordinates = polygon.intersection(plus90LineString).getCoordinates();
      Coordinate[] minus90Coordinates = polygon.intersection(minus90LineString).getCoordinates();

      for (int i = 0; i < plus90Coordinates.length; i++) {
        plus90AllCoordinates.add(plus90Coordinates[i]);
      }

      for (Coordinate coordinate : minus90Coordinates) {
        minus90AllCoordinates.add(coordinate);
      }
    }

    minus90Intersection = getCoordinateWithMaxDistance(minus90AllCoordinates);
    plus90Intersection = getCoordinateWithMaxDistance(plus90AllCoordinates);
  }

  private LineString getLineString(GeometryFactory geometryFactory, Point2D plus90EndPoint) {
    return geometryFactory.createLineString(new Coordinate[] {getMidPoint().getCoordinate(),
        new Coordinate(plus90EndPoint.getX(), plus90EndPoint.getY())});
  }

  private Point2D getDestinationPoint(double azimuthPlus90) {
    return GeoCalculationHelper.getEndPointAtGivenDirection(getMidPoint(), azimuthPlus90, 10_000_0);
  }

  /**
   * Get the intersection point with max distance from center.
   * 
   * @param coordinateList
   * @return
   */
  private Coordinate getCoordinateWithMaxDistance(List<Coordinate> coordinateList) {

    Optional<Coordinate> coordinateOptional = coordinateList.stream()
        .reduce((a,
            b) -> HaversineCalculator.getHaverSineDistanceInMeter(getMidPoint(),
                new GeoLocation(a.y, a.x)) > HaversineCalculator
                    .getHaverSineDistanceInMeter(getMidPoint(), new GeoLocation(b.y, b.x)) ? a : b);

    return coordinateOptional.isPresent() ? coordinateOptional.get()
        : getMidPoint().getCoordinate();
  }

  public Coordinate getPlus90Intersection() {
    return plus90Intersection;
  }

  public void setPlus90Intersection(Coordinate plus90Intersection) {
    this.plus90Intersection = plus90Intersection;
  }

  public Coordinate getMinus90Intersection() {
    return minus90Intersection;
  }

  public void setMinus90Intersection(Coordinate minus90Intersection) {
    this.minus90Intersection = minus90Intersection;
  }
}
