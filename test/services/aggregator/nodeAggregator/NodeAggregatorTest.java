package services.aggregator.nodeAggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import models.geography.BoundingBox;
import models.geography.BoundingBoxTest;
import models.graph.Edge;
import models.graph.ResultGraph;
import models.trip.GeoLocation;
import models.trip.Trip;
import models.trip.SocialMediaTrip;

/**
 * Tests for @{@link NodeAggregator}.
 */
public class NodeAggregatorTest {

  private BoundingBox londonBox;

  @Before
  public void setUp() {
    londonBox = BoundingBoxTest.londonBox();
  }

  /**
   * https://github.com/hein30/play-test-app/wiki/NodeAggregatorTest see the wiki for more
   * information on test
   */
  @Test
  public void testAggregateNodesMethodUndirected() {

    List<Trip> trips = buildTestTrips();

    NodeAggregator aggregator = new NodeAggregator();
    ResultGraph graph = aggregator.aggregateNodes(londonBox, 2, false, trips, false);

    assertEquals(3, graph.getNumEdges());
    assertEquals(4, graph.getNumNodes());

    assertEquals(6,
        graph.getEdgeList().stream().map(Edge::getWeight).mapToInt(Integer::intValue).sum());

    List<Integer> sizeOfEachNode = graph.getNodeMap().entrySet().stream()
        .map(entry -> entry.getValue().getLocations().size()).collect(Collectors.toList());

    assertTrue(CollectionUtils.isEqualCollection(sizeOfEachNode, Arrays.asList(1, 1, 5, 5)));
  }

  @Test
  public void testAggregateNodesMethodDirected() {

    List<Trip> trips = buildTestTrips();

    NodeAggregator aggregator = new NodeAggregator();
    ResultGraph graph = aggregator.aggregateNodes(londonBox, 2, false, trips, true);

    assertEquals(4, graph.getNumEdges());
    assertEquals(4, graph.getNumNodes());

    assertEquals(6,
        graph.getEdgeList().stream().map(Edge::getWeight).mapToInt(Integer::intValue).sum());

    List<Integer> sizeOfEachNode = graph.getNodeMap().entrySet().stream()
        .map(entry -> entry.getValue().getLocations().size()).collect(Collectors.toList());

    assertTrue(CollectionUtils.isEqualCollection(sizeOfEachNode, Arrays.asList(1, 1, 5, 5)));
  }

  private List<Trip> buildTestTrips() {
    List<Trip> trips = new ArrayList<>();

    GeoLocation trip1Start = new GeoLocation(51.41272673, 0.09101646);
    GeoLocation trip1End = new GeoLocation(51.50711486, -0.12731805);
    SocialMediaTrip trip1 = new SocialMediaTrip(trip1Start, trip1End);
    trips.add(trip1);

    GeoLocation outsideStart = new GeoLocation(100, 100);
    trips.add(new SocialMediaTrip(outsideStart, trip1End));

    GeoLocation trip2Start = new GeoLocation(51.51945883, -0.09388557);
    GeoLocation trip2End = new GeoLocation(51.54, -0.2375);
    SocialMediaTrip trip2 = new SocialMediaTrip(trip2Start, trip2End);
    trips.add(trip2);

    GeoLocation trip3Start = new GeoLocation(51.43435496, -0.14545613);
    GeoLocation trip3End = new GeoLocation(51.52655, -0.13319);
    SocialMediaTrip trip3 = new SocialMediaTrip(trip3Start, trip3End);
    trips.add(trip3);

    GeoLocation trip4Start = new GeoLocation(51.4592, 0.0799);
    GeoLocation trip4End = new GeoLocation(51.52573, -0.08736);
    SocialMediaTrip trip4 = new SocialMediaTrip(trip4Start, trip4End);
    trips.add(trip4);

    GeoLocation trip5Start = new GeoLocation(51.51252462, -0.13865487);
    GeoLocation trip5End = new GeoLocation(51.40917238, -0.30569864);
    SocialMediaTrip trip5 = new SocialMediaTrip(trip5Start, trip5End);
    trips.add(trip5);

    GeoLocation trip6Start = new GeoLocation(51.55033056, 0.05621944);
    GeoLocation trip6End = new GeoLocation(51.50711486, -0.12731805);
    SocialMediaTrip trip6 = new SocialMediaTrip(trip6Start, trip6End);
    trips.add(trip6);

    GeoLocation trip7Start = new GeoLocation(51.50711486, -0.12731805);
    GeoLocation trip7End = new GeoLocation(51.41666667, -0.03333333);
    SocialMediaTrip trip7 = new SocialMediaTrip(trip7Start, trip7End);
    trips.add(trip7);


    GeoLocation trip8Start = new GeoLocation(51.50711486, -0.12731805);
    GeoLocation trip8End = new GeoLocation(51.44235536, 0.01447519);
    SocialMediaTrip trip8 = new SocialMediaTrip(trip8Start, trip8End);
    trips.add(trip8);

    GeoLocation trip10Start = new GeoLocation(51.50711486, -0.12731805);
    GeoLocation trip10End = new GeoLocation(51.456, 0.006);
    SocialMediaTrip trip10 = new SocialMediaTrip(trip10Start, trip10End);
    trips.add(trip10);

    return trips;
  }
}
