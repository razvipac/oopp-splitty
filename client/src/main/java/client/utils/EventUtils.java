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

    /**
     * Gets all events.
     * @return All events as a List
     */
//    public List<Event> getAllEvents() {
//       return ClientBuilder.newClient(new ClientConfig())
//               .target(SERVER).path("api/v1/")
//               .request(APPLICATION_JSON)
//               .accept(APPLICATION_JSON)
//               .get(new GenericType<List<Event>>() {
//               });
//   }

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
     * Gets all the participants of an event.
     * @param code The code of the event
     * @return All participants of the event as a List
     */
    public List<Participant> getParticipants(String code) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/" + code + "/expense")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Participant>>() {
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
