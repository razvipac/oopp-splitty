// CHECKSTYLE:OFF
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import commons.request_body.ParticipantRequestBody;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

import commons.Participant;

import java.util.List;

public class ParticipantUtils {

    private final String SERVER;

    public ParticipantUtils(String SERVER) {
        this.SERVER = SERVER;
    }

    /**
     * Gets all the participants of an event.
     *
     * @param code The code of the event
     * @return All participants of the event as a List
     */
    public List<Participant> getParticipants(String code) {
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

        ParticipantRequestBody requestBody = new ParticipantRequestBody(
                p.getName(),
                p.getEmail(),
                p.getIban(),
                p.getBic()
        );

        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path(endpoint)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(requestBody, APPLICATION_JSON));

        // Check the response status code
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) return true;
        else return false;
    }

}
