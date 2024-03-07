package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import org.glassfish.jersey.client.ClientConfig;

import commons.Event;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

public class EventUtils {

    private static final String SERVER = "http://localhost:8080/";

    public Event addEvent(String eventName) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/")
                .queryParam("name", eventName)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(eventName, APPLICATION_JSON), Event.class);
    }

}
