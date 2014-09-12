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

import net.jcip.annotations.NotThreadSafe;
import se.sawano.akka.chicken.events.RaceDecision;
import se.sawano.akka.chicken.messages.DriverSignUpRequest;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.RaceId;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static se.sawano.akka.chicken.model.racecourse.Race.ResultReporter;

@NotThreadSafe
public class DefaultRacecourse {

    private final ScoreBoard scoreBoard = new ScoreBoard();
    private final HashMap<RaceId, Race> ongoingRaces = new HashMap<>();
    private final ArrayDeque<DriverId> driversWaitingToRace = new ArrayDeque<>();

    public boolean signUpForRace(final DriverSignUpRequest request) {
        requireNonNull(request);

        if (driversWaitingToRace.contains(request.driverId)) {
            return false;
        }
        driversWaitingToRace.add(request.driverId);
        return true;
    }

    /**
     * @throws RaceNotFoundException
     */
    public void registerDriverChoice(final RaceDecision decision) {
        requireNonNull(decision);

        getRaceWithId(decision.raceId).driverSays(decision.respondingDriver, decision.choice);
    }

    private Race getRaceWithId(final RaceId raceId) {
        return ofNullable(ongoingRaces.get(raceId)).orElseThrow(() -> new RaceNotFoundException(raceId));
    }

    /**
     * Starts races for all currently registered drivers.
     *
     * @param resultReporter
     *
     * @return the started races
     */
    public List<Race> startRaces(final ResultReporter resultReporter) {
        requireNonNull(resultReporter);

        final ResultReporter raceCourseResultReporter = result -> {
            registerFinishedRace(result);
            resultReporter.report(result);
        };

        final ArrayList<Race> startedRaces = new ArrayList<>();
        while (driversWaitingToRace.size() >= 2) {
            final Race race = addRaceBetween(nextDriver(), nextDriver(), raceCourseResultReporter);
            startedRaces.add(race);
        }

        return startedRaces;
    }

    private DriverId nextDriver() {
        return driversWaitingToRace.removeFirst();
    }

    private Race addRaceBetween(final DriverId driver1, final DriverId driver2, final ResultReporter resultReporter) {
        final Race race = new Race(driver1, driver2, resultReporter);

        ongoingRaces.put(race.id(), race);

        return race;
    }

    private void registerFinishedRace(final RaceResult result) {
        ongoingRaces.remove(result.raceId());
        scoreBoard.registerResult(result);
    }

    public List<DriverStats> scores() {
        return scoreBoard.scores();
    }

    public void resetScores() {
        scoreBoard.reset();
    }

    public List<Race> cleanUpDeadRaces() {
        final long maxIdleSeconds = 10;
        final Instant now = Instant.now();
        return new ArrayList<>(ongoingRaces.values()).stream()
                .filter(race -> SECONDS.between(race.startedTime(), now) >= maxIdleSeconds)
                .peek(Race::cancel).collect(toList());
    }
}
