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

package se.sawano.akka.chicken.events;

import akka.actor.ActorRef;
import akka.event.EventStream;

import static java.util.Objects.requireNonNull;

public class EventBus {

    private final EventStream eventStream;

    public EventBus(final EventStream eventStream) {
        requireNonNull(eventStream);

        this.eventStream = eventStream;
    }

    public void subscribe(final ActorRef subscriber) {
        subscribe(subscriber, Event.class);
    }

    public <T extends Event> void subscribe(final ActorRef subscriber, final Class<T> eventType) {
        eventStream.subscribe(subscriber, eventType);
    }

    public void unsubscribe(final ActorRef subscriber) {
        eventStream.unsubscribe(subscriber);
    }

    public void publish(final Event event) {
        eventStream.publish(event);
    }

    public void publish(final Object event) {
        eventStream.publish(event);
    }
}
