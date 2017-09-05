package utils;

import java.awt.geom.Point2D;
import java.util.Random;

import models.trip.GeoLocation;

/**
 * Randomise the given coordinate.
 */
public class CoordinateRandomiser {

  private static Random random = new Random();

  /**
   *
   * @param original
   * @param maxDistance - max distance of the randomised coordinate in meter.
   * @param minDistance - min distanceof the randomised coordinate from original point in meter.
   * @return
   */
  public static GeoLocation randomise(GeoLocation original, int maxDistance, int minDistance) {

    int distance = (random.nextInt() % maxDistance) + minDistance;
    int angle = random.nextInt() % 360; // max degree is 360.

    final Point2D point =
        GeoCalculationHelper.getEndPointAtGivenDirection(original, angle, distance);

    return new GeoLocation(point.getY(), point.getX());
  }
}
