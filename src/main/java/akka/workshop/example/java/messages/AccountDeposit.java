package akka.workshop.example.java.messages;

/**
 * Created by user on 12/02/14.
 */
public class AccountDeposit extends AccountCommand {
    int amount;

    public AccountDeposit(String account, int amount) {
        super(account);
        this.amount = amount;
    }

    public String getAccount() {
        return getName();
    }

    public int getAmount() {
        return amount;
    }
}
