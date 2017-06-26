package controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.typesafe.config.ConfigFactory;

import models.tweets.Status;
import models.tweets.TwitterUser;
import mongo.MorphiaHelper;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.map;

/**
 * This controller contains an action to handle HTTP requests to the application's home page.
 */
public class HomeController extends Controller {

  private static final List<Double> POINTS_LIST =
      ConfigFactory.load().getDoubleList("twitter.filter.coordinates");

  /**
   * An action that renders an HTML page with a welcome message. The configuration in the
   * <code>routes</code> file means that this method will be called when the application receives a
   * <code>GET</code> request with a path of <code>/</code>.
   */
  public Result index() {
    return ok(index.render("Your new application is ready."));
  }

  public Result map() {
    return ok(map.render());
  }


  public Result usersFromTweets() {
    Map<String, List<Status>> map = MorphiaHelper.getDatastore().createQuery(Status.class).asList()
        .stream().collect(Collectors.groupingBy(Status::getScreenName));

    map.entrySet().forEach(entry -> {
      Status status = entry.getValue().get(0);

      TwitterUser user = new TwitterUser(status.getUserId(), status.getScreenName(), POINTS_LIST);
      user.save();
    });


    return ok();
  }

}
