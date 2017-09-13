package controllers;

import static akka.pattern.Patterns.ask;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import akka.actor.ActorRef;
import models.geography.Area;
import models.geography.BoundingBox;
import models.geography.Grid;
import models.graph.ResultGraph;
import models.graph.TriangulationResults;
import models.trip.SocialMediaTrip;
import models.tweets.Source;
import models.tweets.Status;
import models.tweets.TwitterUser;
import mongo.MorphiaHelper;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;
import services.aggregator.edgeAggregator.BundlingParameters;
import services.aggregator.edgeAggregator.GBEB;
import services.aggregator.nodeAggregator.NodeAggregator;
import services.trips.TripProcessorProtocol;
import services.twitter.rest.TwitterRestfulActorProtocol;


/**
 * Controller to access tweet related end-ponits.
 *
 * @author Hein Min Htike
 */
public class DataController extends Controller {

  public static final Datastore DS = MorphiaHelper.getDatastore();
  private final ActorRef tweetProcessor;

  private final ActorRef twitterResfulActor;

  @Inject
  public DataController(@Named("trip-processor-actor") ActorRef tweetProcessor,
      @Named("twitter-restful-bot-actor") ActorRef twitterResfulActor) {
    this.twitterResfulActor = twitterResfulActor;
    this.tweetProcessor = tweetProcessor;
  }

  public Result tweetTotal() {
    boolean totalTweets = Boolean.parseBoolean(
        request().queryString().getOrDefault("totalTweets", new String[] {"false"})[0]);
    boolean totalTrips = Boolean.parseBoolean(
        request().queryString().getOrDefault("totalTrips", new String[] {"false"})[0]);
    boolean totalUsers = Boolean.parseBoolean(
        request().queryString().getOrDefault("totalUsers", new String[] {"false"})[0]);

    StringBuilder responseBuilder = new StringBuilder();
    responseBuilder.append("Total number of tweets: " + DS.getCollection(Status.class).count());
    responseBuilder.append(System.lineSeparator());

    responseBuilder.append(
        "Total number of twitter trips: " + DS.getCollection(SocialMediaTrip.class).count());
    responseBuilder.append(System.lineSeparator());

    responseBuilder
        .append("Total number of twitter users: " + DS.getCollection(TwitterUser.class).count());
    responseBuilder.append(System.lineSeparator());

    return ok(responseBuilder.toString());
  }

  public Result tweetTrips() {
    List<SocialMediaTrip> trips = getTwitterTrips();

    boolean aggregateNodes = Boolean.parseBoolean(
        request().queryString().getOrDefault("aggregateNodes", new String[] {"false"})[0]);

    if (aggregateNodes) {
      return aggregatedNodes();
    }

    return ok(Json.toJson(trips));
  }

  private Result aggregatedNodes() {
    List<SocialMediaTrip> trips = getTwitterTrips();
    String area = request().queryString().getOrDefault("area", new String[] {"London"})[0];
    int numGrids =
        Integer.parseInt(request().queryString().getOrDefault("numGrids", new String[] {"40"})[0]);

    NodeAggregator aggregator = new NodeAggregator();
    final ResultGraph graph = aggregator.aggregateNodes(Area.getAreaForName(area).getBoundingBox(),
        numGrids, false, trips, false);
    return ok(Json.toJson(new TriangulationResults(graph)));
  }

