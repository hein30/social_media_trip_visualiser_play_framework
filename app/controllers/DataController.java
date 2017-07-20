package controllers;

import static akka.pattern.Patterns.ask;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import actors.twitter.TweetProcessorProtocol;
import akka.actor.ActorRef;
import models.geography.Area;
import models.trip.TwitterTrip;
import models.tweets.Status;
import models.tweets.TwitterUser;
import mongo.MorphiaHelper;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;
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
  public DataController(@Named("tweet-processor-actor") ActorRef tweetProcessor,
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

    responseBuilder
        .append("Total number of twitter trips: " + DS.getCollection(TwitterTrip.class).count());
    responseBuilder.append(System.lineSeparator());

    responseBuilder
        .append("Total number of twitter users: " + DS.getCollection(TwitterUser.class).count());
    responseBuilder.append(System.lineSeparator());

    return ok(responseBuilder.toString());
  }

  public Result tweetTrips() {
    boolean detailsRequested = Boolean
        .parseBoolean(request().queryString().getOrDefault("details", new String[] {"false"})[0]);
    String area = request().queryString().getOrDefault("area", new String[] {"London"})[0];

    Query<TwitterTrip> tripQuery = createTripQuery(detailsRequested, area);
    List<TwitterTrip> trips;
    if (detailsRequested) {
      trips = tripQuery.asList();
      response().setHeader("Content-Disposition", "attachment; filename=twitter-trips.json");
    } else {
      final FindOptions options = new FindOptions();
       options.limit(10);
      tripQuery.field("distanceInMeter").greaterThan(10000);
      trips = tripQuery.asList(options);
    }
    return ok(Json.toJson(trips));
  }

  public CompletionStage<Result> processorStatus() {
    Future<Object> response =
        ask(tweetProcessor, new TweetProcessorProtocol.ActorStatus(true), 1000);

    return FutureConverters.toJava(response)
        .thenApply(r -> ok("Number of runs for tweet processor: " + (int) r));
  }

  public CompletionStage<Result> processorRun() {
    Future<Object> response = ask(tweetProcessor, new TweetProcessorProtocol.RunActor(true), 30000);
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

  private Query<TwitterTrip> createTripQuery(boolean detailsRequested, String area) {
    Query<TwitterTrip> query = MorphiaHelper.getDatastore().createQuery(TwitterTrip.class);

    query.field("area").equal(Area.getAreaForName(area));

    // strip off details if not explicitly requested
    if (!detailsRequested) {
      query.project("startStatus", false).project("endStatus", false);
    }

    return query;
  }


}
