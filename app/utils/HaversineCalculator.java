package utils;

import models.trip.GeoLocation;

/**
 * Helper class to calculate the distance between two coordinates.
 */
public class HaversineCalculator {
  public static final double RADIUS_OF_EARTH = 6_371;

  private HaversineCalculator() {
    // static class. hide constructor.
  }

  public static double getHaverSineDistanceInMeter(GeoLocation start, GeoLocation end) {
    return getHaverSineDistanceInMeter(start.getLatitude(), start.getLongitude(), end.getLatitude(),
        end.getLongitude());
  }

  /**
   * Method to calculate the distance between two GPS points.
   *
   * @param lat1 - Latitude coordinate of point 1
   * @param lon1 - Longitude coordinate of point 1
   * @param lat2 - Latitiude coordinate of point 2
   * @param lon2 - Longitude coordiate of point 2
   * @return - distance between two points in meter.
   */
  private static double getHaverSineDistanceInMeter(double lat1, double lon1, double lat2,
      double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double radianLat1 = Math.toRadians(lat1);
    double radianlat2 = Math.toRadians(lat2);

    double a = Math.pow(Math.sin(dLat / 2), 2)
        + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(radianLat1) * Math.cos(radianlat2);
    double c = 2 * Math.asin(Math.sqrt(a));
    return RADIUS_OF_EARTH * c * 1000;
  }
}
