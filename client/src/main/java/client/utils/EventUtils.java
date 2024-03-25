// CHECKSTYLE:OFF
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

import commons.Event;

import java.util.List;

public class EventUtils {

    private final String SERVER;

    public EventUtils(String SERVER) {
        this.SERVER = SERVER;
    }

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

}
