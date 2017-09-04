package services;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import actors.twitter.TweetProcessorProtocol;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import services.flickr.userNames.FlickrUserNameActorProtocol;
import services.flickr.userPhotos.FlickrUserPhotoActorProtocol;
import services.twitter.rest.TwitterRestfulActorProtocol;

/**
 * Created by Hein Min Htike on 6/25/2017.
 */
@Singleton
public class ActorsScheduler {

  private final ActorSystem actorSystem;
  private final ExecutionContextExecutor exec;
  private final ActorRef tweetProcessor;
  private final ActorRef twitterRestfulActor;
  private final ActorRef flickrUserNameActor;
  private final ActorRef flickrUserPhotoActor;

  @Inject
  public ActorsScheduler(ActorSystem actorSystem, ExecutionContextExecutor exec,
      @Named("tweet-processor-actor") ActorRef tweetProcessor,
      @Named("twitter-restful-bot-actor") ActorRef twitterRestfulActor,
      @Named("flickr-username-actor") ActorRef flickrUserNameActor,
      @Named("flickr-userphoto-actor") ActorRef flickrUserPhotoActor) {

    this.actorSystem = actorSystem;
    this.exec = exec;
    this.tweetProcessor = tweetProcessor;
    this.twitterRestfulActor = twitterRestfulActor;
    this.flickrUserNameActor = flickrUserNameActor;
    this.flickrUserPhotoActor = flickrUserPhotoActor;

    scheduleTweetProcessor();

    scheduleTwitterRestfulActor();

    scheduleFlickrUserNameActor();

    scheduleFlickrUserPhotoActor();
  }

  private void scheduleFlickrUserPhotoActor() {

    actorSystem.scheduler().schedule(Duration.create(61, TimeUnit.MINUTES),

        Duration.create(61, TimeUnit.MINUTES),

        flickrUserPhotoActor,

        new FlickrUserPhotoActorProtocol.RunActor(),

        actorSystem.dispatcher(),

        null);
  }

  private void scheduleFlickrUserNameActor() {

    actorSystem.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),

        Duration.create(61, TimeUnit.MINUTES),

        flickrUserNameActor,

        new FlickrUserNameActorProtocol.RunActor(),

        actorSystem.dispatcher(),

        null);
  }

  private void scheduleTweetProcessor() {
    actorSystem.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),

        Duration.create(3, TimeUnit.HOURS),

        tweetProcessor,

        new TweetProcessorProtocol.RunActor(false),

        actorSystem.dispatcher(),

        null);
  }

  private void scheduleTwitterRestfulActor() {
    actorSystem.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),

        Duration.create(15, TimeUnit.MINUTES),

        twitterRestfulActor,

        new TwitterRestfulActorProtocol.RunActor(false),

        actorSystem.dispatcher(),

        null);
  }
}
