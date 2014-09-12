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

package se.sawano.akka.chicken.driver.exercise.spoiler;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;
import se.sawano.akka.chicken.driver.DefaultRacingStrategy;
import se.sawano.akka.chicken.driver.RacecourseService;
import se.sawano.akka.chicken.driver.RacingStrategy;
import se.sawano.akka.chicken.events.*;
import se.sawano.akka.chicken.messages.DriverSignUpRequest;
import se.sawano.akka.chicken.model.Choice;
import se.sawano.akka.chicken.model.Driver;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.japi.messagehandling.MessageDelegatingActor;

import static java.util.concurrent.TimeUnit.SECONDS;
import static se.sawano.akka.chicken.messages.AdminMessages.PONG;

public class DriverActor extends MessageDelegatingActor implements Driver {

    public static Props props(final String alias) {
        return Props.create(DriverActor.class, () -> new DriverActor(alias));
    }

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final EventBus eventBus = new EventBus(context().system().eventStream());
    private ActorRef racecourse = null;
    private final DriverId id;
    private final RacingStrategy strategy = new DefaultRacingStrategy();

    public DriverActor(final String alias) {
        id = new DriverId(alias);
    }

    @Override
    public void race(final RaceCommand command) {
        if (command.isCompetitor(id)) {
            log.info("{} says: I will race against {}", id.alias(), command.opponentFor(id).alias());
            eventBus().publish(new RaceDecision(command.raceId, id, makeChoice(command.opponentFor(id))));
        }
    }

    private Choice makeChoice(final DriverId opponent) {
        return strategy.raceAgainst(opponent);
    }

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

    private ActorRef racecourse() {
        if (racecourse == null) {
            racecourse = RacecourseService.resolve(context());
        }
        return racecourse;
    }

    private EventBus eventBus() {
        return eventBus;
    }
}
