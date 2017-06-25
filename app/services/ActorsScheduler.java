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

/**
 * Created by Hein Min Htike on 6/25/2017.
 */
@Singleton
public class ActorsScheduler {

  private final ActorSystem actorSystem;
  private final ExecutionContextExecutor exec;
  private final ActorRef tweetProcessor;

  @Inject
  public ActorsScheduler(ActorSystem actorSystem, ExecutionContextExecutor exec,
      @Named("tweet-processor-actor") ActorRef tweetProcessor) {
    this.actorSystem = actorSystem;
    this.exec = exec;
    this.tweetProcessor = tweetProcessor;

    scheduleTweetProcessor();
  }

  private void scheduleTweetProcessor() {
    actorSystem.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),

        Duration.create(3, TimeUnit.HOURS),

        tweetProcessor,

        new TweetProcessorProtocol.RunActor(false),

        actorSystem.dispatcher(),

        null);
  }
}
