package models.geography;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class GridTest {

  @Test
  public void testConstructor() {
    BoundingBox bb = Area.LONDON.getBoundingBox();

    Grid londonGrid = new Grid("London id", bb);
    assertEquals("London id", londonGrid.getId());
    assertNotNull(londonGrid.getBoundingBox());
    assertEquals(51.52886790465437, londonGrid.getMidPoint().getLatitude(), 1e-4);
    assertEquals( -0.10238670615890477, londonGrid.getMidPoint().getLongitude(), 1e-4);
  }
}
