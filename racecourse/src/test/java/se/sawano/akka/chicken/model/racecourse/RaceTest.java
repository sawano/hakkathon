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

import org.junit.Before;
import org.junit.Test;
import se.sawano.akka.chicken.model.DriverId;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static se.sawano.akka.chicken.model.Choice.STRAIGHT;
import static se.sawano.akka.chicken.model.racecourse.Race.ResultReporter;

public class RaceTest {

    ResultReporter resultReporter;
    final RaceResult[] results = new RaceResult[1];
    private Race race;

    @Before
    public void setUp() throws Exception {
        results[0] = null;
    }

    @Test
    public void should_reportFinished_race() throws Exception {
        givenResultReporter();
        givenRace();

        whenFinishingRace();

        thenResultShouldBeReported();
    }

    @Test
    public void finished_race_should_not_allow_more_interaction() throws Exception {
        givenResultReporter();
        givenRace();

        whenFinishingRace();

        thenNoMoreChoicesCanBeMade();
    }

    private void thenNoMoreChoicesCanBeMade() {
        try {
            race.driverOneSays(STRAIGHT);
            fail();
        } catch (IllegalStateException e) {
            // NoOp
        }
        try {
            race.driverTwoSays(STRAIGHT);
            fail();
        } catch (IllegalStateException e) {
            // NoOp
        }
    }

    private void thenResultShouldBeReported() {
        assertNotNull(results[0]);
    }

    private void whenFinishingRace() {
        race.driverOneSays(STRAIGHT);
        race.driverTwoSays(STRAIGHT);
    }

    private void givenResultReporter() {
        resultReporter = new ResultReporter() {
            @Override
            public void report(final RaceResult result) {
                results[0] = result;
            }
        };
    }

    private void givenRace() {
        race = new Race(new DriverId("John"), new DriverId("Jane"), resultReporter);
    }
}
