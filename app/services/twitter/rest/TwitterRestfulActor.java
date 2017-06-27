package services.twitter.rest;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.japi.pf.ReceiveBuilder;
import play.Logger;
import scala.PartialFunction;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;

/**
 * Created by Hein Min Htike on 6/26/2017.
 */
public class TwitterRestfulActor extends AbstractActor {

  private static final Logger.ALogger LOGGER = Logger.of(TwitterRestfulActor.class);

  private final PartialFunction<Object, BoxedUnit> RUNNING;
  private final PartialFunction<Object, BoxedUnit> IDLE;
  private final TwitterRestBot twitterRestBot;
  private int totalRuns;

  public TwitterRestfulActor() {

    twitterRestBot = new TwitterRestBot();

    RUNNING = ReceiveBuilder

        .match(TwitterRestfulActorProtocol.ActorStatus.class, this::getStatus).build();

    IDLE = ReceiveBuilder

        .match(TwitterRestfulActorProtocol.RunActor.class, this::runActor)

        .match(TwitterRestfulActorProtocol.ActorStatus.class, this::getStatus).build();

    // start from idle stage.
    receive(IDLE);
  }

  public static Props props() {
    return Props.create(TwitterRestfulActor.class);
  }

  private void runActor(TwitterRestfulActorProtocol.RunActor p) {
    totalRuns++;
    getContext().become(RUNNING);
    LOGGER.info("TwitterRestfulActor's runActor method started.");

    runRestBotAsync(p);

    if (p.isGetResponse()) {
      sender().tell(totalRuns, self());
    }
  }

  private void getStatus(TwitterRestfulActorProtocol.ActorStatus p) {
    LOGGER.debug("Status being asked.");
    if (p.isGetResponse()) {
      sender().tell(totalRuns, self());
    }
  }

  private void runRestBotAsync(TwitterRestfulActorProtocol.RunActor p) {
    Future<TwitterRestfulActorProtocol.RunActor> future = Futures.future(() -> {
      twitterRestBot.processUsers();
      return p;
    }, getContext().dispatcher());

    FutureConverters.toJava(future).thenApply(s -> {
      LOGGER.info("Running twitter rest bot asynchronously completed.");
      getContext().become(IDLE);
      return s;
    }).exceptionally(e -> {
      LOGGER.error("Something fucked up while running twitter rest bot.", e.getCause());
      return null;
    });
  }
}
