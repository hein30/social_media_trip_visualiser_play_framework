package services.twitter;

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
    final String consumerKey = ConfigFactory.load().getString("twitter.consumerKey");
    final String consumerKeySecret = ConfigFactory.load().getString("twitter.consumerKeySecret");
    final String accessToken = ConfigFactory.load().getString("twitter.accessToken");
    final String accessTokenSecret = ConfigFactory.load().getString("twitter.accessTokenSecret");

    return new ConfigurationBuilder()

        .setOAuthConsumerKey(consumerKey)

        .setOAuthConsumerSecret(consumerKeySecret)

        .setOAuthAccessToken(accessToken)

        .setOAuthAccessTokenSecret(accessTokenSecret).build();
  }
}
