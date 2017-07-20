package models.geography;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import models.trip.GeoLocation;

/**
 * Created by Hein Min Htike on 7/20/2017.
 */
public class AreaTest {
  @Test
  public void getArea() throws Exception {
    assertEquals("London", Area.LONDON.getArea());
  }

  @Test
  public void getBoundingBox() throws Exception {
    BoundingBox london = Area.LONDON.getBoundingBox();

    assertEquals(-0.3515, london.getSouthWest().getLongitude(), 1e-14);
    assertEquals(51.3849, london.getSouthWest().getLatitude(), 1e-14);
    assertEquals(0.1483, london.getNorthEast().getLongitude(), 1e-14);
    assertEquals(51.6723, london.getNorthEast().getLatitude(), 1e-14);
  }

  @Test
  public void getAreaForLocationMethod() {
    GeoLocation londonLocation = new GeoLocation(51.464009, -0.227966);
    assertEquals(Area.LONDON, Area.getAreaForLocation(londonLocation));

    GeoLocation cardiffLocation = new GeoLocation(51.479347, -3.193231);
    assertEquals(Area.CARDIFF, Area.getAreaForLocation(cardiffLocation));

    GeoLocation randomLocation = new GeoLocation(51.384485, -2.783558);
    assertEquals(Area.OTHERS, Area.getAreaForLocation(randomLocation));
  }

  @Test
  public void testAreaByNameMethod() {
    assertEquals(Area.LONDON, Area.getAreaForName("London"));
    assertEquals(Area.LONDON, Area.getAreaForName("LONDON"));
    assertEquals(Area.OTHERS, Area.getAreaForName("LONDONs"));
  }
}
