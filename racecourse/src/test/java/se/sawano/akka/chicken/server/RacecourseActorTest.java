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

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static se.sawano.akka.chicken.messages.AdminMessages.PING;
import static se.sawano.akka.chicken.messages.AdminMessages.PONG;

public class RacecourseActorTest {

    private ActorSystem system;
    private ActorRef playground;

    @Before
    public void setUp() throws Exception {
        system = ActorSystem.create();
    }

    @After
    public void tearDown() throws Exception {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void should_respond_to_ping_request() throws Exception {
        new JavaTestKit(system) {
            {
                givenRaceCourse();

                playground.tell(PING, getRef());
                expectMsgAllOf(PONG);
            }
        };
    }

    private void givenRaceCourse() {
        playground = system.actorOf(RacecourseActor.props());
    }

}
