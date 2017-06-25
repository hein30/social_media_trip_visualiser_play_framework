package services.twitter.rest;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.Logger;
import play.inject.ApplicationLifecycle;
import services.twitter.TwitterBot;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by Hein Min Htike on 6/23/2017.
 */
@Singleton
public class TwitterRestBot extends TwitterBot {

  private static final Logger.ALogger LOGGER = Logger.of(TwitterRestBot.class);
  private final ApplicationLifecycle applicationLifecycle;

  @Inject
  public TwitterRestBot(ApplicationLifecycle applicationLifecycle) throws TwitterException {
    this.applicationLifecycle = applicationLifecycle;

    LOGGER.info("Twitter rest bot started");

    Twitter twitter = new TwitterFactory(buildConfig()).getInstance();

    Paging paging = new Paging();
    paging.setCount(1000);
    ResponseList<Status> list = twitter.getUserTimeline("MarshaMilan", paging);

    System.out.println("");


  }
}
