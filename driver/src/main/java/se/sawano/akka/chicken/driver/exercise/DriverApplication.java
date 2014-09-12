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

package se.sawano.akka.chicken.driver.exercise;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import se.sawano.akka.chicken.driver.exercise.spoiler.DriverActor;

public class DriverApplication {

    public static Props props() {
        return DriverActor.props("MyName");
    }

    public static void main(final String... args) {
        final Config config = ConfigFactory.load();
        ActorSystem actorSystem = ActorSystem.create("driver-system", config);
        final ActorRef driver = actorSystem.actorOf(props(), "my-driver");
    }

}
