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

package se.sawano.akka.chicken.model.racecourse;

import org.junit.Test;
import se.sawano.akka.chicken.events.RaceDecision;
import se.sawano.akka.chicken.messages.DriverSignUpRequest;
import se.sawano.akka.chicken.model.Choice;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.RaceId;

import static org.junit.Assert.*;

public class DefaultRacecourseTest {

    @Test
    public void should_not_add_a_driver_more_than_once() {
        final DefaultRacecourse racecourse = new DefaultRacecourse();
        final DriverId john = new DriverId("John");
        assertTrue(racecourse.signUpForRace(new DriverSignUpRequest(john)));
        assertFalse(racecourse.signUpForRace(new DriverSignUpRequest(john)));
    }

    @Test
    public void should_throw_RaceNotFoundException_when_registering_choice_for_nonexisting_race() {
        final DefaultRacecourse racecourse = new DefaultRacecourse();
        final RaceId raceId = RaceId.next();
        try {
            racecourse.registerDriverChoice(new RaceDecision(raceId, new DriverId("John"), Choice.STRAIGHT));
            fail();
        } catch (RaceNotFoundException e) {
            assertEquals(raceId, e.raceId);
        }
    }
}
