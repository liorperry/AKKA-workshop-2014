package akka.workshop.example.java.messages;

import akka.workshop.example.java.utils.forex.Deal;
import akka.workshop.example.java.utils.forex.SymbolPosition;

import java.util.Date;

/**
 * Created by user on 12/02/14.
 */
public class DealOpeningWithPosition {
    private SymbolPosition symbol;
    private Deal.DealType type;
    private int amount;
    private Date openDate;

    public DealOpeningWithPosition(SymbolPosition symbol, Deal.DealType type, int amount,Date openDate) {
        this.symbol = symbol;
        this.type = type;
        this.amount = amount;
        this.openDate = openDate;
    }

    public SymbolPosition getSymbol() {
        return symbol;
    }

    public Deal.DealType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public Date getOpenDate() {
        return openDate;
    }
}