  public Result aggregateEdges() {
    String area = request().queryString().getOrDefault("area", new String[] {"London"})[0];
    String source = request().queryString().getOrDefault("source", new String[] {"Twitter"})[0];
    int numGridsForNodeBundling =
        Integer.parseInt(request().queryString().getOrDefault("numGrids", new String[] {"40"})[0]);

    int numGridsForEdgeBundling = Integer.parseInt(
        request().queryString().getOrDefault("numGridsEdgeBundling", new String[] {"40"})[0]);

    int angularDifferenceThreshold = Integer.parseInt(
        request().queryString().getOrDefault("angularDifferenceThreshold", new String[] {"15"})[0]);

    boolean useCache = Boolean
        .parseBoolean(request().queryString().getOrDefault("useCache", new String[] {"true"})[0]);

    BundlingParameters parameters = new BundlingParameters();
    parameters.setArea(area);
    parameters.setSource(source);
    parameters.setNumGridsForEdgeBundling(numGridsForEdgeBundling);
    parameters.setNumGridsForNodeBundling(numGridsForNodeBundling);
    parameters.setAngularDifferenceThreshold(angularDifferenceThreshold);
    parameters.setUseCache(useCache);

    List<SocialMediaTrip> trips = getTwitterTrips();

    NodeAggregator aggregator = new NodeAggregator();
    final BoundingBox boundingBox = Area.getAreaForName(area).getBoundingBox();
    final ResultGraph graph =
        aggregator.aggregateNodes(boundingBox, numGridsForNodeBundling, false, trips, false);

    GBEB edgeAggregator = new GBEB(graph.getEdgeList());
    Grid[][] gridArray = boundingBox.gridsArrays(numGridsForEdgeBundling, false);
    edgeAggregator.withGrids(gridArray);
    edgeAggregator.withParameters(parameters);
    edgeAggregator.process();
    return ok(Json.toJson(new TriangulationResults(edgeAggregator)));
  }

  private List<SocialMediaTrip> getTwitterTrips() {
    boolean detailsRequested = Boolean
        .parseBoolean(request().queryString().getOrDefault("details", new String[] {"false"})[0]);
    String area = request().queryString().getOrDefault("area", new String[] {"London"})[0];
    String source = request().queryString().getOrDefault("source", new String[] {"Twitter"})[0];

    Query<SocialMediaTrip> tripQuery = createTripQuery(detailsRequested, area, source);
    List<SocialMediaTrip> trips;
    if (detailsRequested) {
      trips = tripQuery.asList();
      response().setHeader("Content-Disposition", "attachment; filename=twitter-trips.json");
    } else {
      trips = tripQuery.asList();
    }
    return trips;
  }

  public CompletionStage<Result> processorStatus() {
    Future<Object> response =
        ask(tweetProcessor, new TripProcessorProtocol.ActorStatus(true), 1000);

    return FutureConverters.toJava(response)
        .thenApply(r -> ok("Number of runs for tweet processor: " + (int) r));
  }

  public CompletionStage<Result> processorRun() {
    Future<Object> response = ask(tweetProcessor, new TripProcessorProtocol.RunActor(true), 30000);
    return FutureConverters.toJava(response)

        .thenApply(r -> ok("Your are run number: " + (int) r))

        .exceptionally(
            r -> badRequest("Your request not served as a tweet processor is already running."));
  }

  public CompletionStage<Result> restfulActorStatus() {
    Future<Object> response =
        ask(twitterResfulActor, new TwitterRestfulActorProtocol.ActorStatus(true), 1000);

    return FutureConverters.toJava(response)
        .thenApply(r -> ok("Number of runs for tweet processor: " + (int) r));
  }

  public CompletionStage<Result> restfulActorRun() {
    Future<Object> response =
        ask(twitterResfulActor, new TwitterRestfulActorProtocol.RunActor(true), 5000);
    return FutureConverters.toJava(response)

        .thenApply(r -> ok("Your are run number: " + (int) r))

        .exceptionally(
            r -> badRequest("Your request not served as a tweet processor is already running."));
  }

  private Query<SocialMediaTrip> createTripQuery(boolean detailsRequested, String area,
      String source) {
    Query<SocialMediaTrip> query = MorphiaHelper.getDatastore().createQuery(SocialMediaTrip.class);

    query.field("area").equal(Area.getAreaForName(area));

    if (!source.equalsIgnoreCase("All")) {
      query.field("source").equal(Source.getSourceForName(source));
    }

    // strip off details if not explicitly requested
    if (!detailsRequested) {
      query.project("startStatus", false).project("endStatus", false);
      query.limit(30_000);
    }

    return query;
  }
}
