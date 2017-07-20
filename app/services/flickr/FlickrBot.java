package services.flickr;


import java.util.List;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Base class for flickr bots.
 */
public class FlickrBot {
  final Config config = ConfigFactory.load();

  protected Flickr buildFlickr() {
    final String apiKey = config.getString("flickr.apiKey");
    final String sharedSecret = config.getString("flickr.sharedSecret");

    return new Flickr("b0cecbb71ec3c356567fe8af4fdba3fc", "fb4f4806ffe4abf0", new REST());
  }

  protected List<Double> coordinates() {
    return config.getDoubleList("twitter.filter.coordinates");
  }

}
