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

package se.sawano.akka.chicken;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import se.sawano.akka.chicken.driver.exercise.spoiler.DriverActor;
import se.sawano.akka.chicken.events.DriverSignedUp;
import se.sawano.akka.chicken.events.RaceCommand;
import se.sawano.akka.chicken.messages.DriverSignUpRequest;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.server.ServerApp;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameIT {

    private ServerApp server;
    private ActorSystem system;

    @Before
    public void setUp() throws Exception {
        system = ActorSystem.create();
        server = new ServerApp().start();
    }

    @After
    public void tearDown() throws Exception {
        JavaTestKit.shutdownActorSystem(system);
        server.stop();
    }

    @Test
    public void should_return_race_command_when_two_players_has_signed_up() throws Exception {
        new JavaTestKit(system) {
            {
                final ActorRef racecourse = racecourse();
                final DriverId driver1 = new DriverId("John");
                final DriverId driver2 = new DriverId("Jane");

                racecourse.tell(new DriverSignUpRequest(driver1), getRef());
                racecourse.tell(new DriverSignUpRequest(driver2), getRef());

                expectMsgAllOf(new DriverSignedUp(driver2), new DriverSignedUp(driver1));
                final Object[] objects = receiveN(1);
                assertEquals(1, objects.length);
                RaceCommand command = (RaceCommand) objects[0];
                assertTrue(command.isCompetitor(driver1));
                assertTrue(command.isCompetitor(driver2));
            }

        };
    }

    @Test
    public void should_play_game() throws Exception {
        new JavaTestKit(system) {{

            final ActorRef john = getSystem().actorOf(DriverActor.props("John"), "John");
            final ActorRef jane = getSystem().actorOf(DriverActor.props("Jane"), "Jane");

            expectNoMsg(Duration.create(30, SECONDS));
        }};
    }

    private ActorRef racecourse() {
        try {
            final ActorSelection actorSelection = system.actorSelection("akka.tcp://chicken@127.0.0.1:2553/user/racecourse");
            final Future<ActorRef> actorRefFuture = actorSelection.resolveOne(Duration.create(2, TimeUnit.SECONDS));
            return Await.result(actorRefFuture, Duration.create(1, TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new RuntimeException("Unable to look up racecourse");
        }
    }

    public static void main(final String... args) throws Exception {
        try {
            final Result result = JUnitCore.runClasses(GameIT.class);
            for (final Failure failure : result.getFailures()) {
                failure.getException().printStackTrace();
            }
            assertTrue(result.wasSuccessful());
        } finally {
            System.exit(0);
        }
    }
}
