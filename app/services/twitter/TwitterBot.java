package services.twitter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Base class for Twitter's stream and restful bots.
 *
 *
 * @author Hein Min Htike
 */
public abstract class TwitterBot {

  protected Configuration buildConfig() {
    final Config config = ConfigFactory.load();
    final String consumerKey = config.getString("twitter.consumerKey");
    final String consumerKeySecret = config.getString("twitter.consumerKeySecret");
    final String accessToken = config.getString("twitter.accessToken");
    final String accessTokenSecret = config.getString("twitter.accessTokenSecret");

    return new ConfigurationBuilder()

        .setOAuthConsumerKey(consumerKey)

        .setOAuthConsumerSecret(consumerKeySecret)

        .setOAuthAccessToken(accessToken)

        .setOAuthAccessTokenSecret(accessTokenSecret).build();
  }
}
