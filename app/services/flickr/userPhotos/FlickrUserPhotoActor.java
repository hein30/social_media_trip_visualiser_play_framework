package services.flickr.userPhotos;

import com.flickr4java.flickr.FlickrException;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.Future;

public class FlickrUserPhotoActor extends AbstractActor {

  private static final play.Logger.ALogger LOGGER = play.Logger.of(FlickrUserPhotoActor.class);
  private final FlickrUserPhotosBot userPhotosBot;

  public FlickrUserPhotoActor() {
    userPhotosBot = new FlickrUserPhotosBot();

    receive(ReceiveBuilder.match(FlickrUserPhotoActorProtocol.RunActor.class, this::runActor)

        .build());
  }

  public static Props props() {
    return Props.create(FlickrUserPhotoActor.class);
  }

  private void runActor(FlickrUserPhotoActorProtocol.RunActor p) {

    Future<FlickrUserPhotoActorProtocol.RunActor> future = Futures.future(() -> {

      try {
        userPhotosBot.processUsers();
      } catch (FlickrException e) {
        LOGGER.error("Flickr user name actor exception", e);
      }

      return p;
    }, getContext().dispatcher());
  }
}
