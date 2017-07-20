package services.aggregator.nodeAggregator;

import org.junit.Before;
import org.junit.Test;

import models.geography.BoundingBox;
import models.geography.BoundingBoxTest;
import models.trip.GeoLocation;
import models.trip.TwitterTrip;

/**
 * Tests for @{@link NodeAggregator}.
 */
public class NodeAggregatorTest {

  private BoundingBox londonBox;

  @Before
  public void setUp() {
    londonBox = BoundingBoxTest.londonBox();
  }

  @Test
  public void testAggregateNodesMethod() {
    GeoLocation trip1Start = new GeoLocation(51.41272673, 0.09101646);
    GeoLocation trip1End = new GeoLocation(51.50711486, -0.12731805);
    TwitterTrip trip1 = new TwitterTrip(trip1Start, trip1End);

    GeoLocation trip2Start = new GeoLocation(51.51945883, -0.09388557);
    GeoLocation trip2End = new GeoLocation(51.54, -0.2375);
    TwitterTrip trip2 = new TwitterTrip(trip2Start, trip2End);

    GeoLocation trip3Start = new GeoLocation(51.43435496, -0.14545613);
    GeoLocation trip3End = new GeoLocation(51.52655, -0.13319);
    TwitterTrip trip3 = new TwitterTrip(trip3Start, trip3End);

    GeoLocation trip4Start = new GeoLocation(51.4592, 0.0799);
    GeoLocation trip4End = new GeoLocation(51.52573, -0.08736);
    TwitterTrip trip4 = new TwitterTrip(trip4Start, trip4End);

    GeoLocation trip5Start = new GeoLocation(51.51252462, -0.13865487);
    GeoLocation trip5End = new GeoLocation(51.40917238, -0.30569864);
    TwitterTrip trip5 = new TwitterTrip(trip5Start, trip5End);

    GeoLocation trip6Start = new GeoLocation(51.55033056, 0.05621944);
    GeoLocation trip6End = new GeoLocation(51.50711486, -0.12731805);
    TwitterTrip trip6 = new TwitterTrip(trip6Start, trip6End);

    GeoLocation trip7Start = new GeoLocation(51.50711486, -0.12731805);
    GeoLocation trip7End = new GeoLocation(51.41666667, -0.03333333);
    TwitterTrip trip7 = new TwitterTrip(trip7Start, trip7End);

    GeoLocation trip8Start = new GeoLocation(51.50711486, -0.12731805);
    GeoLocation trip8End = new GeoLocation(51.44235536, 0.01447519);
    TwitterTrip trip8 = new TwitterTrip(trip8Start, trip8End);

    GeoLocation trip9Start = new GeoLocation(51.41666667, -0.03333333);
    GeoLocation trip9End = new GeoLocation(51.50711486, -0.12731805);
    TwitterTrip trip9 = new TwitterTrip(trip9Start, trip9End);

    GeoLocation trip10Start = new GeoLocation(51.50711486, -0.12731805);
    GeoLocation trip10End = new GeoLocation(51.456, 0.006);
    TwitterTrip trip10 = new TwitterTrip(trip10Start, trip10End);


  }


}
