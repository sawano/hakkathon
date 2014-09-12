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
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.Result;

import static java.util.Objects.requireNonNull;

@Immutable
public final class DriverStats {

    private final DriverId driverId;
    private final long score;

    DriverStats(final DriverId driverId) {
        this(driverId, 0);
    }

    private DriverStats(final DriverId driverId, final long score) {
        requireNonNull(driverId);

        this.driverId = driverId;
        this.score = score;
    }

    public DriverId driverId() {
        return driverId;
    }

    public long score() {
        return score;
    }

    public DriverStats add(final Result result) {
        requireNonNull(result);

        return new DriverStats(driverId, score + result.points());
    }

}
