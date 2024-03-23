// CHECKSTYLE:OFF
/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import commons.*;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {

    private static ServerUtils serverUtils;
    private EventUtils eventUtils;
    private ParticipantUtils participantUtils;

    // Server address
    private static final String SERVER = "http://localhost:8080/";

    /**
     * Utility class for managing server-related functionality. Follows the Singleton design
     * pattern (ensures only one instance exists throughout the application).
     */
    private ServerUtils() {
        eventUtils = new EventUtils(SERVER);
        participantUtils = new ParticipantUtils(SERVER);
    }

    /**
     * Gets single instance of ServerUtils.
     * If the instance does not exist, a new one is created.
     *
     * @return The instance of ServerUtils
     */
    public static synchronized ServerUtils getServerUtils() {
        if (serverUtils == null) {
            serverUtils = new ServerUtils();
        }
        return serverUtils;
    }

    /**
     * Gets the EventUtils instance.
     *
     * @return The EventUtils instance
     */
    public EventUtils getEventUtils() {
        return eventUtils;
    }

    /**
     * Gets the ParticipantUtils instance.
     *
     * @return The ParticipantUtils instance
     */
    public ParticipantUtils getParticipantUtils() {
        return participantUtils;
    }

        /**
     * Gets all events.
     * @return All events as a List
     */
    public List<Event> getAllEvents() {
       return ClientBuilder.newClient(new ClientConfig())
               .target(SERVER).path("api/v1/")
               .request(APPLICATION_JSON)
               .accept(APPLICATION_JSON)
               .get(new GenericType<List<Event>>() {
               });
   }

    /**
     * Creates an event with the given event name.
     * @param eventName the name of the event
     * @return the created Event
     */
    public Event createEvent(String eventName) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/")
                .queryParam("name", eventName)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(eventName, APPLICATION_JSON), Event.class);
    }

    /**
     * Creates an event with the given event name.
     * @param eventCode the name of the event
     * @return the created Event
     */
    public Event deleteEvent(String eventCode) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/")
                .queryParam("eventCode", eventCode)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(Event.class);
    }
    /**
     * Gets all the participants of an event.
     * @param code The code of the event
     * @return All participants of the event as a List
     */
    public List<Participant> getParticipants(String code) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/" + code + "/expense")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }



//    public Participant addParticipant(Participant p) {
//        String code = p.getEvent().getCode();
//        String endpoint = "api/v1/" + code + "/participant";
//        String name = p.getName();
//        String email = p.getEmail();
//        String iban = p.getIban();
//        String bic = p.getBic();
//
//        return ClientBuilder.newClient(new ClientConfig())
//                .target(SERVER).path(endpoint)
//                .request(APPLICATION_JSON)
//                .accept(APPLICATION_JSON)
//                .post(Entity.entity(..., APPLICATION_JSON), Participant.class);
//    }

}