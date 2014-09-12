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

package se.sawano.akka.chicken.driver.exercise.level2;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;
import se.sawano.akka.chicken.driver.DefaultRacingStrategy;
import se.sawano.akka.chicken.driver.RacingStrategy;
import se.sawano.akka.chicken.events.*;
import se.sawano.akka.chicken.messages.DriverSignUpRequest;
import se.sawano.akka.chicken.model.Driver;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.Racecourse;
import se.sawano.akka.japi.messagehandling.MessageDelegatingActor;

import static java.util.concurrent.TimeUnit.SECONDS;
import static se.sawano.akka.chicken.messages.AdminMessages.PONG;

/**
 * Level 2 base implementation. Please refer to the javadoc of {@link Racecourse} on game details such as the flow of events etc.
 * <p>
 * For convenience and as a recommended practice it is good to provide a factory method for creating {@link akka.actor.Props} for an actor
 * class. E.g.:
 * <pre>
 * public static Props props(final String alias) {
 *     return Props.create(MyDriverActor.class, alias);
 * }
 * </pre>
 *
 * @see Racecourse
 */
public abstract class L2DriverActor extends MessageDelegatingActor implements Driver {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final DriverId id;
    private final RacingStrategy strategy = new DefaultRacingStrategy();

    public L2DriverActor(final String alias) {
        id = new DriverId(alias);
    }

    /**
     * Publishes an event on the event bus.
     *
     * @param event
     *         the event to publish
     *
     * @see EventBus
     */
    protected abstract void publishEvent(final Event event);

    /**
     * Returns this driver's racing strategy.
     *
     * @return the racing strategy
     */
    protected abstract RacingStrategy racingStrategy();

    /**
     * Gets the distributed event bus that is used by the game.
     *
     * @return the event bus
     *
     * @see EventBus
     */
    protected abstract EventBus eventBus();

    /**
     * Returns an ActorRef to the race course. If the racecourse is a remote actor it might be good practice to only
     * resolve the remote actor once.
     *
     * @return the ActorRef to the (remote) racecourse
     *
     * @see se.sawano.akka.chicken.driver.RacecourseService#resolve(akka.actor.ActorContext)
     * @see se.sawano.akka.chicken.driver.RacecourseService#lookup(akka.actor.ActorContext)
     */
    protected abstract ActorRef racecourse();

    @Override
    public abstract void race(final RaceCommand command);

    @Override
    public void acceptRaceResult(final RaceFinished raceFinished) {
        if (raceFinished.isCompetitor(id)) {
            log.info("{} says: Race against {} finished and I got {} points.",
                     id.alias(),
                     raceFinished.opponentFor(id).alias(),
                     raceFinished.resultFor(id).points());
            goToRacecourseAndSignUp();  // Race again...
        }
    }

    @Override
    public void onReceive(final Object message) {
        if (PONG.equals(message)) {
            log.info("Got PONG from {}", sender());
        }
        else if (DriverSignedUp.class.isInstance(message)) {
            if (id.equals(((DriverSignedUp) message).driverId)) {
                log.debug("Yay! I am now officially signed up to race.");
            }
        }
        else {
            super.onReceive(message);
        }
    }

    @Override
    public void preStart() throws Exception {
        eventBus().subscribe(racecourse());
        goToRacecourseAndSignUp();
    }

    private void goToRacecourseAndSignUp() {
        context().system()
                .scheduler()
                .scheduleOnce(Duration.create(1, SECONDS), racecourse(), new DriverSignUpRequest(id), context()
                        .dispatcher(), self());
    }

    @Override
    public void postStop() throws Exception {
        eventBus().unsubscribe(racecourse());
    }

}
