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

package se.sawano.akka.chicken.events;

import net.jcip.annotations.Immutable;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.RaceId;
import se.sawano.akka.chicken.model.Result;

import static java.util.Objects.requireNonNull;

@Immutable
public final class RaceFinished implements Event {
    public final RaceId raceId;
    public final DriverId driver1;
    public final Result result1;
    public final DriverId driver2;
    public final Result result2;

    public RaceFinished(final RaceId raceId, final DriverId driver1, final Result result1, final DriverId driver2, final Result result2) {
        requireNonNull(raceId);
        requireNonNull(driver1);
        requireNonNull(result1);
        requireNonNull(driver2);
        requireNonNull(result2);

        this.raceId = raceId;
        this.driver1 = driver1;
        this.result1 = result1;
        this.driver2 = driver2;
        this.result2 = result2;
    }

    public boolean isCompetitor(final DriverId driverId) {
        requireNonNull(driverId);

        return driver1.equals(driverId) || driver2.equals(driverId);
    }

    public DriverId opponentFor(final DriverId driverId) {
        assertCompetitor(driverId);

        if (driver1.equals(driverId)) {
            return driver2;
        }
        return driver1;
    }

    public Result resultFor(final DriverId driverId) {
        assertCompetitor(driverId);

        if (driver1.equals(driverId)) {
            return result1;
        }
        return result2;
    }

    private void assertCompetitor(final DriverId driverId) {
        if (!isCompetitor(driverId)) {
            throw new IllegalArgumentException("Driver " + driverId + " not part of this result");
        }
    }

}
