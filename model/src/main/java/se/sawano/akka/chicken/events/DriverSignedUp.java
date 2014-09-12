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

import se.sawano.akka.chicken.messages.DriverSignUpRequest;
import se.sawano.akka.chicken.model.DriverId;

import static java.util.Objects.requireNonNull;

/**
 * Published when a driver has been signed up to race. This happens as a result of a {@link DriverSignUpRequest}.
 */
public class DriverSignedUp implements Event {

    public final DriverId driverId;

    public DriverSignedUp(final DriverId driverId) {
        requireNonNull(driverId);

        this.driverId = driverId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DriverSignedUp that = (DriverSignedUp) o;

        if (!driverId.equals(that.driverId)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return driverId.hashCode();
    }
}
