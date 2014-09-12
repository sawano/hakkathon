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
import se.sawano.akka.chicken.messages.DriverSignUpRequest;
import se.sawano.akka.japi.messagehandling.Messages;

/**
 * Welcome to the racecourse where we play Chicken!
 * <p>
 * The game chicken is played by having two {@code drivers} to {@code race} each other. The {@code drivers} drive towards each other on a
 * collision course and has to make a {@code choice} to either {@code swerve} or go {@code straight}. If both {@code drivers} go {@code
 * straight} then they will {@code crash}. If one {@code driver} goes {@code straight} and the other {@code swerves} then the one who {@code
 * swerved} will {@code lose} (i.e. be a chicken) and the other one will {@code win}. If both {@code drivers} {@code swerves} then there
 * will be a {@code tie}.
 * <p>
 * Points are awarded as:
 * <pre>
 * <table style='border: 1px solid black;'>
 *     <tr>
 *         <td></td>
 *         <td><b>Swerve</b></td>
 *         <td><b>Straight</b></td>
 *     </tr>
 *     <tr>
 *         <td><b>Swerve</b></td>
 *         <td>Tie, Tie</td>
 *         <td>Lose, Win</td>
 *     </tr>
 *     <tr>
 *         <td><b>Straight</b></td>
 *         <td>Win, Lose</td>
 *         <td>Crash, Crash</td>
 *     </tr>
 * </table>
 * </pre>
 * <p>
 * (http://en.wikipedia.org/wiki/Chicken_game)
 * <p>
 * Typical flow for drivers:
 * <pre>
 *     <ul>
 *         <li>Driver goes to the racecourse and signs up for a race ({@link DriverSignUpRequest})</li>
 *         <li>Driver is told to race ({@link RaceCommand})</li>
 *         <li>Driver races, i.e. makes a choice to swerve or go straight ({@link RaceDecision})</li>
 *         <li>Driver is informed of the result of the race ({@link RaceFinished})</li>
 *     </ul>
 * </pre>
 * <p>
 * The flow of a game can be illustrated as:
 * <pre>
 * Driver      (DriverSignUpRequest)  ->  Racecourse
 * Driver  <-  (RaceCommand)              Racecourse
 * Driver      (RaceDecision)           ->  Racecourse
 * Driver  <-  (RaceFinished)             Racecourse
 * </pre>
 * Note that since most communication is event based, each event will be received by all actors. I.e. an actor should always
 * check the intended recipient before acting upon an event.
 */
public interface Racecourse extends Messages {

    /**
     * Called when a driver enters the racecourse and signs up to race.
     *
     * @param request
     */
    void signUpForRace(DriverSignUpRequest request);

    /**
     * Registers a choice from a driver.
     *
     * @param decision
     *         the driver's decision
     */
    void registerDriverChoice(RaceDecision decision);

}
