package services.flickr.userNames;

import com.flickr4java.flickr.FlickrException;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.Future;

/**
 * Created by Hein Min Htike on 7/18/2017.
 */
public class FlickrUserNameActor extends AbstractActor {

  private static final play.Logger.ALogger LOGGER = play.Logger.of(FlickrUserNameActor.class);
  private final FlickrUserNamesBot userNamesBot;
  private int callsLeft;

  public FlickrUserNameActor() {
    userNamesBot = new FlickrUserNamesBot();

    receive(ReceiveBuilder.match(FlickrUserNameActorProtocol.RunActor.class, this::runActor)

        .build());
  }

  public static Props props() {
    return Props.create(FlickrUserNameActor.class);
  }

  private void runActor(FlickrUserNameActorProtocol.RunActor p) {

    Future<FlickrUserNameActorProtocol.RunActor> future = Futures.future(() -> {

      for (int i = 0; i < 3600; i++) {
        try {
          userNamesBot.getPhotos();
        } catch (FlickrException e) {
          LOGGER.error("Flickr user name actor exception", e);
          break;
        }
      }

      return p;
    }, getContext().dispatcher());
  }
}
