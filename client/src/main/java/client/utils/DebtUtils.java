// CHECKSTYLE:OFF
package client.utils;

import commons.Debt;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class DebtUtils {
    private final String SERVER;

    public DebtUtils(String SERVER) {
        this.SERVER = SERVER;
    }

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