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

import se.sawano.akka.chicken.model.DriverId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.sort;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public final class ScoreBoard {
    private final HashMap<DriverId, DriverStats> scores = new HashMap<>();
    private final ArrayList<RaceResult> resultHistory = new ArrayList<>();

    public void registerResult(final RaceResult result) {
        requireNonNull(result);

        resultHistory.add(result);

        final DriverStats newStatsForDriverOne = getOrCreateStatsFor(result.driverOne()).add(result.resultForDriverOne());
        store(newStatsForDriverOne);

        final DriverStats newStatsForDriverTwo = getOrCreateStatsFor(result.driverTwo()).add(result.resultForDriverTwo());
        store(newStatsForDriverTwo);
    }

    private void store(final DriverStats newStats) {
        scores.put(newStats.driverId(), newStats);
    }

    private DriverStats getOrCreateStatsFor(final DriverId driverId) {
        return ofNullable(scores.get(driverId)).orElseGet(() -> new DriverStats(driverId));
    }

    public List<DriverStats> scores() {
        final ArrayList<DriverStats> stats = new ArrayList<>(scores.values());
        sort(stats, (x, y) -> Long.compare(y.score(), x.score()));
        return stats;
    }

    public DriverStats statsFor(final DriverId driverId) {
        return scores.get(driverId);
    }

    public void reset() {
        scores.clear();
    }
}
