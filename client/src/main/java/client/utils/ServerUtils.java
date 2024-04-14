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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.dto.*;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private final String httpServerUrl;
    private final String serverUrl;
    private final Map<Object, StompSession> wsSessions;
    private final Map<Object, Set<String>> destinations;

    /**
     * Constructor for ServerUtils
     */
    public ServerUtils() {
        try {
            File file = new File("client/src/main/resources/userSettings/config.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);
            wsSessions = new HashMap<>();
            destinations = new HashMap<>();
            if (rootNode.has("serverURL")) {
                serverUrl = rootNode.get("serverURL").asText();
                httpServerUrl = "http://" + serverUrl + "/";
            } else {
                throw new RuntimeException("Server URL not found");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private StompSession wsConnect(String url){
        var wsClient = new StandardWebSocketClient();
        var stompClient = new WebSocketStompClient(wsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            return stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();
        } catch (ExecutionException e) {
            throw new ProcessingException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new IllegalStateException();
    }

    /**
     * Registers a consumer for receiving Web Socket STOMP messages from the server.
     * Does not allow registering multiple consumers on the same path, for the same session key.
     * @param sessionKey unique key that identifies said session
     * @param dest destination to listen on
     * @param consumer consumer callback for handling messages
     * @param <T> type of fetched messages
     */
    private <T> void registerForWebSocketMessages(Object sessionKey, String dest,
                                                  Consumer<WSWrapperResponseBody<T>> consumer){

        if (wsSessions.get(sessionKey) == null || !wsSessions.get(sessionKey).isConnected()){
            wsSessions.put(sessionKey, wsConnect("ws://" + serverUrl + "/ws-connect"));
            destinations.put(sessionKey, new HashSet<>());
        }
        if (!destinations.get(sessionKey).contains(dest)){
            destinations.get(sessionKey).add(dest);
            wsSessions.get(sessionKey).subscribe(dest, new StompFrameHandler() {
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
    }

    /**
     * Registers a consumer for handling changes of participant entities in a given event
     * @param eventCode code of the event on which to listen
     * @param consumer consumer for handling changes
     * @param sessionKey sets the key for this session
     */
    public void registerForWebSocketUpdatesOnParticipant(Object sessionKey, String eventCode,
                                                         Consumer<WSWrapperResponseBody<ParticipantDTO>> consumer)
    {
        registerForWebSocketMessages(sessionKey, "/api/websocket/v1/channel/" + eventCode + "/participant", consumer);
    }

    /**
     * Registers a consumer for handling changes of all event entities
     * @param consumer consumer for handling changes
     * @param sessionKey sets the key for this session
     */
    public void registerForWebSocketUpdatesForAllEvents(Object sessionKey,
                                                        Consumer<WSWrapperResponseBody<EventDTO>> consumer)
    {
        registerForWebSocketMessages(sessionKey, "/api/websocket/v1/channel/event", consumer);
    }

    /**
     * Registers a consumer for handling changes of all entities on given as well as event deletions and creations
     * @param eventCode code of the event on which to listen
     * @param consumer consumer for handling changes
     * @param sessionKey sets the key for this session
     */
    public void registerForWebSocketUpdatesForTheWholeEvent(Object sessionKey, String eventCode,
                                                            Consumer<WSWrapperResponseBody> consumer)
    {
        registerForWebSocketMessages(
                sessionKey,
                "/api/websocket/v1/channel/event",
                consumer::accept
        );
        registerForWebSocketMessages(
                sessionKey,
                "/api/websocket/v1/channel/" + eventCode,
                consumer::accept
        );
        registerForWebSocketMessages(
                sessionKey,
                "/api/websocket/v1/channel/" + eventCode + "/participant",
                consumer::accept
        );
        registerForWebSocketMessages(
                sessionKey,
                "/api/websocket/v1/channel/" + eventCode + "/expense",
                consumer::accept
        );
    }

    /**
     * Disconnects the session with given key
     * @param sessionKey key of the session to be disconnected
     */
    public void disconnectWSSession(Object sessionKey){
        this.wsSessions.get(sessionKey).disconnect();
        this.destinations.remove(sessionKey);
        this.wsSessions.remove(sessionKey);
    }

    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor();

    /**
     * Registers a consumer for receiving long polling updates for Debts
     * @param eventCode eventCode
     * @param consumer consumer
     */
    public void registerForLongPollingDebtUpdates(String eventCode, Consumer<List<DebtDTO>> consumer){
        EXEC.submit(() -> {
            while (!Thread.interrupted()) {
                var res = ClientBuilder.newClient(new ClientConfig())
                        .target(httpServerUrl).path("api/v1/" + eventCode + "/debt/updates")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);

                if (res.getStatus() == 408) {
                    continue;
                }

                consumer.accept(res.readEntity(List.class));
            }
        });
    }

    /**
     * Gets all events.
     *
     * @return All events as a List
     */
    public List<EventDTO> getAllEvents() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path("api/v1/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Gets a specific event or null if it does not exist
     *
     * @param eventCode code of the event to get
     * @return the seeked event's DTO or null if it does not exists
     */
    public EventDTO getEvent(String eventCode) {
        List<EventDTO> matchingEvents = getAllEvents()
                .stream()
                .filter(eventDTO -> eventDTO.code().equals(eventCode))
                .toList();

        return matchingEvents.isEmpty() ? null : matchingEvents.getFirst();
    }

    /**
     * Creates an event with the given event name.
     *
     * @param eventName the name of the event
     * @return the created Event
     */
    public EventDTO createEvent(String eventName) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path("api/v1/")
                .queryParam("name", eventName)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(eventName, APPLICATION_JSON), EventDTO.class);
    }

    /**
     * Updates an event with the given event name.
     *
     * @param eventDTO the eventDTO of the event to update
     * @param eventName the name of the event
     * @return the created Event
     */
    public EventDTO updateEvent(EventDTO eventDTO, String eventName) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path("api/v1/" + eventDTO.code())
                .queryParam("name", eventName)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(eventName, APPLICATION_JSON), EventDTO.class);
    }
    /**
     * Creates an event with the given event name.
     * @param eventCode the name of the event
     * @return the created Event
     */
    public boolean deleteEvent(String eventCode) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path("api/v1/")
                .queryParam("eventCode", eventCode)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();

        return response.getStatus() == Response.Status.OK.getStatusCode();
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
                .target(httpServerUrl).path("api/v1/" + code + "/participant")
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
        Client client = ClientBuilder.newClient(new ClientConfig());
        URI uri = URI.create(httpServerUrl + "api/v1/" + eventCode + "/participant?name=" + name);
        Response response = client.target(uri).request(APPLICATION_JSON).get();
        List<ParticipantDTO> participantDTOs = response.readEntity(new GenericType<List<ParticipantDTO>>() {});
        if (participantDTOs.isEmpty()) return null;
        return participantDTOs.getFirst();
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
                .target(httpServerUrl).path(endpoint)
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
                .target(httpServerUrl).path(endpoint)
                .queryParam("name", name)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(body, APPLICATION_JSON));

        // Check the response status code
        // TODO: should check for error types and pass that information on to user
        return response.getStatus() == Response.Status.OK.getStatusCode() ||
                response.getStatus() == Response.Status.NO_CONTENT.getStatusCode();
    }

    /**
     * Deletes participant in the server.
     * @param eventCode Event code of participant
     * @param name (Old) name of participant
     * @return True iff successful, false otherwise.
     */
    public boolean deleteParticipant(String eventCode, String name) {
        String endpoint = "api/v1/" + eventCode + "/participant";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path(endpoint)
                .queryParam("name", name)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();

        return response.getStatus() == Response.Status.OK.getStatusCode();
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
                .target(httpServerUrl).path("api/v1/" + code + "/expense")
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
                .target(httpServerUrl).path(endpoint)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(e, APPLICATION_JSON));

        // Check the response status code
        // TODO: should check for error types and pass that information on to user
        return response.getStatus() == Response.Status.CREATED.getStatusCode();
    }

    /**
     * Updates a given expense in the server
     * @param body DTO containing the updated information
     * @param eventCode code of the event on which to update the given expense
     * @return true if deleted, false otherwise
     */
    public boolean updateExpense(ExpenseDTO body, String eventCode){
        String endpoint = "api/v1/" + eventCode + "/expense";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path(endpoint)
                .queryParam("id", body.id())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(body, APPLICATION_JSON));

        // Check the response status code
        // TODO: should check for error types and pass that information on to user
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    /**
     * Deletes a given expense from the server
     * @param e dto for the expense to be deleted
     * @param eventCode code of the event on which to delete the given expense
     * @return true if deleted, false otherwise
     */
    public boolean deleteExpense(ExpenseDTO e, String eventCode){
        String endpoint = "api/v1/" + eventCode + "/expense";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path(endpoint)
                .queryParam("id", e.id())
                .queryParam("participantName", e.paidByName())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();

        // Check the response status code
        // TODO: should check for error types and pass that information on to user
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    // Debt methods

    /**
     * Get all open debts for a specific event.
     *
     * @param eventCode The code of the event for which debts are to be retrieved
     * @return A List containing all open debts for the specified event
     */
    public List<DebtDTO> getAllDebts(String eventCode) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl)
                .path("api/v1/" + eventCode + "/debt")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Toggles the received status for a given debt entity
     * @param eventCode event code of the event to which it belongs
     * @param debtDTO DTO of a given debt entity
     * @return state after toggle
     */
    public boolean toggleDebtReceivedStatus(String eventCode, DebtDTO debtDTO){
        String endpoint = "api/v1/" + eventCode + "/debt";

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path(endpoint)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(debtDTO, APPLICATION_JSON));

        return response.readEntity(new GenericType<DebtDTO>() {}).received();
    }

    /**
     * Regenerates debts from expenses
     * @param eventCode code of the event for which to regenerate debts
     */
    public void regenerateDebts(String eventCode){
        ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl)
                .path("api/v1/" + eventCode + "/debt")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(null);
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
                .target(httpServerUrl)
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
                .target(httpServerUrl).path("api/v1/admin/jsondump")
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
    public boolean restoreEvent(JSONDumpEventDTO body) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(httpServerUrl).path("api/v1/admin/jsondump")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(body, APPLICATION_JSON));
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    /**
     * Stops the execution of threads
     */
    public void stop(){
        EXEC.shutdownNow();
    }
}
