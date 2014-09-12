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

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static se.sawano.akka.chicken.messages.Discovery.THE_ROOSTER;

public class RacecourseServiceTest {

    @Test
    public void should_discover_via_multicast() throws Exception {
        final Future<String> future = newSingleThreadExecutor().submit(() -> new RacecourseService().discoverServer());

        while (!future.isDone()) {
            broadcastResponse();
            Thread.sleep(100);
        }

        final String ip = future.get();

        final String hostAddress = InetAddress.getLocalHost().getHostAddress();
        assertEquals(hostAddress, ip);
    }

    @Test(timeout = 2000)
    @Ignore("Run with remote server started")
    public void should_discover_remote_server() throws Exception {
        final Future<String> future = newSingleThreadExecutor().submit(() -> new RacecourseService().discoverServer());

        while (!future.isDone()) {
            Thread.sleep(100);
        }

        final String ip = future.get();

        final String hostAddress = InetAddress.getLocalHost().getHostAddress();
        assertNotEquals(hostAddress, ip);
    }

    private static void broadcastResponse() {
        try (final MulticastSocket sender = new MulticastSocket(2002)) {
            final InetAddress group = InetAddress.getByName("255.255.255.255");
            final DatagramPacket dp = new DatagramPacket(THE_ROOSTER.says().getBytes("UTF-8"), THE_ROOSTER.says().length(), group, 2002);
            sender.send(dp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
