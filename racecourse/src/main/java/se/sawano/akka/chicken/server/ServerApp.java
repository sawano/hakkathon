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

package se.sawano.akka.chicken.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sawano.akka.chicken.model.racecourse.DriverStats;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static akka.actor.ActorRef.noSender;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class ServerApp {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ActorSystem actorSystem;
    private ActorRef racecourse;
    private final AtomicReference<CurrentScores> currentScores = new AtomicReference<>();
    private DiscoveryService discoveryService = new DiscoveryService();

    public ServerApp start() {
        logger.debug("Creating Akka actor system...");

        final Config config = ConfigFactory.load("application-racecourse");
        actorSystem = ActorSystem.create(RacecourseActor.systemName(), config);
        racecourse = actorSystem.actorOf(RacecourseActor.props(), RacecourseActor.actorName());

        logger.debug("Actor system created");

        registerForScoreUpdates();
        discoveryService.start();

        return this;
    }

    private void registerForScoreUpdates() {
        final ActorRef scoresListener =
                actorSystem.actorOf(Props.create(ScoresListener.class, () -> new ScoresListener(currentScores::set)));
        actorSystem.eventStream().subscribe(scoresListener, CurrentScores.class);
    }

    public void stop() {
        discoveryService.stop();
        actorSystem.shutdown();
    }

    public List<DriverStats> scores() {
        return ofNullable(currentScores.get()).orElse(new CurrentScores(emptyList())).scores;
    }

    public void resetScores() {
        racecourse.tell(RacecourseActor.InternalMessages.RESET_SCORES, noSender());
    }

    private static final class ScoresListener extends UntypedActor {

        private final Consumer<CurrentScores> consumer;

        private ScoresListener(final Consumer<CurrentScores> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onReceive(final Object message) throws Exception {
            if (CurrentScores.class.isInstance(message)) {
                consumer.accept((CurrentScores) message);
            } else {
                unhandled(message);
            }
        }
    }

    public static void main(final String... args) throws Exception {
        try {
            new ServerApp().start();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }
}
