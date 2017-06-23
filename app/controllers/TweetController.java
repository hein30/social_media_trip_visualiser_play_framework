package controllers;

import static play.mvc.Results.ok;

import models.tweets.Status;
import mongo.MorphiaHelper;
import play.mvc.Result;

/**
 * Controller to access tweet related endponits.
 *
 * @author Hein Min Htike
 */
public class TweetController {

  public Result count() {
    return ok("Total number of tweets: "
        + MorphiaHelper.getDatastore().getCollection(Status.class).count());
  }
}
