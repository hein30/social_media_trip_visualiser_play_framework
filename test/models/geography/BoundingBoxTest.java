package models.geography;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import models.trip.GeoLocation;
import models.trip.Trip;
import models.trip.TwitterTrip;

/**
 * Tests for {@link BoundingBox}.
 */
public class BoundingBoxTest {

  private BoundingBox bb;

  public static BoundingBox londonBox() {
    return new BoundingBox(-0.3515, 51.3849, 0.1483, 51.6723);
  }

  @Before
  public void setUp() {
    bb = londonBox();
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
  public void testGridSizeCalculations() {
    assertEquals(689, bb.getHorizontalGridSize(bb, 50), 1e-4);
    assertEquals(639, bb.getVerticalGridSize(bb, 50), 1e-4);
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

  @Test
  public void testGridsMethod() {
    List<Grid> grids = bb.grids(30, false);

    assertEquals(900, grids.size());

    List<List<Grid>> rows = Lists.partition(grids, 30);
    assertEquals(30, rows.size());

    for (List<Grid> row : rows) {
      // asserting that the box from left side's SE and NE points are the same as the SW and NW
      // points of right side box.
      for (int i = 1; i < row.size(); i++) {
        final BoundingBox leftBox = row.get(i - 1).getBoundingBox();
        final BoundingBox rightBox = row.get(i).getBoundingBox();

        assertEquals(leftBox.getSouthEast().getLatitude(), rightBox.getSouthWest().getLatitude(),
            1e-6);
        assertEquals(leftBox.getSouthEast().getLongitude(), rightBox.getSouthWest().getLongitude(),
            1e-6);

        assertEquals(leftBox.getNorthEast().getLatitude(), rightBox.getNorthWest().getLatitude(),
            1e-6);
        assertEquals(leftBox.getNorthEast().getLongitude(), rightBox.getNorthWest().getLongitude(),
            1e-6);
      }
    }

    // asserting that the bottom box's NW and NE points are the top box's SW and SE points
    for (int i = 1; i < rows.size(); i++) {
      final BoundingBox bottomBox = rows.get(i - 1).get(0).getBoundingBox();
      final BoundingBox topBox = rows.get(i).get(0).getBoundingBox();

      assertEquals(bottomBox.getNorthWest().getLongitude(), topBox.getSouthWest().getLongitude(),
          1e-5);
      assertEquals(bottomBox.getNorthWest().getLatitude(), topBox.getSouthWest().getLatitude(),
          1e-5);

      assertEquals(bottomBox.getNorthEast().getLongitude(), topBox.getSouthEast().getLongitude(),
          1e-5);
      assertEquals(bottomBox.getNorthEast().getLatitude(), topBox.getSouthEast().getLatitude(),
          1e-5);
    }
  }

  @Test
  public void testIsEdgeIntersecting() {
    GeoLocation start = new GeoLocation(51.467199, -3.498076);
    GeoLocation end = new GeoLocation(51.513587, -3.623046);
    Trip trip = new TwitterTrip(start, end);
    assertFalse(londonBox().crossBoundary(trip));

    GeoLocation intersectingStart = new GeoLocation(51.713152, -0.128221);
    GeoLocation intersectingEnd = new GeoLocation(51.431957, -0.100856);
    Trip intersectingTrip = new TwitterTrip(intersectingStart, intersectingEnd);
    assertTrue(londonBox().crossBoundary(intersectingTrip));

    GeoLocation trip3Start = new GeoLocation(51.366380, 0.100238);
    GeoLocation trip3End = new GeoLocation(51.396414, 0.011162);
    Trip trip3 = new TwitterTrip(trip3Start, trip3End);
    assertTrue(londonBox().crossBoundary(trip3));

    // trip that is completely inside bounding box
    GeoLocation trip4Start = new GeoLocation(51.425152, -0.037984);
    GeoLocation trip4End = new GeoLocation(51.461999, -0.100935);
    Trip trip4 = new TwitterTrip(trip4Start, trip4End);
    assertFalse(londonBox().crossBoundary(trip4));
  }
}
