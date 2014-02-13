package akka.workshop.example.java.messages;

/**
 * Created by user on 12/02/14.
 */
public class AccountCreation extends AccountCommand{
    int balance;

    public AccountCreation(String name, int balance) {
        super(name);
        this.balance = balance;
    }


    public int getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "AccountCreation{" +
                "name='" + getName() + '\'' +
                ", balance=" + balance +
                '}';
    }
}
