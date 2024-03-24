// CHECKSTYLE:OFF
package client.utils;

import commons.Debt;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;

import java.util.List;

public class DebtUtils {
    private final String SERVER;

    public DebtUtils(String server) {
        this.SERVER = server;
    }

    /**
     * Get all open debts for a specific event.
     *
     * @param eventCode The code of the event for which debts are to be retrieved
     * @return A List containing all open debts for the specified event
     */
    public List<Debt> getAllOpenDebts(String eventCode) {
        return ClientBuilder.newClient()
                .target(SERVER)
                .path("api/v1/" + eventCode + "/debts")
                .request()
                .get(new GenericType<>() {});
    }
}