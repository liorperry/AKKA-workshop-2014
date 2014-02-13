package akka.workshop.example.java.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.workshop.example.java.messages.DealOpeningWithPosition;
import akka.workshop.example.java.utils.forex.Deal;
import akka.workshop.example.java.utils.forex.DealServices;
import akka.workshop.example.java.utils.forex.SymbolPosition;
import akka.workshop.example.java.utils.forex.SymbolStream;

import java.math.BigDecimal;
import java.util.Date;

public class DealProcessor extends UntypedActor implements DealServices{
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private Deal deal;

    public void onReceive(Object message) {
        log.debug(message.toString());
        if (message instanceof SymbolStream) {
            final BigDecimal value = applyRates((SymbolStream) message);
            getSender().tell(value, getSelf());
        } else if (message instanceof DealOpeningWithPosition) {
            final DealOpeningWithPosition dealOpening = (DealOpeningWithPosition) message;
            deal = addDeal(dealOpening.getSymbol(), dealOpening.getAmount(), dealOpening.getType(), dealOpening.getOpenDate());
            getSender().tell(deal, getSelf());
        } else {
            unhandled(message);
        }
    }

    @Override
    public Deal addDeal(SymbolPosition symbol, int amount, Deal.DealType type, Date date) {
        return new Deal(symbol,amount,type, date);
    }

    @Override
    public Deal closeDeal(String dealId) {
        //@todo
        return null;
    }

    @Override
    public BigDecimal applyRates(SymbolStream stream) {
        return Deal.calculateValue(deal,stream.getSymbol(deal.getSymbol()));
    }
}