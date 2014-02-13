package akka.workshop.example.java;

import akka.workshop.example.java.actors.AccountProcessor;
import akka.workshop.example.java.messages.AccountCreation;
import akka.workshop.example.java.messages.AccountStatusQuery;
import akka.workshop.example.java.messages.DealOpening;
import akka.workshop.example.java.utils.forex.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

import java.math.BigDecimal;
import java.util.Date;

import static junit.framework.Assert.assertEquals;


/**
 * Created by user on 12/02/14.
 */
public class TestAccountServices {


    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testRegistration() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
            final Props props = Props.create(AccountProcessor.class);
            final ActorRef subject = system.actorOf(props);
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            subject.tell(new AccountCreation("TestDummy",100), getRef());
            // await the correct response
            expectMsgAllOf(duration("1 second"), new Account("TestDummy",100));

        }};
    }

    @Test
    public void testDealOpening() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
            final AccountCreation dummy = new AccountCreation("TestDummy", 100);

            final Props props = Props.create(AccountProcessor.class);
            final ActorRef subject = system.actorOf(props,dummy.getName());
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            subject.tell(dummy, getRef());
            // await the correct response
            expectMsgAllOf(duration("1 second"), new Account("TestDummy",100));

            //publish rate stream
            final SymbolStream stream = RateStreamBuilder.initialStream();
            subject.tell(stream,getRef());
            //not expecting answer (via event stream)
            expectNoMsg();

            //request deal opening
            final DealOpening dealOpening = new DealOpening(dummy.getName(), Symbol.AUDCAD, Deal.DealType.BUY, 100, new Date());
            subject.tell(dealOpening,getRef());
            // await the correct response
//            expectMsgAnyOf(duration("1 second"), new Deal(stream.getSymbol(Symbol.AUDCAD), 100, Deal.DealType.BUY));

            final Deal out = new ExpectMsg<Deal>("match hint") {
                  protected Deal match(Object in) {
                    if (in instanceof Deal)  {
                      return (Deal) in;
                    } else {
                      throw noMatch();
                    }
                  }
                }.get(); // this extracts the received message

            assertEquals(new Deal(stream.getSymbol(dealOpening.getSymbol()), dealOpening.getAmount(), dealOpening.getType(), dealOpening.getOpenDate()), out);

        }};
    }
    @Test
    public void testDealRateUpdates() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
            final Props props = Props.create(AccountProcessor.class);
            final ActorRef subject = system.actorOf(props);
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            final AccountCreation dummy = new AccountCreation("TestDummy", 1000);
            subject.tell(dummy, getRef());
            // await the correct response
            expectMsgAllOf(duration("1 second"), new Account(dummy.getName(),dummy.getBalance()));

            //publish rate stream
            SymbolStream stream = RateStreamBuilder.initialStream();
            subject.tell(stream,getRef());
            //not expecting answer (via event stream)
            expectNoMsg();

            //request deal opening
            final DealOpening dealOpening = new DealOpening(dummy.getName(), Symbol.AUDCAD, Deal.DealType.BUY, 10, new Date());
            subject.tell(dealOpening,getRef());

            final Deal deal = new ExpectMsg<Deal>("match hint") {
                  protected Deal match(Object in) {
                    if (in instanceof Deal)  {
                      return (Deal) in;
                    } else {
                      throw noMatch();
                    }
                  }
                }.get(); // this extracts the received message

            // update rate stream
            stream = RateStreamBuilder.buildTimelyStream(stream);
            subject.tell(stream,getRef());
            //not expecting answer (via event stream)
            expectNoMsg(duration("1 second"));

            subject.tell(new AccountStatusQuery("TestDummy"), getRef());
            final Account out = new ExpectMsg<Account>("match hint") {
                  protected Account match(Object in) {
                    if (in instanceof Account)  {
                      return (Account) in;
                    } else {
                      throw noMatch();
                    }
                  }
                }.get();

            final SymbolPosition symbol = stream.getSymbol(Symbol.AUDCAD);
            final BigDecimal calculateValue = Deal.calculateValue(deal, symbol);
            assertEquals(out.getBalance(), BigDecimal.valueOf(dummy.getBalance()).add(calculateValue));
        }};
    }
    @Test
    public void testDoubleDealRateUpdates() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
            final Props props = Props.create(AccountProcessor.class);
            final ActorRef subject = system.actorOf(props);
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            final AccountCreation dummy = new AccountCreation("TestDummy", 1000);
            subject.tell(dummy, getRef());
            // await the correct response
            expectMsgAllOf(duration("1 second"), new Account(dummy.getName(),dummy.getBalance()));

            //publish rate stream
            SymbolStream stream = RateStreamBuilder.initialStream();
            subject.tell(stream,getRef());
            //not expecting answer (via event stream)
            expectNoMsg();

            //request deal opening
            DealOpening dealOpening = new DealOpening(dummy.getName(), Symbol.AUDCAD, Deal.DealType.BUY, 10, new Date());
            subject.tell(dealOpening,getRef());

            Deal deal1 = new ExpectMsg<Deal>("match hint") {
                  protected Deal match(Object in) {
                    if (in instanceof Deal)  {
                      return (Deal) in;
                    } else {
                      throw noMatch();
                    }
                  }
                }.get(); // this extracts the received message


            //request deal opening
            dealOpening = new DealOpening(dummy.getName(), Symbol.EURAUD, Deal.DealType.SELL, 5, new Date());
            subject.tell(dealOpening,getRef());

            Deal deal2 = new ExpectMsg<Deal>("match hint") {
                  protected Deal match(Object in) {
                    if (in instanceof Deal)  {
                      return (Deal) in;
                    } else {
                      throw noMatch();
                    }
                  }
                }.get(); // this extracts the received message

            // update rate stream
            stream = RateStreamBuilder.buildTimelyStream(stream);
            subject.tell(stream,getRef());
            //not expecting answer (via event stream)
            expectNoMsg(duration("1 second"));

            subject.tell(new AccountStatusQuery("TestDummy"), getRef());
            final Account out = new ExpectMsg<Account>("match hint") {
                  protected Account match(Object in) {
                    if (in instanceof Account)  {
                      return (Account) in;
                    } else {
                      throw noMatch();
                    }
                  }
                }.get();

            SymbolPosition symbol = stream.getSymbol(Symbol.AUDCAD);
            final BigDecimal calculateValue1 = Deal.calculateValue(deal1, symbol);
            symbol = stream.getSymbol(Symbol.EURAUD);
            final BigDecimal calculateValue2 = Deal.calculateValue(deal2, symbol);
            assertEquals(out.getBalance(), BigDecimal.valueOf(dummy.getBalance()).add(calculateValue1).add(calculateValue2));
        }};
    }

}
