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

import java.util.List;

import commons.*;
import commons.dto.ExpenseDTO;
import commons.dto.ParticipantDTO;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {

    private static ServerUtils serverUtils;

    // Server address
    private static final String SERVER = "http://localhost:8080/";

    /**
     * Utility class for managing server-related functionality. Follows the Singleton design
     * pattern (ensures only one instance exists throughout the application).
     */
    private ServerUtils() {
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

    // Event methods

    /**
     * Gets all events.
     *
     * @return All events as a List
     */
    public List<Event> getAllEvents() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Creates an event with the given event name.
     *
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

    // Participant methods

    /**
     * Gets all the participants of an event.
     *
     * @param code The code of the event
     * @return All participants of the event as a List
     */
    public List<ParticipantDTO> getParticipants(String code) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/" + code + "/participant")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Adds Participant to server
     * @param p Participant to add
     * @return True iff add was successful, false otherwise
     */
    public boolean addParticipant(Participant p) {
        String code = p.getEvent().getCode();
        String endpoint = "api/v1/" + code + "/participant";

        ParticipantDTO participantDTO = new ParticipantDTO(
                p.getName(),
                p.getEmail(),
                p.getIban(),
                p.getBic()
        );

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path(endpoint)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(participantDTO, APPLICATION_JSON));

        // Check the response status code
        // TODO: should check for error types and pass that information on to user
        return response.getStatus() == Response.Status.CREATED.getStatusCode();
    }

    // Expense methods

    /**
     * Gets all the expenses of an event.
     *
     * @param code The code of the event
     * @return All expenses of the event as a List
     */
    public List<ExpenseDTO> getExpenses(String code) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/" + code + "/expense")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Adds Expense to server
     * @param e Expense to add
     * @return True iff add was successful, false otherwise
     */
    public boolean addExpense(Expense e, String code) {
        String endpoint = "api/v1/" + code + "/expense";

        ExpenseDTO expenseDTO = new ExpenseDTO(
                e.getPrice(),
                e.getItem(),
                e.getPaidBy().getName()
        );

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path(endpoint)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(expenseDTO, APPLICATION_JSON));

        // Check the response status code
        // TODO: should check for error types and pass that information on to user
        return response.getStatus() == Response.Status.CREATED.getStatusCode();
    }

    // Debt methods

    /**
     * Get all open debts for a specific event.
     *
     * @param eventCode The code of the event for which debts are to be retrieved
     * @return A List containing all open debts for the specified event
     */
    public List<Debt> getAllOpenDebts(String eventCode) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER)
                .path("api/v1/" + eventCode + "/debts")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

}