package models.tweets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import models.trip.GeoLocation;

/**
 * Tests for {@link BoundingBox}.
 */
public class BoundingBoxTest {

  private BoundingBox bb;

  @Before
  public void setUp() {
    bb = new BoundingBox(-0.3515, 51.3849, 0.1483, 51.6723);
  }

  @Test
  public void testConstructorMethod() {
    assertEquals(51.3849, bb.getSouthWest().getLatitude(), 1e-15);
    assertEquals(-0.3515, bb.getSouthWest().getLongitude(), 1e-15);
    assertEquals(51.6723, bb.getNorthEast().getLatitude(), 1e-15);
    assertEquals(0.1483, bb.getNorthEast().getLongitude(), 1e-15);
    assertEquals(51.3849, bb.getSouthEast().getLatitude(), 1e-15);
    assertEquals(0.1483, bb.getSouthEast().getLongitude(), 1e-15);
    assertEquals(51.6723, bb.getNorthWest().getLatitude(), 1e-15);
    assertEquals(-0.3515, bb.getNorthWest().getLongitude(), 1e-15);
  }

  @Test
  public void testIsLocationInBoxMethod() {
    GeoLocation bridgend = new GeoLocation(51.5142013, -3.5848513);
    assertFalse(bb.isLocationInBox(bridgend));

    GeoLocation justOutsideSouthWestCornerSouth = new GeoLocation(51.384545, -0.350514);
    assertFalse(bb.isLocationInBox(justOutsideSouthWestCornerSouth));

    GeoLocation justOutsideSouthwestCornerWest = new GeoLocation(51.385170, -0.351736);
    assertFalse(bb.isLocationInBox(justOutsideSouthwestCornerWest));

    GeoLocation justOutsideNorthWestCorner = new GeoLocation(51.672593, -0.351918);
    assertFalse(bb.isLocationInBox(justOutsideNorthWestCorner));

    GeoLocation justOutsideSouthEastCorner = new GeoLocation(51.384118, 0.148760);
    assertFalse(bb.isLocationInBox(justOutsideSouthEastCorner));

    GeoLocation justInsideSouthEastCorner = new GeoLocation(51.386178, 0.146460);
    assertTrue(bb.isLocationInBox(justInsideSouthEastCorner));

    GeoLocation insideBox = new GeoLocation(51.386091, 0.146711);
    assertTrue(bb.isLocationInBox(insideBox));
  }
}
