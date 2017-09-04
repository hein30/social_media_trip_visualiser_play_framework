package models.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import models.trip.GeoLocation;

public class EdgeTest {
  @Test
  public void get180Angle() throws Exception {
    Node start = new Node(new GeoLocation(51.488790, -3.169731), "node1");
    Node end = new Node(new GeoLocation(51.491623, -3.171705), "node2");
    // start -> end = 336.32

    Edge startEnd = new Edge(start, end);
    Edge endStart = new Edge(end, start);

    assertEquals(startEnd.getAngle(), endStart.getAngle(), 1e-2);
  }

}
