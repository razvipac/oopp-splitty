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

import commons.dto.*;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private final String SERVER = "localhost:8080";
    private final String HTTP_SERVER = "https://" + SERVER + "/";
    private StompSession wsSession = wsConnect("ws://" + SERVER + "/ws-connect");

    private StompSession wsConnect(String url){
        var wsClient = new StandardWebSocketClient();
        var stompClient = new WebSocketStompClient(wsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            return stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new IllegalStateException();
    }

    private <T> void registerForWebSocketMessages(String dest,
                                                 Consumer<WSWrapperResponseBody<T>> consumer){
        wsSession.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WSWrapperResponseBody.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((WSWrapperResponseBody<T>) payload);
            }
        });
    }

    public void registerForWebSocketMessagesForEvent(String eventCode,
                                                     Consumer<WSWrapperResponseBody<EventDTO>> consumer){
        registerForWebSocketMessages("/api/v1/channel/" + eventCode, consumer);
        registerForWebSocketMessages("/api/v1/channel/event", consumer);
    }

    public void registerForWebSocketMessagesForParticipant(String eventCode,
                                                     Consumer<WSWrapperResponseBody<ParticipantDTO>> consumer){
        registerForWebSocketMessages("/api/v1/channel/" + eventCode + "/participant", consumer);
    }

    public void registerForWebSocketMessagesForExpense(String eventCode,
                                                     Consumer<WSWrapperResponseBody<ExpenseDTO>> consumer){
        registerForWebSocketMessages("/api/v1/channel/" + eventCode + "/expense", consumer);
    }

    /**
     * Gets all events.
     *
     * @return All events as a List
     */
    public List<EventDTO> getAllEvents() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(HTTP_SERVER).path("api/v1/")
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
                .target(HTTP_SERVER).path("api/v1/")
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
                .target(HTTP_SERVER).path("api/v1/")
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
                .target(HTTP_SERVER).path("api/v1/" + code + "/participant")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }


    /**
     * Gets a single participant from the HTTP_SERVER
     * @param eventCode eventCode of the event to which the participant belongs
     * @param name name of the participant
     * @return the ParticipantDTO instance corresponding to that participant
     */
    public ParticipantDTO getParticipant(String eventCode, String name){
        return (ParticipantDTO) ClientBuilder.newClient(new ClientConfig())
                .target(HTTP_SERVER).path("api/v1/" + eventCode + "/participant?name=" + name)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(List.class)
                .getFirst();
    }

    /**
     * Adds Participant to HTTP_SERVER
     * @param p ParticipantDTO corresponding to the Participant to add
     * @param eventCode event code of the participant to add
     * @return True iff add was successful, false otherwise
     */
    public boolean addParticipant(ParticipantDTO p, String eventCode) {
        String endpoint = "api/v1/" + eventCode + "/participant";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(HTTP_SERVER).path(endpoint)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(p, APPLICATION_JSON));

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
                .target(HTTP_SERVER).path("api/v1/" + code + "/expense")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Adds Expense to HTTP_SERVER
     * @param e Expense to add
     * @param code Code of event
     * @return True iff add was successful, false otherwise
     */
    public boolean addExpense(ExpenseDTO e, String code) {
        String endpoint = "api/v1/" + code + "/expense";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(HTTP_SERVER).path(endpoint)
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
                .target(HTTP_SERVER)
                .path("api/v1/" + eventCode + "/debts")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    // Password methods

    /**
     * Checks if the given String matches the HTTP_SERVER's password
     *
     * @param input The entered password
     * @return True iff input matches password, false otherwise.
     */
    public Boolean matchesPassword(String input) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(HTTP_SERVER)
                .path("api/v1/admin/auth/matches-password")
                .queryParam("input", input)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    // JSON Dump methods

    /**
     *
     * @return Gets the Json dump
     */
    public List<JSONDumpEventDTO> getJSON() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(HTTP_SERVER).path("api/v1/admin/jsondump")
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
                .target(HTTP_SERVER).path("api/v1/admin/jsondump")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(body, APPLICATION_JSON));
        return response.getStatus() == Response.Status.CREATED.getStatusCode();
    }
}
