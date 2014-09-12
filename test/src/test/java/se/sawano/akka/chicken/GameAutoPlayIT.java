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
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import scala.concurrent.duration.Duration;
import se.sawano.akka.chicken.driver.exercise.spoiler.DriverActor;

import static java.util.concurrent.TimeUnit.MINUTES;

public class GameAutoPlayIT {

    private ActorSystem system;

    @Before
    public void setUp() throws Exception {
        system = ActorSystem.create();
    }

    @After
    public void tearDown() throws Exception {
        JavaTestKit.shutdownActorSystem(system);
    }

    @Test
    @Ignore("Used together with a running server")
    public void should_play_game() throws Exception {
        new JavaTestKit(system) {{

            final ActorRef john = getSystem().actorOf(DriverActor.props("John"), "John");
            final ActorRef jane = getSystem().actorOf(DriverActor.props("Jane"), "Jane");

            expectNoMsg(Duration.create(4, MINUTES));
        }};
    }
}
