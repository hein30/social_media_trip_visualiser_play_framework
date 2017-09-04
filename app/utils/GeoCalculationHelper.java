package utils;

import java.awt.geom.Point2D;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;

import models.geography.BoundingBox;
import models.trip.GeoLocation;

/**
 * Helper class to perform geo-distance calculations.
 */
public class GeoCalculationHelper {

  /**
   *
   * @param location - start location
   * @param azimuth - angle
   * @param distance - distance in metre
   * @return
   */
  public static Point2D getEndPointAtGivenDirection(GeoLocation location, double azimuth,
      double distance) {

    GeodeticCalculator calculator = new GeodeticCalculator();
    calculator.setStartingGeographicPoint(location.getLongitude(), location.getLatitude());
    calculator.setDirection(azimuth, distance);

    return calculator.getDestinationGeographicPoint();
  }

  public static GeoLocation calculateMidpoint(BoundingBox boundingBox) {

    return calculateMidpoint(boundingBox.getSouthWest().getLongitude(),
        boundingBox.getSouthWest().getLatitude(), boundingBox.getNorthEast().getLongitude(),
        boundingBox.getNorthEast().getLatitude());
  }

  public static GeoLocation calculateMidpoint(double minLong, double minLat, double maxLong,
      double maxLat) {
    GeodeticCalculator calculator = new GeodeticCalculator();
    calculator.setStartingGeographicPoint(minLong, minLat);
    calculator.setDestinationGeographicPoint(maxLong, maxLat);

    final List<Point2D> geodeticPath = calculator.getGeodeticPath(1);
    return new GeoLocation(geodeticPath.get(1).getY(), geodeticPath.get(1).getX());
  }
}
