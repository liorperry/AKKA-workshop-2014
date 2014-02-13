package akka.workshop.example.java.messages;

/**
 * Created by user on 12/02/14.
 */
public abstract class AccountCommand {
    private String name;

    public AccountCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AccountCommand{" +
                "name='" + name + '\'' +
                '}';
    }
}
