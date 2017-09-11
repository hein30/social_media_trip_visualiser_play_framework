package services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.mongodb.morphia.query.FindOptions;

import au.com.bytecode.opencsv.CSVWriter;
import models.trip.SocialMediaTrip;
import mongo.MorphiaHelper;
import services.twitter.Node;

/**
 * Created by Hein Min Htike on 7/18/2017.
 */
public class ToGephi {

  public static void main(String args[]) throws IOException {

    final FindOptions options = new FindOptions();
    options.limit(1000);
    List<SocialMediaTrip> trips =
        MorphiaHelper.getDatastore().createQuery(SocialMediaTrip.class).asList(options);

    try (ByteArrayOutputStream baosNode = new ByteArrayOutputStream();
        OutputStreamWriter oswNode = new OutputStreamWriter(baosNode);
        CSVWriter nodeCSV = new CSVWriter(oswNode, CSVWriter.DEFAULT_SEPARATOR);

        ByteArrayOutputStream baosEdge = new ByteArrayOutputStream();
        OutputStreamWriter oswEdge = new OutputStreamWriter(baosEdge);
        CSVWriter edgeCSV = new CSVWriter(oswEdge, CSVWriter.DEFAULT_SEPARATOR)) {

      nodeCSV.writeNext(new String[] {"id", "lat", "lon"});
      edgeCSV.writeNext(new String[] {"from", "to", "count"});

      trips.forEach(trip -> {
        Node start = new Node(trip.getStartPoint());
        Node end = new Node(trip.getEndPoint());

        nodeCSV.writeNext(new String[] {start.getId(), String.valueOf(start.getLat()),
            String.valueOf(start.getLon())});
        nodeCSV.writeNext(
            new String[] {end.getId(), String.valueOf(end.getLat()), String.valueOf(end.getLon())});

        edgeCSV.writeNext(new String[] {start.getId(), end.getId(), "1"});
      });

      edgeCSV.flush();
      nodeCSV.flush();

      Files.write(Paths.get("./nodes.csv"), baosNode.toByteArray());
      Files.write(Paths.get("./edges.csv"), baosEdge.toByteArray());
    }
  }
}
