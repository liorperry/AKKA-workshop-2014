package akka.workshop.example.java.utils.forex;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 11/02/14.
 */
public class RateStreamBuilder {

    static Random generator = new Random();

    public static SymbolStream initialStream() {
        SymbolStream openStream = new SymbolStream(System.currentTimeMillis());
        for (Symbol symbol : Symbol.values()) {
            openStream.addSymbol(new SymbolPosition(symbol, BigDecimal.valueOf(100).add(BigDecimal.valueOf(symbol.ordinal()))));
        }
        return openStream;
    }


    public static SymbolStream buildTimelyStream(SymbolStream formerStream) {
        SymbolStream stream = new SymbolStream(System.currentTimeMillis());
        final List<SymbolPosition> symbols = formerStream.getSymbols();
        for (SymbolPosition symbol : symbols) {
            int number = generator.nextInt(7);
            double direction = generator.nextBoolean() ? -1 : 1;
            double result = number * direction;
            final BigDecimal newValue = symbol.getValue().add(BigDecimal.valueOf(result));
            stream.addSymbol(new SymbolPosition(symbol.getSymbol(), newValue));
        }
        return stream;
    }
}
