package utils;

import models.trip.GeoLocation;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for {@link HaversineCalculator}.
 */
public class HaversineCalculatorTest {

    @Test
    public void testDistanceCalculatorForGeoLocations(){
        GeoLocation start = new GeoLocation(51.45,0.1);
        GeoLocation end = new GeoLocation(51.50711486, -0.12731805);

        assertEquals(16975.234089397363, HaversineCalculator.getHaverSineDistanceInMeter(start,end), 1e-14);
    }
}