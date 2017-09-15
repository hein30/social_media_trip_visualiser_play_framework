import com.google.inject.AbstractModule;

import play.libs.akka.AkkaGuiceSupport;
import services.ActorsScheduler;
import services.flickr.userNames.FlickrUserNameActor;
import services.flickr.userPhotos.FlickrUserPhotoActor;
import services.trips.TripProcessorActor;
import services.twitter.rest.TwitterRestfulActor;
import services.twitter.stream.TwitterStreamBot;

/**
 * This class is a Guice module that tells Guice how to bind several different types. This Guice
 * module is created when the Play application starts.
 *
 * Play will automatically use any class called `Module` that is in the root package. You can create
 * modules in other locations by adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule implements AkkaGuiceSupport {

  @Override
  public void configure() {
    bindActor(TripProcessorActor.class, "trip-processor-actor");
    bindActor(TwitterRestfulActor.class, "twitter-restful-bot-actor");
    bindActor(FlickrUserNameActor.class, "flickr-username-actor");
    bindActor(FlickrUserPhotoActor.class, "flickr-userphoto-actor");

    bind(ActorsScheduler.class).asEagerSingleton();
    // Start the TwitterStreamBot class
    bind(TwitterStreamBot.class).asEagerSingleton();
  }

}
