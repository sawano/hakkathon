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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import se.sawano.akka.chicken.model.Choice;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.Result;
import se.sawano.akka.chicken.model.RaceId;
import se.sawano.akka.chicken.model.racecourse.RaceResult;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static se.sawano.akka.chicken.model.Choice.STRAIGHT;
import static se.sawano.akka.chicken.model.Choice.SWERVE;
import static se.sawano.akka.chicken.model.Result.*;

@RunWith(Parameterized.class)
public class RaceResultTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{{Choice.SWERVE, Choice.SWERVE, Result.TIE, Result.TIE},
                                     {Choice.SWERVE, Choice.STRAIGHT, Result.LOSE, Result.WIN},
                                     {Choice.STRAIGHT, Choice.SWERVE, Result.WIN, Result.LOSE},
                                     {Choice.STRAIGHT, Choice.STRAIGHT, Result.CRASH, Result.CRASH}});
    }

    private final Choice choice1;
    private final Choice choice2;
    private final Result result1;
    private final Result result2;

    public RaceResultTest(final Choice choice1, final Choice choice2, final Result result1, final Result result2) {
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.result1 = result1;
        this.result2 = result2;
    }

    @Test
    public void should_produce_correct_result() throws Exception {
        final RaceResult result = new RaceResult(RaceId.next(), new DriverId("John"), choice1, new DriverId("Jane"), choice2);

        assertEquals(result.resultForDriverOne(), result1);
        assertEquals(result.resultForDriverTwo(), result2);
    }
}
