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

import net.jcip.annotations.Immutable;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

@Immutable
public final class DriverId implements Serializable {

    private static final int MAX_ALIAS_LENGTH = 50;

    private final String alias;

    public DriverId(final String alias) {
        validateAlias(alias);

        this.alias = alias.trim();
    }

    private static void validateAlias(final String alias) {
        requireNonNull(alias);
        final String trimmedAlias = alias.trim();
        if (trimmedAlias.isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be empty");
        }
        if (trimmedAlias.length() > MAX_ALIAS_LENGTH) {
            throw new IllegalArgumentException("Alias cannot be longer than " + MAX_ALIAS_LENGTH);
        }
    }

    public String alias() {
        return alias;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DriverId driverId = (DriverId) o;

        if (!alias.equals(driverId.alias)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return alias.hashCode();
    }

    @Override
    public String toString() {
        return "DriverId{" +
                "alias='" + alias + '\'' +
                '}';
    }
}
