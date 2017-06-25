package actors.twitter;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.japi.pf.ReceiveBuilder;
import play.Logger;
import scala.PartialFunction;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;

/**
 * An actor class to process tweets and turn them into {@link models.trip.Trip}.
 *
 * @author Hein Min Htike
 */
public class TweetProcessorActor extends AbstractLoggingActor {
  private static final Logger.ALogger LOGGER = Logger.of(TweetProcessorActor.class);

  private final PartialFunction<Object, BoxedUnit> RUNNING;
  private final PartialFunction<Object, BoxedUnit> IDLE;

  private int totalRuns;

  public TweetProcessorActor() {

    RUNNING = ReceiveBuilder

        .match(TweetProcessorProtocol.ActorStatus.class, this::getStatus).build();

    IDLE = ReceiveBuilder

        .match(TweetProcessorProtocol.RunActor.class, this::runActor)

        .match(TweetProcessorProtocol.ActorStatus.class, this::getStatus).build();

    // start from idle stage.
    receive(IDLE);
  }

  /**
   * Prop factory for this actor.
   *
   * @return @link {@link Props} for @link {@link TweetProcessorActor}.
   */
  public static Props props() {
    return Props.create(TweetProcessorActor.class);
  }

  private void runActor(TweetProcessorProtocol.RunActor p) {
    totalRuns++;
    getContext().become(RUNNING);
    LOGGER.info("TweetProcessorActor's runActor method started.");

    processTweetsAsyncAndUpdateState(p);

    if (p.isGetResponse()) {
      sender().tell(totalRuns, self());
    }
  }

  private void getStatus(TweetProcessorProtocol.ActorStatus p) {
    LOGGER.debug("Status being asked.");
    if (p.isGetResponse()) {
      sender().tell(totalRuns, self());
    }
  }

  private void processTweetsAsyncAndUpdateState(TweetProcessorProtocol.RunActor p) {
    Future<TweetProcessorProtocol.RunActor> future = Futures.future(() -> {
      TweetProcessor processor = new TweetProcessor();
      processor.processTweets();
      return p;
    }, getContext().dispatcher());

    FutureConverters.toJava(future).thenApply(s -> {
      LOGGER.info("Processing tweets asynchronously completed.");
      getContext().become(IDLE);
      return s;
    });
  }
}
