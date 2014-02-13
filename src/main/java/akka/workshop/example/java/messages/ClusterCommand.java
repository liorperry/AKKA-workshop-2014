package akka.workshop.example.java.messages;

import java.io.Serializable;


public final class ClusterCommand implements Serializable {
    private Command status;

    public ClusterCommand(Command status) {
        this.status = status;
    }

    public Command getStatus() {
        return status;
    }

    public enum Command {
        START, STOP, PAUSE, RESUME, SHUTDOWN
    }

}
