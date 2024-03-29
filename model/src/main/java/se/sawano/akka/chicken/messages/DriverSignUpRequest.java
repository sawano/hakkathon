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

package se.sawano.akka.chicken.messages;

import net.jcip.annotations.Immutable;
import se.sawano.akka.chicken.model.DriverId;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

@Immutable
public final class DriverSignUpRequest implements Serializable {

    public final DriverId driverId;

    public DriverSignUpRequest(final DriverId driverId) {
        requireNonNull(driverId);

        this.driverId = driverId;
    }
}
