package akka.workshop.example.java.utils.forex;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 10/02/14.
 */
public class Account implements Serializable{
    String name;
    //state
    BigDecimal balance;
    //state
    List<Deal> deals;


    public Account(String name) {
        this.name=name;
    }
    public Account(String name, int deposit) {
        this(name, BigDecimal.valueOf(deposit));
    }

    public Account(String name, BigDecimal deposit) {
        this(name);
        this.balance = deposit;
        deals = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addBalance(int amount) {
        balance = balance.add(BigDecimal.valueOf(amount));
    }

    public void addDeal(Deal deal) {
        deals.add(deal);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Deal> getDeals() {
        return deals;
    }

    public void updateProfitOrLoss(BigDecimal currentValue) {
        this.balance = this.balance.add(currentValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (name != null ? !name.equals(account.name) : account.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
