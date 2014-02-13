package akka.workshop.example.java.actors.cluster;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.contrib.pattern.DistributedPubSubExtension;
/**
 * Created by user on 11/02/14.
 */
public class Subscriber extends UntypedActor {
  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  public Subscriber() {
    ActorRef mediator =
      DistributedPubSubExtension.get(getContext().system()).mediator();
    // subscribe to the topic named "content"
    mediator.tell(new DistributedPubSubMediator.Subscribe("content", getSelf()),
      getSelf());
  }

  public void onReceive(Object msg) {
    if (msg instanceof String)
      log.info("Got: {}", msg);
    else if (msg instanceof DistributedPubSubMediator.SubscribeAck)
      log.info("subscribing");
    else
      unhandled(msg);
  }
}
