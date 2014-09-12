/*
 * Copyright 2014 Daniel Sawano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.sawano.akka.chicken.driver;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static java.util.concurrent.TimeUnit.SECONDS;
import static se.sawano.akka.chicken.messages.Discovery.*;

public class RacecourseService {

    private static final FiniteDuration LOOKUP_TIMEOUT = Duration.create(10, SECONDS);

    /**
     * Resolves the remote racecourse using the IP address in the application.conf and returns an {@code ActorRef} for the racecourse.
     *
     * @param context
     *         the current context
     *
     * @return an ActorRef to the racecourse
     *
     * @see #lookup(akka.actor.ActorContext)
     */
    public static ActorRef resolve(final ActorContext context) {
        return resolveRemoteActor(context, serverPathFromConfig());
    }

    /**
     * Resolves the remote racecourse using network discovery and returns an {@code ActorRef} for the racecourse. This
     * will only work if the racecourse is on the same subnet.
     *
     * @param context
     *         the current context
     *
     * @return an ActorRef to the racecourse
     *
     * @see #resolve(akka.actor.ActorContext)
     */
    public static ActorRef lookup(final ActorContext context) {
        return resolveRemoteActor(context, serverPathFromLookup());
    }

    private static String serverPathFromConfig() {
        final Config config = ConfigFactory.load().getConfig("akka.chicken");
        final String serverIp = config.getString("server-ip");
        final int serverPort = config.getInt("server-port");
        return "akka.tcp://chicken@" + serverIp + ":" + serverPort + "/user/racecourse";
    }

    private static ActorRef resolveRemoteActor(final ActorContext context, final String serverPath) {
        try {
            final ActorSelection actorSelection = context.actorSelection(serverPath);
            final Future<ActorRef> actorRefFuture = actorSelection.resolveOne(Duration.create(4, SECONDS));
            return Await.result(actorRefFuture, LOOKUP_TIMEOUT);
        } catch (Exception e) {
            throw new RuntimeException("Unable to look up racecourse", e);
        }
    }

    private static String serverPathFromLookup() {
        final String serverIp = discoverServer();
        final Config config = ConfigFactory.load().getConfig("akka.chicken");
        final int serverPort = config.getInt("server-port");
        return "akka.tcp://chicken@" + serverIp + ":" + serverPort + "/user/racecourse";
    }

    static String discoverServer() {
        try (final MulticastSocket socket = createSocket()) {
            broadcastDiscovery();
            return receiveServerInetAddress(socket).getHostAddress();
        }
    }

    private static MulticastSocket createSocket() {
        try {
            return new MulticastSocket(PORT.port());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void broadcastDiscovery() {
        try (final MulticastSocket sender = new MulticastSocket(PORT.port())) {
            final InetAddress group = InetAddress.getByName("255.255.255.255");
            sender.send(new DatagramPacket(THE_CHICKEN.says().getBytes("UTF-8"), THE_CHICKEN.says()
                    .length(), group, PORT.port()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InetAddress receiveServerInetAddress(final MulticastSocket receiver) {
        try {
            final int bufferSize = 500;
            final DatagramPacket receivedDP = new DatagramPacket(new byte[bufferSize], bufferSize);
            String message = null;
            while (!THE_ROOSTER.says().equals(message) && !Thread.currentThread().isInterrupted()) {
                receiver.receive(receivedDP);
                message = new String(receivedDP.getData(), 0, receivedDP.getLength(), "UTF-8");
            }
            return receivedDP.getAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
