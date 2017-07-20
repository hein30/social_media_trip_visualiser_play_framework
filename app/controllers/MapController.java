package controllers;

import models.geography.Area;
import models.geography.BoundingBox;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.gridMap;
import views.html.map;

/**
 * Created by Hein Min Htike on 7/20/2017.
 */
public class MapController extends Controller {

  public Result map() {
    return ok(map.render());
  }

  public Result gridMap() {
    return ok(gridMap.render());
  }

  public Result grids() {
    String area = request().queryString().getOrDefault("area", new String[] {"LONDON"})[0];
    int numGrids =
        Integer.parseInt(request().queryString().getOrDefault("numGrids", new String[] {"50"})[0]);

    BoundingBox originalBox = Area.getAreaForName(area).getBoundingBox();

    return ok(Json.toJson(originalBox.grids(numGrids, true)));
  }
}
