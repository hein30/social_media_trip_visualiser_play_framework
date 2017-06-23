package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.tweets.Tweet;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.AtomicCounter;
import services.Counter;

/**
 * This controller demonstrates how to use dependency injection to bind a component into a
 * controller class. The class contains an action that shows an incrementing count to users. The
 * {@link Counter} object is injected by the Guice dependency injection system.
 */
@Singleton
public class CountController extends Controller {

  private final Counter counter;

  @Inject
  public CountController(AtomicCounter counter) {
    this.counter = counter;
  }

  /**
   * An action that responds with the {@link Counter}'s current count. The result is plain text.
   * This action is mapped to <code>GET</code> requests with a path of <code>/count</code> requests
   * by an entry in the <code>routes</code> config file.
   */
  public Result count() {
    return ok(Integer.toString(counter.nextCount()));
  }


  public Result addTweet() {
    Tweet tweet = new Tweet();
    tweet.setName(Integer.toString(counter.nextCount()));

    tweet.save();
    return ok(Json.toJson(tweet.query()));
  }
}
