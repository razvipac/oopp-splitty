package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

import commons.Participant;
import commons.Event;

import java.util.List;

public class EventUtils {

    private static final String SERVER = "http://localhost:8080/";

    public List<Event> getAllEvents() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Event>>() {
                });
    }

    public Event addEvent(String eventName) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/")
                .queryParam("name", eventName)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(eventName, APPLICATION_JSON), Event.class);
    }

    public List<Participant> getParticipants(String code) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/" + code + "/expense")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Participant>>() {
                });
    }

}
