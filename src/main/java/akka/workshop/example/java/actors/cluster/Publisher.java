package akka.workshop.example.java.actors.cluster;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;

/**
 * Created by user on 11/02/14.
 */
public class Publisher extends UntypedActor {

  // activate the extension
  ActorRef mediator =
    DistributedPubSubExtension.get(getContext().system()).mediator();

  public void onReceive(Object msg) {
    if (msg instanceof String) {
      String in = (String) msg;
      String out = in.toUpperCase();
      mediator.tell(new DistributedPubSubMediator.Publish("content", out),
        getSelf());
    } else {
      unhandled(msg);
    }
  }
}
