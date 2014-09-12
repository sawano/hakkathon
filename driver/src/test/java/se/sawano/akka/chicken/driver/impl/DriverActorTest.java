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

package se.sawano.akka.chicken.driver.impl;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import scala.concurrent.duration.Duration;
import se.sawano.akka.chicken.driver.exercise.spoiler.DriverActor;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.assertTrue;

public class DriverActorTest {

    ActorSystem system;

    @Before
    public void setUp() throws Exception {
        system = ActorSystem.create();
    }

    @After
    public void tearDown() throws Exception {
        JavaTestKit.shutdownActorSystem(system);
    }

    @Test
    @Ignore("Enable to test your implementation against server")
    public void should_play_game() throws Exception {
        new JavaTestKit(system) {{
            final Class<? extends Actor> actorClass = DriverActor.class; // TODO provide your own actor class here
            final String driverName = "John Doe " + UUID.randomUUID().hashCode();

            final ActorRef driver = getSystem().actorOf(DriverActor.props(driverName));

            expectNoMsg(Duration.create(1, MINUTES));  // Let driver play for 1 minute
        }};
    }

    /**
     * Makes it possible to run from command line.
     */
    public static void main(final String... args) throws Exception {
        try {
            final Result result = JUnitCore.runClasses(DriverActorTest.class);
            for (final Failure failure : result.getFailures()) {
                failure.getException().printStackTrace();
            }
            assertTrue(result.wasSuccessful());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
