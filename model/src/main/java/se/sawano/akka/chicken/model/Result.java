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

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.summingLong;

public enum Result {

    WIN(1),

    LOSE(-1),

    TIE(0),

    CRASH(-10);

    private final int points;

    private Result(final int points) {
        this.points = points;
    }

    public int points() {
        return points;
    }

    public static long sum(final Result... results) {
        requireNonNull(results);

        return stream(results).collect(summingLong(Result::points));
    }
}
