package akka.workshop.example.java.utils.forex;

/**
 * Created by user on 10/02/14.
 */
public enum Symbol {
	EURUSD(Currency.EUR, Currency.USD),
	USDJPY(Currency.USD, Currency.JPY),
	GBPUSD(Currency.GBP, Currency.USD),
	USDCHF(Currency.USD, Currency.CHF),
	AUDUSD(Currency.AUD, Currency.USD),
	USDCAD(Currency.USD, Currency.CAD),
	NZDUSD(Currency.NZD, Currency.USD),
	EURJPY(Currency.EUR, Currency.JPY),
	EURGBP(Currency.EUR, Currency.GBP),
	EURCHF(Currency.EUR, Currency.CHF),
	GBPJPY(Currency.GBP, Currency.JPY),
	GBPCHF(Currency.GBP, Currency.CHF),
	EURAUD(Currency.EUR, Currency.AUD),
	EURCAD(Currency.EUR, Currency.CAD),
	NZDJPY(Currency.NZD, Currency.JPY),
	CADJPY(Currency.CAD, Currency.JPY),
	AUDJPY(Currency.AUD, Currency.JPY),
	CHFJPY(Currency.CHF, Currency.JPY),
	GBPCAD(Currency.GBP, Currency.CAD),
	AUDNZD(Currency.AUD, Currency.NZD),
	AUDCAD(Currency.AUD, Currency.CAD),
	EURNZD(Currency.EUR, Currency.NZD),
	GBPAUD(Currency.GBP, Currency.AUD),
	GBPNZD(Currency.GBP, Currency.NZD),
	NZDCHF(Currency.NZD, Currency.CHF);

    private final Currency left;
    private final Currency right;

    private Symbol(Currency left, Currency right) {
   		this.left = left;
   		this.right = right;
   	}

    public Currency getLeft() {
        return left;
    }

    public Currency getRight() {
        return right;
    }
}
