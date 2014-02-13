package akka.workshop.example.java.utils.forex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 10/02/14.
 */
public class SymbolStream implements Serializable {
    long timestamp;
    List<SymbolPosition> symbols;

    public SymbolStream(long timestamp) {
        this.timestamp = timestamp;
        this.symbols = new ArrayList<>();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<SymbolPosition> getSymbols() {
        return symbols;
    }

    public void addSymbol(SymbolPosition position) {
        symbols.add(position);
    }

    @Override
    public String toString() {
        return "SymbolStream{" +
                "timestamp=" + timestamp +
                '}';
    }

    public SymbolPosition getSymbol(Symbol symbol) {
        for (SymbolPosition symbolPosition : symbols) {
            if(symbolPosition.getSymbol()==symbol) {
                return symbolPosition;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SymbolStream stream = (SymbolStream) o;

        if (timestamp != stream.timestamp) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }
}
