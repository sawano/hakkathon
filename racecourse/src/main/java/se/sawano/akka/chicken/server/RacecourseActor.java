package se.sawano.akka.chicken.server;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import se.sawano.akka.chicken.events.*;
import se.sawano.akka.chicken.messages.DriverSignUpRequest;
import se.sawano.akka.chicken.model.Racecourse;
import se.sawano.akka.chicken.model.racecourse.DefaultRacecourse;
import se.sawano.akka.chicken.model.racecourse.Race;
import se.sawano.akka.chicken.model.racecourse.RaceNotFoundException;
import se.sawano.akka.chicken.model.racecourse.RaceResult;
import se.sawano.akka.japi.messagehandling.MessageDelegatingActor;

import java.time.Duration;
import java.util.List;

import static akka.actor.ActorRef.noSender;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static se.sawano.akka.chicken.messages.AdminMessages.PING;
import static se.sawano.akka.chicken.messages.AdminMessages.PONG;
import static se.sawano.akka.chicken.server.RacecourseActor.InternalMessages.RESET_SCORES;
import static se.sawano.akka.chicken.server.RacecourseActor.InternalMessages.START_RACES;

public class RacecourseActor extends MessageDelegatingActor implements Racecourse {

    public static final Duration INTERVAL_BETWEEN_RACES = ofSeconds(3);

    public enum InternalMessages {
        START_RACES,
        RESET_SCORES
    }

    /**
     * The name of the actor system. This is the same name that player systems will use to define the remote playground system.
     *
     * @return the name of the actor system for the playground
     */
    public static String systemName() {
        return "chicken";
    }

    public static String actorName() {
        return "racecourse";
    }

    public static Props props() {
        return Props.create(RacecourseActor.class);
    }

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final EventBus eventBus = new EventBus(context().system().eventStream());
    private final DefaultRacecourse racecourse = new DefaultRacecourse();

    private Cancellable raceRunner;

    @Override
    public void signUpForRace(final DriverSignUpRequest request) {
        final boolean signedUp = racecourse.signUpForRace(request);
        if (signedUp) {
            // TODO might add deathwatch and remove driver on termination
            eventBus.subscribe(sender());
            eventBus.publish(new DriverSignedUp(request.driverId));
            log.info("Driver {} signed up to race ({})", request.driverId.alias(), sender());
        }
        else {
            log.debug("Driver {} is already signed up", request.driverId.alias());
        }
    }

    @Override
    public void registerDriverChoice(final RaceDecision decision) {
        try {
            racecourse.registerDriverChoice(decision);
            log.info("Driver {} chooses: {}", decision.respondingDriver.alias(), decision.choice);
        } catch (final RaceNotFoundException e) {
            log.warning("No race found with id {}.", decision.raceId);
        } catch (final IllegalArgumentException e) {
            log.warning("Driver {} tried to make a decision on a race she is not part of", decision.respondingDriver);
        }
    }

    private void notifyDriversOfResult(final RaceResult raceResult) {
        final RaceFinished resultEvent = new RaceFinished(raceResult.raceId(),
                                                          raceResult.driverOne(),
                                                          raceResult.resultForDriverOne(),
                                                          raceResult.driverTwo(),
                                                          raceResult.resultForDriverTwo());
        eventBus.publish(resultEvent);
        eventBus.publish(new CurrentScores(racecourse.scores()));
    }

    @Override
    public void onReceive(final Object message) {
        if (PING.equals(message)) {
            log.debug("Got ping from {}", sender());
            sender().tell(PONG, self());
        }
        else if (START_RACES.equals(message)) {
            startRaces();
        }
        else if (RESET_SCORES.equals(message)) {
            racecourse.resetScores();
        }
        else if (CurrentScoresRequest.class.isInstance(message)) {
            sendScores();
        }
        else if (RaceResult.class.isInstance(message)) {
            notifyDriversOfResult(((RaceResult) message));
        }
        else {
            super.onReceive(message);
        }
    }

    private void sendScores() {
        sender().tell(new CurrentScores(racecourse.scores()), self());
    }

    private void startRaces() {
        final ActorRef safeRef = self();
        cleanupDeadRaces();
        final List<Race> races = racecourse.startRaces(result -> safeRef.tell(result, noSender()));
        tellDriversToRace(races);
    }

    private void cleanupDeadRaces() {
        final List<Race> deadRaces = racecourse.cleanUpDeadRaces();
        deadRaces.stream()
                .forEach(race -> log.info("Removed dead race between {} and {}", race.driverOne(), race.driverTwo()));
    }

    private void tellDriversToRace(final List<Race> races) {
        races.forEach(race -> {
            log.info("{} will race against {}", race.driverOne().alias(), race.driverTwo().alias());
            eventBus.publish(new RaceCommand(race.id(), race.driverOne(), race.driverTwo()));
        });
    }

    @Override
    public void postStop() throws Exception {
        if (raceRunner != null) {
            raceRunner.cancel();
            raceRunner = null;
        }
    }

    @Override
    public void preStart() throws Exception {
        scheduleRacesToRunAtInterval(INTERVAL_BETWEEN_RACES);
    }

    private void scheduleRacesToRunAtInterval(final Duration interval) {
        raceRunner = context().system().scheduler().schedule(scala.concurrent.duration.Duration.create(1, SECONDS),
                                                             scala.concurrent.duration.Duration
                                                                     .fromNanos(interval.toNanos()),
                                                             self(),
                                                             START_RACES,
                                                             context().dispatcher(),
                                                             noSender());
        log.debug("Scheduled racing");
    }
}
