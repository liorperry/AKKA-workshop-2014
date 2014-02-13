/* Oct 6, 2009 */

package akka.workshop.example.java.utils.cluster;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.workshop.example.java.messages.ClusterCommand;
import org.apache.log4j.Logger;

public class ClusterRuntimeUtils {
    private final static Logger logger = Logger.getLogger(ClusterRuntimeUtils.class);

    private static ActorSystem system;
    private static RuntimeService.RuntimeStatus status = RuntimeService.RuntimeStatus.UNMOUNTED;


    public static ActorSystem registerCluster() {
        if(system!=null && !system.isTerminated())
            return system;

        // Create an Akka system
        system = ActorSystem.create("ClusterSystem");

        ActorRef clusterListener = system.actorOf(new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                return new UntypedActor() {

                    @Override
                    public void onReceive(Object message) throws Exception {
                        System.out.println(message);
                        if(message instanceof ClusterCommand){
                            processCommand((ClusterCommand) message);
                        } else {
                            unhandled(message);
                        }
                    }

                    private void processCommand(ClusterCommand command) throws Exception {
                        try {
                            logger.warn(command.getStatus());
                            switch (command.getStatus()) {
                                case PAUSE:
                                    pause();
                                    break;
                                case START:
                                    start();
                                    break;
                                case RESUME:
                                    resume();
                                    break;
                                case STOP:
                                    stop();
                                    break;
                                case SHUTDOWN:
                                    stop();
                                    //todo - shutdown gracefully
                                    break;
                            }
                            logger.info("************************************************************************************************************ ");
                            logger.info("********************************** ---- "+command.getStatus()+" --- ********************************** ");
                            logger.info("************************************************************************************************************ ");
                        } catch (Throwable throwable) {
                            throw new Exception(throwable);
                        }
                    }
                };
            }
        }), "clusterListener");

        Cluster.get(system).subscribe(clusterListener, ClusterEvent.UnreachableMember.class);
        Cluster.get(system).subscribe(clusterListener, ClusterEvent.MemberUp.class);
        Cluster.get(system).subscribe(clusterListener, ClusterEvent.MemberExited.class);
        Cluster.get(system).subscribe(clusterListener, ClusterEvent.MemberRemoved.class);

        return system;
    }

    static void pause() {
        //todo pause
        status = RuntimeService.RuntimeStatus.PAUSED;
    }

    static void resume() {
        //todo resume
        status = RuntimeService.RuntimeStatus.STARTED;
    }

    static void start() {
    }

    static void stop() {
        //remove context
        status = RuntimeService.RuntimeStatus.STOPPED;
    }

    public static RuntimeService.RuntimeStatus getStatus() {
        return status;
    }
}
