package client.utils;

import commons.Event;
import commons.response_body.EventResponseBody;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class JsonUtils {
    private final String SERVER;

    public JsonUtils(String SERVER) {
        this.SERVER = SERVER;
    }

    /**
     * Gets all events.
     *
     * @return All events as a List
     */
    public List<EventResponseBody> getJSON() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/v1/admin/jsondump")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

}
