package controllers;

import java.util.List;

import com.typesafe.config.ConfigFactory;

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
}
