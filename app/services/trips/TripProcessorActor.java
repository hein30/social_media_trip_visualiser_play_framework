package services.trips;

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
public class TripProcessorActor extends AbstractLoggingActor {
  private static final Logger.ALogger LOGGER = Logger.of(TripProcessorActor.class);

  private final PartialFunction<Object, BoxedUnit> RUNNING;
  private final PartialFunction<Object, BoxedUnit> IDLE;

  private int totalRuns;

  public TripProcessorActor() {

    RUNNING = ReceiveBuilder

        .match(TripProcessorProtocol.ActorStatus.class, this::getStatus).build();

    IDLE = ReceiveBuilder

        .match(TripProcessorProtocol.RunActor.class, this::runActor)

        .match(TripProcessorProtocol.ActorStatus.class, this::getStatus).build();

    // start from idle stage.
    receive(IDLE);
  }

  /**
   * Prop factory for this actor.
   *
   * @return @link {@link Props} for @link {@link TripProcessorActor}.
   */
  public static Props props() {
    return Props.create(TripProcessorActor.class);
  }

  private void runActor(TripProcessorProtocol.RunActor p) {
    totalRuns++;
    getContext().become(RUNNING);
    LOGGER.info("TripProcessorActor's runActor method started.");

    processTweetsAsyncAndUpdateState(p);

    if (p.isGetResponse()) {
      sender().tell(totalRuns, self());
    }
  }

  private void getStatus(TripProcessorProtocol.ActorStatus p) {
    LOGGER.debug("Status being asked.");
    if (p.isGetResponse()) {
      sender().tell(totalRuns, self());
    }
  }

  private void processTweetsAsyncAndUpdateState(TripProcessorProtocol.RunActor p) {
    Future<TripProcessorProtocol.RunActor> future = Futures.future(() -> {
      TripProcessor processor = new TripProcessor();
      processor.startTripProcessor();
      return p;
    }, getContext().dispatcher());

    FutureConverters.toJava(future).thenApply(s -> {
      LOGGER.info("Processing tweets asynchronously completed.");
      getContext().become(IDLE);
      return s;
    }).exceptionally(r -> {
      LOGGER.error("Processing tweets asynchronously ran into problem.", r.getCause());
      getContext().become(IDLE);
      return null;
    });
  }
}
