package akka.workshop.example.java.utils.forex;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by user on 10/02/14.
 */
public class SymbolPosition implements Serializable {
    private Symbol symbol;
    private BigDecimal value;

    public SymbolPosition(Symbol symbol, BigDecimal value) {
        this.symbol = symbol;
        this.value = value;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SymbolPosition that = (SymbolPosition) o;

        if (symbol != that.symbol) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = symbol.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SymbolPosition{" +
                "symbol=" + symbol +
                ", value=" + value +
                '}';
    }
}
