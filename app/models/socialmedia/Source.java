package models.socialmedia;

/**
 * Created by Hein Min Htike on 7/22/2017.
 */
public enum Source {
  TWITTER("Twitter"), FLICKR("Flickr"), ALL("All");

  private String name;

  Source(String name) {
    this.name = name;
  }

  public static Source getSourceForName(String name) {
    for (Source source : values()) {
      if (source.getName().equalsIgnoreCase(name)) {
        return source;
      }
    }

    return ALL;
  }

  public String getName() {
    return name;
  }
}
