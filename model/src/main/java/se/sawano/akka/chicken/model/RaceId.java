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

package se.sawano.akka.chicken.model;

import java.io.Serializable;
import java.util.UUID;

@Deprecated // Not needed? Map on DriverId instead and make this private to playground?
public class RaceId implements Serializable {
    public static RaceId next() {
        return new RaceId();
    }

    private final UUID id;

    private RaceId() {
        this.id = UUID.randomUUID();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final RaceId raceId = (RaceId) o;

        if (!id.equals(raceId.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "RaceId{" +
                "id=" + id +
                '}';
    }
}
