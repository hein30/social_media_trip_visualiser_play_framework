package services.twitter.stream;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.typesafe.config.ConfigFactory;

import play.Logger;
import play.inject.ApplicationLifecycle;
import twitter4j.FilterQuery;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Thread that will open a stream connection to twitter Api.
 *
 * @author Hein Min Htike
 */

@Singleton
public class TwitterStreamBot {

  private static final Logger.ALogger LOGGER = Logger.of(TwitterStreamBot.class);

  private final ApplicationLifecycle applicationLifecycle;

  @Inject
  public TwitterStreamBot(ApplicationLifecycle applicationLifecycle) throws TwitterException {
    this.applicationLifecycle = applicationLifecycle;

    LOGGER.info("Twitter stream bot started");

    TwitterStreamFactory streamFactory =
        new TwitterStreamFactory(getTwitterConfiguration().build());

    TwitterStream stream = streamFactory.getInstance();

    LOGGER.info("User id: {}", stream.getId());
    LOGGER.info("User name: {}", stream.getScreenName());

    StatusListener statusListener = new PersistingStatusListener();
    stream.addListener(statusListener);

    addFilter(stream);

    addStopHook(applicationLifecycle, statusListener);
  }

  private ConfigurationBuilder getTwitterConfiguration() {
    final String consumerKey = ConfigFactory.load().getString("twitter.consumerKey");
    final String consumerKeySecret = ConfigFactory.load().getString("twitter.consumerKeySecret");
    final String accessToken = ConfigFactory.load().getString("twitter.accessToken");
    final String accessTokenSecret = ConfigFactory.load().getString("twitter.accessTokenSecret");

    return new ConfigurationBuilder()

        .setOAuthConsumerKey(consumerKey)

        .setOAuthConsumerSecret(consumerKeySecret)

        .setOAuthAccessToken(accessToken)

        .setOAuthAccessTokenSecret(accessTokenSecret);
  }

  private void addFilter(TwitterStream stream) throws TwitterException {
    final FilterQuery query = new FilterQuery();
    addLocationQuery(query);
    stream.filter(query);
  }

  private void addLocationQuery(FilterQuery query) throws TwitterException {
    List<Double> pointsList = ConfigFactory.load().getDoubleList("twitter.filter.coordinates");

    if (pointsList.size() != 4) {
      LOGGER.error("List of coordinates not in correct format: {}", pointsList);
      throw new TwitterException("Coordinates are fucked.");
    }

    final double[][] coordinates =
        {{pointsList.get(0), pointsList.get(1)}, {pointsList.get(2), pointsList.get(3)}};

    query.locations(coordinates);
  }

  private void addStopHook(ApplicationLifecycle applicationLifecycle,
      StatusListener statusListener) {
    applicationLifecycle.addStopHook(() -> {
      LOGGER.info("Twitter stream bot closing. Number of tweets saved: {}.",
          ((PersistingStatusListener) statusListener).getCount());
      return CompletableFuture.completedFuture(null);
    });
  }
}
