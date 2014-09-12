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

package se.sawano.akka.chicken.driver.exercise.level3;

/**
 * Level 3 base implementation. Please refer to the javadoc of {@link se.sawano.akka.chicken.model.Racecourse} on game details such as the flow of events etc.
 * <p>
 * For convenience and as a recommended practice it is good to provide a factory method for creating {@link akka.actor.Props} for an actor
 * class. E.g.:
 * <pre>
 * public static Props props(final String alias) {
 *     return Props.create(MyDriverActor.class, alias);
 * }
 * </pre>
 *
 * @see se.sawano.akka.chicken.model.Racecourse
 */
public class L3DriverActor {

    // TODO expert mode...
}
