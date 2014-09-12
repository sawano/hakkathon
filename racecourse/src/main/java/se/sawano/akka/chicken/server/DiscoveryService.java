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

package se.sawano.akka.chicken.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static se.sawano.akka.chicken.messages.Discovery.*;

public class DiscoveryService {

    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private boolean stopped = false;

    public void start() {
        stopped = false;
        service.submit(this::startLoop);
    }

    public void stop() {
        stopped = true;
    }

    private void startLoop() {
        listenForChicken();
        broadcastResponse();
        if (!stopped) {
            service.submit(this::startLoop);
        }
    }

    private void listenForChicken() {
        try (final MulticastSocket socket = new MulticastSocket(PORT.port())) {
            final int bufferSize = 500;
            final DatagramPacket receivedDP = new DatagramPacket(new byte[bufferSize], bufferSize);
            String message = null;
            while (!THE_CHICKEN.says().equals(message) && !Thread.currentThread().isInterrupted()) {
                socket.receive(receivedDP);
                message = new String(receivedDP.getData(), 0, receivedDP.getLength(), "UTF-8");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcastResponse() {
        try (final MulticastSocket sender = new MulticastSocket(PORT.port())) {
            final InetAddress group = InetAddress.getByName("255.255.255.255");
            final DatagramPacket dp = new DatagramPacket(THE_ROOSTER.says().getBytes("UTF-8"), THE_ROOSTER.says()
                    .length(), group, 2002);
            sender.send(dp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
