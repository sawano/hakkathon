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

import se.sawano.akka.chicken.model.Choice;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.RaceId;

import java.time.Instant;

import static java.time.Instant.now;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static se.sawano.akka.chicken.model.Choice.SWERVE;

/**
 * Represents a race. When the race is finished it will send a {@code RaceResult} back to the registered {@code ResultReporter}.
 */
public final class Race {

    @FunctionalInterface
    public interface ResultReporter {

        void report(RaceResult result);
    }

    private final RaceId raceId = RaceId.next();

    private final Instant started = now();
    private final DriverId[] drivers = new DriverId[2];
    private final Choice[] choices = new Choice[2];
    private final ResultReporter resultReporter;
    private boolean finished = false;

    /**
     * Creates a new race between two drivers.
     *
     * @param driver1
     *         driver one
     * @param driver2
     *         driver two
     * @param resultReporter
     *         the result reporter that will take care of the race result
     */
    public Race(final DriverId driver1, final DriverId driver2, final ResultReporter resultReporter) {
        requireNonNull(resultReporter);
        requireNonNull(driver1);
        requireNonNull(driver2);

        this.resultReporter = resultReporter;
        drivers[0] = driver1;
        drivers[1] = driver2;
    }

    public Instant startedTime() {
        return started;
    }

    public RaceId id() {
        return raceId;
    }

    public DriverId driverOne() {
        return drivers[0];
    }

    public DriverId driverTwo() {
        return drivers[1];
    }

    public void driverSays(final DriverId driver, final Choice choice) {
        if (driverOne().equals(driver)) {
            driverOneSays(choice);
        }
        else if (driverTwo().equals(driver)) {
            driverTwoSays(choice);
        }
        else {
            throw new IllegalArgumentException("Driver " + driver + " is not part of this race");
        }
    }

    /**
     * Registers a choice for driver 1. A choice can only be registered once and to an unfinished race.
     *
     * @param choice
     *         the choice for driver 1
     */
    public void driverOneSays(final Choice choice) {
        requireNonNull(choice);
        assertNotFinished();

        registerChoice(0, choice);
        reportIfFinished();
    }

    /**
     * Registers a choice for driver 2. A choice can only be registered once and to an unfinished race.
     *
     * @param choice
     *         the choice for driver 2
     */
    public void driverTwoSays(final Choice choice) {
        requireNonNull(choice);
        assertNotFinished();

        registerChoice(1, choice);
        reportIfFinished();
    }

    private void assertNotFinished() {
        if (finished) {
            throw new IllegalStateException("Race is already finished");
        }
    }

    private void registerChoice(final int driver, final Choice choice) {
        if (choices[driver] != null) {
            throw new IllegalStateException("Driver " + drivers[driver] + " has already made its choice");
        }
        choices[driver] = choice;
    }

    private void reportIfFinished() {
        finished = stream(choices).allMatch(choice -> choice != null);
        if (finished) {
            resultReporter.report(new RaceResult(raceId, drivers[0], choices[0], drivers[1], choices[1]));
        }
    }

    public void cancel() {
        finished = true;
        resultReporter.report(tie());
    }

    private RaceResult tie() {
        return new RaceResult(raceId, drivers[0], SWERVE, drivers[1], SWERVE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Race race = (Race) o;

        if (!raceId.equals(race.raceId)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return raceId.hashCode();
    }
}
