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

import commons.dto.*;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";

    /**
     * Gets all events.
     *
     * @return All events as a List
     */
    public List<EventDTO> getAllEvents() {
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
    public EventDTO createEvent(String eventName) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/")
                .queryParam("name", eventName)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(eventName, APPLICATION_JSON), EventDTO.class);
    }
    /**
     * Creates an event with the given event name.
     * @param eventCode the name of the event
     * @return the created Event
     */
    public EventDTO deleteEvent(String eventCode) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/")
                .queryParam("eventCode", eventCode)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(EventDTO.class);
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
     * Gets a single participant from the server
     * @param eventCode eventCode of the event to which the participant belongs
     * @param name name of the participant
     * @return the ParticipantDTO instance corresponding to that participant, or null if not found
     */
    public ParticipantDTO getParticipant(String eventCode, String name){
        List<ParticipantDTO> participants = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/" + eventCode + "/participant")
                .queryParam("name", name)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<ParticipantDTO>>() {});

        // Check if participants list is not empty
        if (!participants.isEmpty()) {
            return participants.get(0); // Return the first participant
        } else {
            return null; // Participant not found
        }
    }

    /**
     * Adds Participant to server
     * @param p ParticipantDTO corresponding to the Participant to add
     * @param eventCode event code of the participant to add
     * @return True iff add was successful, false otherwise
     */
    public boolean addParticipant(ParticipantDTO p, String eventCode) {
        String endpoint = "api/v1/" + eventCode + "/participant";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path(endpoint)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(p, APPLICATION_JSON));

        // Check the response status code
        // TODO: should check for error types and pass that information on to user
        return response.getStatus() == Response.Status.CREATED.getStatusCode();
    }

    /**
     * Updates participant in the server
     * @param body ParticipantDTO containing the updated fields
     * @param eventCode Event code of participant
     * @param name (Old) name of participant
     * @return True iff successful, false otherwise.
     */
    public boolean updateParticipant(ParticipantDTO body, String eventCode, String name) {
        String endpoint = "api/v1/" + eventCode + "/participant";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path(endpoint)
                .queryParam("name", name)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(body, APPLICATION_JSON));

        // Check the response status code
        // TODO: should check for error types and pass that information on to user
        return response.getStatus() == Response.Status.OK.getStatusCode() ||
                response.getStatus() == Response.Status.NO_CONTENT.getStatusCode();
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
     * @param code Code of event
     * @return True iff add was successful, false otherwise
     */
    public boolean addExpense(ExpenseDTO e, String code) {
        String endpoint = "api/v1/" + code + "/expense";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path(endpoint)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(e, APPLICATION_JSON));

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
    public List<DebtDTO> getAllOpenDebts(String eventCode) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER)
                .path("api/v1/" + eventCode + "/debts")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     *
     * @return Gets the Json dump
     */
    public List<JSONDumpEventDTO> getJSON() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/admin/jsondump")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Restore event from JSON
     * @param body EventResponseBody
     * @return True iff successfully created, false otherwise
     */
    public boolean restoreEvent(List<JSONDumpEventDTO> body) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/admin/jsondump")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(body, APPLICATION_JSON));
        return response.getStatus() == Response.Status.CREATED.getStatusCode();
    }
}
