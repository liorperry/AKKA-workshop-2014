/**
 * Copyright (C) 2009-2012 Typesafe Inc. <http://www.typesafe.com>
 */

package akka.workshop.example.java.utils.cluster;

//#imports

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.workshop.example.java.actors.cluster.Subscriber;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

//#app
public class ClusterNode {

    public static void main(String[] args) {
        if (args.length > 0)
            System.setProperty("akka.remote.netty.port", args[0]);

        //load configuration manually
        Config config = ConfigFactory.load();
        System.out.println(config.getObject("akka.remote.netty").unwrapped());

        final ActorSystem system = ClusterRuntimeUtils.registerCluster();
        system.actorOf(Props.create(Subscriber.class), "subscriber1");
    }
}