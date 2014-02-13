package akka.workshop.example.java.utils.cluster;

/**
 * Created by user on 10/02/14.
 */
public interface RuntimeService {
    void shutdown();

    void init() throws Throwable;
    void start() throws Throwable;
    void stop() throws Throwable;

    void pause();
    void resume();

    RuntimeStatus getStatus();

    public static enum RuntimeStatus {
        UNMOUNTED,MOUNTED,STARTED,STOPPED,PAUSED
    }
}
