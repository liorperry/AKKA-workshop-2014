package akka.workshop.example.java.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ConsistentHashingRouter;
import akka.util.Timeout;
import akka.workshop.example.java.messages.*;
import akka.workshop.example.java.utils.forex.SymbolStream;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

//import static akka.actor.SupervisorStrategy.*;

public class TradingProcessor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static final ConsistentHashingRouter.ConsistentHashMapper hashMapper = new ConsistentHashingRouter.ConsistentHashMapper() {
       @Override
       public Object hashKey(Object message) {
         if (message instanceof AccountCommand) {
           return ((AccountCommand) message).getName();
         } else {
           return null;
         }
       }
     };

    public void onReceive(Object message) throws Exception {
        log.debug(message.toString());
        if (message instanceof AccountStatusQuery) {
            final AccountStatusQuery query = (AccountStatusQuery) message;
            final ActorRef actorRef = getContext().getChild(query.getName());
            //send-reply message
            Timeout timeout = new Timeout(Duration.create(100, TimeUnit.MILLISECONDS));
            Future<Object> future = ask(actorRef, query, timeout);
            getSender().tell(Await.result(future, timeout.duration()), getSelf());
        } else if (message instanceof AccountCalibration) {
            final ActorRef actorRef = getContext().getChild(((AccountCalibration) message).getName());
            //unsubscribe from rates events
            getContext().system().eventStream().unsubscribe(actorRef,SymbolStream.class);
            //
        } else if (message instanceof AccountCreation) {
            final AccountCreation accountCreation = (AccountCreation) message;
            //create account actor
            final ActorRef actorRef = getContext().actorOf(Props.create(AccountProcessor.class), accountCreation.getName());
            //subscribe to rates events
            getContext().system().eventStream().subscribe(actorRef,SymbolStream.class);

            //send-reply message
            Timeout timeout = new Timeout(Duration.create(100, TimeUnit.MILLISECONDS));
            Future<Object> future = ask(actorRef, accountCreation, timeout);
            getSender().tell(Await.result(future, timeout.duration()), getSelf());
        } else if (message instanceof DealOpening) {
            final DealOpening dealOpening = (DealOpening) message;
            final ActorRef actorRef = getContext().getChild(dealOpening.getAccount());
            //send-reply message
            Timeout timeout = new Timeout(Duration.create(100, TimeUnit.MILLISECONDS));
            Future<Object> future = ask(actorRef, dealOpening, timeout);
            getSender().tell(Await.result(future, timeout.duration()), getSelf());
        } else if (message instanceof AccountDeposit) {
            final ActorRef actorRef = getContext().getChild(((AccountDeposit) message).getAccount());
            //send-reply message
            Timeout timeout = new Timeout(Duration.create(100, TimeUnit.MILLISECONDS));
            Future<Object> future = ask(actorRef, message, timeout);
            getSender().tell(Await.result(future, timeout.duration()), getSelf());
        } else {
            unhandled(message);
        }
    }

/*
    private static SupervisorStrategy strategy =
      new OneForOneStrategy(10, Duration.create("1 minute"),
        new Function<Throwable, SupervisorStrategy.Directive>() {
          @Override
          public SupervisorStrategy.Directive apply(Throwable t) {
            if(t instanceof ArithmeticException) {
              return resume();
            } else if (t instanceof NullPointerException) {
              return restart();
            } else if (t instanceof IllegalArgumentException) {
              return stop();
            } else {
              return escalate();
            }
          }
        });
*/

/*
    @Override
    public SupervisorStrategy supervisorStrategy() {
      return strategy;
    }
*/

}