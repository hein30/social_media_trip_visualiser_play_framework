package models.geography;

import models.trip.GeoLocation;

/**
 * Enum class for the locations (i.e. cardiff,london, etc.)
 */
public enum Area {

  LONDON("London", new BoundingBox(-0.3515, 51.3849, 0.1483, 51.6723)),

  CARDIFF("Cardiff", new BoundingBox(-3.282382, 51.443575, -3.116437, 51.560906)),

  OTHERS("Others", new BoundingBox(0, 0, 0, 0));

  private String area;
  private BoundingBox boundingBox;

  Area(String area, BoundingBox boundingBox) {
    this.area = area;
    this.boundingBox = boundingBox;
  }

  public static Area getAreaForLocation(GeoLocation location) {
    for (Area area : values()) {

      if (area.getBoundingBox().isLocationInBox(location)) {
        return area;
      }
    }
    return OTHERS;
  }

  public static Area getAreaForName(String name) {
    for (Area area : values()) {
      if (area.getArea().equalsIgnoreCase(name)) {
        return area;
      }
    }
    return OTHERS;
  }

  public String getArea() {
    return area;
  }

  public BoundingBox getBoundingBox() {
    return boundingBox;
  }

}
