package akka.workshop.example.java.utils.forex;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by user on 12/02/14.
 */
public interface DealServices {
    public Deal addDeal(SymbolPosition symbol,int amount,Deal.DealType type,Date date);
    public Deal closeDeal(String dealId);
    public BigDecimal applyRates(SymbolStream stream);

}
