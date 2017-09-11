package services;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import services.flickr.userNames.FlickrUserNameActorProtocol;
import services.flickr.userPhotos.FlickrUserPhotoActorProtocol;
import services.trips.TripProcessorProtocol;
import services.twitter.rest.TwitterRestfulActorProtocol;

/**
 * Created by Hein Min Htike on 6/25/2017.
 */
@Singleton
public class ActorsScheduler {

  private final ActorSystem actorSystem;
  private final ExecutionContextExecutor exec;
  private final ActorRef tripProcessor;
  private final ActorRef twitterRestfulActor;
  private final ActorRef flickrUserNameActor;
  private final ActorRef flickrUserPhotoActor;

  @Inject
  public ActorsScheduler(ActorSystem actorSystem, ExecutionContextExecutor exec,
      @Named("trip-processor-actor") ActorRef tripProcessor,
      @Named("twitter-restful-bot-actor") ActorRef twitterRestfulActor,
      @Named("flickr-username-actor") ActorRef flickrUserNameActor,
      @Named("flickr-userphoto-actor") ActorRef flickrUserPhotoActor) {

    this.actorSystem = actorSystem;
    this.exec = exec;
    this.tripProcessor = tripProcessor;
    this.twitterRestfulActor = twitterRestfulActor;
    this.flickrUserNameActor = flickrUserNameActor;
    this.flickrUserPhotoActor = flickrUserPhotoActor;

    scheduleTripProcessor();

    scheduleTwitterRestfulActor();

    scheduleFlickrUserNameActor();

    scheduleFlickrUserPhotoActor();
  }

  private void scheduleFlickrUserPhotoActor() {

    actorSystem.scheduler().schedule(Duration.create(0, TimeUnit.MINUTES),

        Duration.create(15, TimeUnit.MINUTES),

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

  private void scheduleTripProcessor() {
    actorSystem.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),

        Duration.create(3, TimeUnit.HOURS),

        tripProcessor,

        new TripProcessorProtocol.RunActor(false),

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
