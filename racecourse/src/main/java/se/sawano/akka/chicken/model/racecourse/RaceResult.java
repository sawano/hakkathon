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

import net.jcip.annotations.Immutable;
import se.sawano.akka.chicken.model.Choice;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.RaceId;
import se.sawano.akka.chicken.model.Result;

import static java.util.Objects.requireNonNull;
import static se.sawano.akka.chicken.model.Choice.SWERVE;

/**
 * The result of a finished {@link Race}.
 */
@Immutable
public class RaceResult {

    private final RaceId raceId;
    private final DriverId driver1;
    private final Choice choice1;
    private final DriverId driver2;
    private final Choice choice2;

    public RaceResult(final RaceId raceId, final DriverId driver1, final Choice choice1, final DriverId driver2, final Choice choice2) {
        requireNonNull(raceId);
        requireNonNull(driver1);
        requireNonNull(choice1);
        requireNonNull(driver2);
        requireNonNull(choice2);

        this.raceId = raceId;
        this.driver1 = driver1;
        this.choice1 = choice1;
        this.driver2 = driver2;
        this.choice2 = choice2;
    }

    public RaceId raceId() {
        return raceId;
    }

    public DriverId driverOne() {
        return driver1;
    }

    public DriverId driverTwo() {
        return driver2;
    }

    public Result resultForDriverOne() {
        return evaluate(choice1, choice2);
    }

    public Result resultForDriverTwo() {
        return evaluate(choice2, choice1);
    }

    private Result evaluate(final Choice ownChoice, final Choice opponentsChoice) {
        if (Choice.SWERVE.equals(ownChoice)) {
            if (Choice.SWERVE.equals(opponentsChoice)) {
                return Result.TIE;
            }
            return Result.LOSE;
        }
        if (Choice.SWERVE.equals(opponentsChoice)) {
            return Result.WIN;
        }
        return Result.CRASH;
    }

}
