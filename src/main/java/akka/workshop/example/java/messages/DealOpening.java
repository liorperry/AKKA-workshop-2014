package akka.workshop.example.java.messages;

import akka.workshop.example.java.utils.forex.Deal;
import akka.workshop.example.java.utils.forex.Symbol;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 12/02/14.
 */
public class DealOpening extends AccountCommand{
    private static SimpleDateFormat format = new SimpleDateFormat("YYYY-ww-u");
    private Symbol symbol;
    private Deal.DealType type;
    private int amount;
    private Date openDate;

    public DealOpening(String account, Symbol symbol, Deal.DealType type, int amount, Date openDate) {
        super(account);
        this.symbol = symbol;
        this.type = type;
        this.amount = amount;
        this.openDate = openDate;
    }

    public String getAccount() {
        return getName();
    }

    public Symbol getSymbol() {
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

    public static String getName(Symbol symbol, Deal.DealType type, Date openDate) {
        return symbol.name() + "_"+type.name() +"_"+ format.format(openDate);
    }
}
