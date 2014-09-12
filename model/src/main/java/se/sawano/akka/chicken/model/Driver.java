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

import se.sawano.akka.chicken.events.RaceCommand;
import se.sawano.akka.chicken.events.RaceDecision;
import se.sawano.akka.chicken.events.RaceFinished;
import se.sawano.akka.japi.messagehandling.Messages;

/**
 * @see Racecourse
 */
public interface Driver extends Messages {

    /**
     * Called when a driver races an opponent and need to make a choice. The decision is made by replying with a {@link RaceDecision}.
     *
     * @param command
     *         the command that instructs the driver to race
     *
     * @see Choice
     * @see RaceDecision
     */
    void race(RaceCommand command);

    /**
     * Called when a race that this driver has participated in has been finished.
     *
     * @param raceFinished
     *         the information about the finished race
     */
    void acceptRaceResult(RaceFinished raceFinished);

}
