package akka.workshop.example.java.actors;

import akka.actor.UntypedActor;
import akka.event.EventStream;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.workshop.example.java.utils.forex.RateStreamBuilder;
import akka.workshop.example.java.utils.forex.SymbolStream;

/**
 * User: liorpe
 * Date: 9/2/13
 */
public class RateStreamer extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private SymbolStream lastRates;

    EventStream eventStream;

    public RateStreamer(EventStream stream) {
        this.eventStream = stream;
    }

    @Override
    public void onReceive(Object message) {
        //time ticker
        log.debug("Received message from external rates feed source");
        if (message.equals("Tick")) {
            if (lastRates == null) {
                lastRates = RateStreamBuilder.initialStream();
            }
            final SymbolStream event = RateStreamBuilder.buildTimelyStream(lastRates);
            eventStream.publish(event);
            lastRates = event;
        } else {
            unhandled(message);
        }
    }

    public static class RateStreamerCreator implements Creator<RateStreamer> {
        EventStream stream;

        public RateStreamerCreator(EventStream stream) {
            this.stream = stream;
        }

        @Override
        public RateStreamer create() {
            return new RateStreamer(stream);
        }


    }
}

