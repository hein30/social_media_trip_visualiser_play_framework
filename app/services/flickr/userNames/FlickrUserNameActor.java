package services.flickr.userNames;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

/**
 * Created by Hein Min Htike on 7/18/2017.
 */
public class FlickrUserNameActor extends AbstractActor {

  private static final play.Logger.ALogger LOGGER = play.Logger.of(FlickrUserNameActor.class);

  private final FlickrUserNamesBot userNamesBot;

  public FlickrUserNameActor() {
    userNamesBot = new FlickrUserNamesBot();

    receive(ReceiveBuilder.match(FlickrUserNameActorProtocol.RunActor.class, this::runActor)

        .build());
  }

  public static Props props() {
    return Props.create(FlickrUserNameActor.class);
  }

  private void runActor(FlickrUserNameActorProtocol.RunActor p) {

  }
}
