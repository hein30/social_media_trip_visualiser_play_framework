package controllers;

import static akka.pattern.Patterns.ask;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import org.mongodb.morphia.query.Query;

import actors.twitter.TweetProcessorProtocol;
import akka.actor.ActorRef;
import models.trip.TwitterTrip;
import models.tweets.Status;
import mongo.MorphiaHelper;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;


/**
 * Controller to access tweet related end-ponits.
 *
 * @author Hein Min Htike
 */
public class DataController extends Controller {

  private ActorRef tweetProcessor;

  @Inject
  public DataController(@Named("tweet-processor-actor") ActorRef tweetProcessor) {
    this.tweetProcessor = tweetProcessor;
  }

  public Result tweetTotal() {
    return ok("Total number of tweets: "
        + MorphiaHelper.getDatastore().getCollection(Status.class).count());
  }

  public Result tweetTripTotal() {
    return ok("Total number of trips from tweets: "
        + MorphiaHelper.getDatastore().getCollection(TwitterTrip.class).count());
  }

  public Result tweetTrips() {
    boolean detailsRequested = Boolean
        .parseBoolean(request().queryString().getOrDefault("details", new String[] {"false"})[0]);

    List<TwitterTrip> trips = createTripQuery(detailsRequested).asList();
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

  private Query<TwitterTrip> createTripQuery(boolean detailsRequested) {
    Query<TwitterTrip> query = MorphiaHelper.getDatastore().createQuery(TwitterTrip.class);
    query.order("-endStatus.createdAt");

    // strip off details if not explicitly requested
    if (!detailsRequested) {
      query.project("startStatus", false).project("endStatus", false);
    }

    return query;
  }


}
