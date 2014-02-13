package akka.workshop.example.java.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import akka.workshop.example.java.messages.*;
import akka.workshop.example.java.utils.forex.Account;
import akka.workshop.example.java.utils.forex.AccountServices;
import akka.workshop.example.java.utils.forex.Deal;
import akka.workshop.example.java.utils.forex.SymbolStream;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

//import static akka.actor.SupervisorStrategy.*;

public class AccountProcessor extends UntypedActor implements AccountServices {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private SymbolStream currentRates;
    private Account account;

    public void onReceive(Object message) throws Exception {
        if (message instanceof SymbolStream) {
            log.debug("Received Rate Stream message: ", ((SymbolStream) message).getTimestamp());
            currentRates = (SymbolStream) message;

            if (getContext().getChildren().iterator().hasNext()) {
                //send-reply message
                Timeout timeout = new Timeout(Duration.create(100, TimeUnit.MILLISECONDS));
                final Iterable<ActorRef> children = getContext().getChildren();
                BigDecimal result = BigDecimal.ZERO;
                for (ActorRef child : children) {
                    Future<Object> future = Patterns.ask(child, currentRates, timeout);
                    final BigDecimal value = (BigDecimal) Await.result(future, timeout.duration());
                    result = result.add(value);
                }

                account.updateProfitOrLoss(result);
                if(account.getBalance().signum()==-1) {
                    getContext().parent().tell(new AccountCalibration(account.getName()),getSelf());
                }
            }

            //message was delivered via EventBus -
        } else if (message instanceof AccountStatusQuery) {
            getSender().tell(account,getSelf());
        } else if (message instanceof AccountCreation) {
            log.debug(message.toString());
            final AccountCreation accountCreation = (AccountCreation) message;
            account = openAccount(accountCreation.getName(), accountCreation.getBalance());
            getSender().tell(account, getSelf());
        } else if (message instanceof AccountDeposit) {
            final AccountDeposit accountDeposit = (AccountDeposit) message;
            deposit(accountDeposit.getAmount());
            getSender().tell(account.getBalance(), getSelf());
        } else if (message instanceof DealOpening) {
            final DealOpening dealOpening = (DealOpening) message;
            //create deal processor actor
            final String name = DealOpening.getName(dealOpening.getSymbol(), dealOpening.getType(), dealOpening.getOpenDate());
            final ActorRef actorRef = getContext().actorOf(Props.create(DealProcessor.class), name);
            //send-reply message
            Timeout timeout = new Timeout(Duration.create(100, TimeUnit.MILLISECONDS));
            final DealOpeningWithPosition openingWithPosition = new DealOpeningWithPosition(currentRates.getSymbol(dealOpening.getSymbol()), dealOpening.getType(), dealOpening.getAmount(), dealOpening.getOpenDate());
            Future<Object> future = ask(actorRef, openingWithPosition, timeout);
            final Deal deal = (Deal) Await.result(future, timeout.duration());
            addDeal(deal);
            getSender().tell(deal, getSelf());
        } else {
            unhandled(message);
        }
    }

    public Account openAccount(String name, int deposit) {
        return new Account(name, BigDecimal.valueOf(deposit));
    }

    @Override
    public void deposit(int amount) {
        account.addBalance(amount);
    }

    @Override
    public void addDeal(Deal deal) {
        account.addDeal(deal);
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