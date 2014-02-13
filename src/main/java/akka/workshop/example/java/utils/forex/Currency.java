package akka.workshop.example.java.utils.forex;

/**
 * Created by user on 10/02/14.
 */
public enum Currency {
	EUR("EUR"),
	USD("USD"),
	JPY("JPY"),
	GBP("GBP"),
	CHF("CHF"),
	AUD("AUD"),
	CAD("CAD"),
	NZD("NZD"),
	ILS("ILS");

    private final String name;

    private Currency(String name) {
   		this.name = name;
   	}

    public String getName() {
        return name;
    }
}
