package akka.workshop.example.java;

import akka.actor.*;
import akka.workshop.example.java.actors.AccountProcessor;
import akka.workshop.example.java.actors.RateStreamer;
import akka.workshop.example.java.actors.TradingProcessor;
import akka.workshop.example.java.utils.forex.SymbolStream;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by user on 11/02/14.
 */
public class SingleNodeSystem {
    public static void main(String[] args) {
               // Create an Akka system
        ActorSystem system = ActorSystem.create("SingleNodeTradingSystem");

        // create the router actor
        ActorRef master = system.actorOf(Props.create(TradingProcessor.class, 10));
        final ActorRef accountProcessor = system.actorOf(Props.create(AccountProcessor.class));

        system.eventStream().subscribe(accountProcessor, SymbolStream.class);
        //This will schedule to send the Tick-message
        //to the tickActor after 0ms repeating every 50ms
        //tick actor
        final ActorRef rateStreamer = system.actorOf(Props.create(new RateStreamer.RateStreamerCreator(system.eventStream())));

        //begin ticker & get cancellable
        final Cancellable tick = system.scheduler().schedule(Duration.Zero(),
                Duration.create(50, TimeUnit.MILLISECONDS), rateStreamer, "Tick",
                system.dispatcher(), null);


    }
}
