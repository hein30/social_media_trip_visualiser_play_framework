package utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import models.trip.GeoLocation;

public class CoordinateRandomiserTest {

  @Test
  public void testRandomiseMethod() {

    GeoLocation start = new GeoLocation(51.45, 0.1);
    final int maxDistance = 1000;
    final int minDistance = 300;
    GeoLocation end = CoordinateRandomiser.randomise(start, maxDistance, minDistance);

    final double haverSineDistanceInMeter =
        HaversineCalculator.getHaverSineDistanceInMeter(start, end);

    assertTrue(haverSineDistanceInMeter >= minDistance);
  }

}
