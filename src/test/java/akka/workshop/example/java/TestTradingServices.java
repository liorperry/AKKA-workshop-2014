package akka.workshop.example.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ConsistentHashingRouter;
import akka.testkit.JavaTestKit;
import akka.workshop.example.java.actors.TradingProcessor;
import akka.workshop.example.java.messages.AccountCreation;
import akka.workshop.example.java.messages.AccountStatusQuery;
import akka.workshop.example.java.messages.DealOpening;
import akka.workshop.example.java.utils.forex.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static junit.framework.Assert.assertEquals;


/**
 * Created by user on 12/02/14.
 */
public class TestTradingServices {


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
    public void testCreateAccount() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
            final AccountCreation dummy = new AccountCreation("TestDummy", 100);
            final Props props = Props.create(TradingProcessor.class);
            final ActorRef subject = system.actorOf(props,dummy.getName());
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            subject.tell(dummy, getRef());
            // await the correct response
            expectMsgAllOf(duration("1 second"), new Account(dummy.getName(),dummy.getBalance()));

        }};
    }
    @Test
    public void testRouterCreateAccount() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
        //workshop actor router
            final ActorRef subject  = system.actorOf(Props.create(TradingProcessor.class).
                    withRouter(new ConsistentHashingRouter(10).withHashMapper(TradingProcessor.hashMapper)),"TradingRouter");
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            for (int i = 0; i < 10; i++) {
                final AccountCreation dummy = new AccountCreation("TestDummy:" + i, 100 + i);
                subject.tell(dummy, getRef());
                // await the correct response
                expectMsgAllOf(duration("1 second"), new Account(dummy.getName(), dummy.getBalance()));
            }

            for (int i = 0; i < 10; i++) {
                final AccountStatusQuery dummy = new AccountStatusQuery("TestDummy:" + i);
                subject.tell(dummy, getRef());
                // await the correct response
                expectMsgAllOf(duration("1 second"), new Account(dummy.getName()));
            }

        }};
    }

    @Test
    public void testAccountQuery() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
            final AccountCreation dummy = new AccountCreation("TestDummy", 100);

            final Props props = Props.create(TradingProcessor.class);
            final ActorRef subject = system.actorOf(props);
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            subject.tell(dummy, getRef());
            // await the correct response
            final Account expected = new Account(dummy.getName(), dummy.getBalance());
            expectMsgAllOf(duration("1 second"), expected);

            subject.tell(new AccountStatusQuery(dummy.getName()),getRef());
            // await the correct response
//            expectMsgAnyOf(duration("1 second"), new Deal(stream.getSymbol(Symbol.AUDCAD), 100, Deal.DealType.BUY));

            final Account out = new ExpectMsg<Account>("match hint") {
                  protected Account match(Object in) {
                    if (in instanceof Account)  {
                      return (Account) in;
                    } else {
                      throw noMatch();
                    }
                  }
                }.get(); // this extracts the received message

            assertEquals(expected, out);

        }};
    }

    @Test
    public void testAccountDealOpening() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
            final AccountCreation dummy = new AccountCreation("TestDummy", 1000);
            final Props props = Props.create(TradingProcessor.class);
            final ActorRef subject = system.actorOf(props,"TradingProcessor");
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            subject.tell(dummy, getRef());
            // await the correct response
            expectMsgAllOf(duration("1 second"), new Account(dummy.getName(),dummy.getBalance()));

            //publish rate stream
            SymbolStream stream = RateStreamBuilder.initialStream();
            system.eventStream().publish(stream);
            expectNoMsg(duration("1 second"));

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
            system.eventStream().publish(stream);
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
    public void testAccountCalibration() {
        /*
         * Wrap the whole test procedure within a testkit constructor
         * if you want to receive actor replies or use Within(), etc.
         */
        new JavaTestKit(system) {{
            final AccountCreation dummy = new AccountCreation("TestDummy", -1000);
            final Props props = Props.create(TradingProcessor.class);
            final ActorRef subject = system.actorOf(props);
            // “inject” the probe by passing it to the test subject
            // like a real resource would be passed in production
            subject.tell(dummy, getRef());
            // await the correct response
            expectMsgAllOf(duration("1 second"), new Account(dummy.getName(),dummy.getBalance()));

            //publish rate stream
            SymbolStream stream = RateStreamBuilder.initialStream();
            system.eventStream().publish(stream);
            expectNoMsg(duration("1 second"));

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
            system.eventStream().publish(stream);
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
}
