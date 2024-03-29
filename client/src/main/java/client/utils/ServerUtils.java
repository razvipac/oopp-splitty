// CHECKSTYLE:OFF
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

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import client.scenes.Admin;
import commons.*;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {

    private static ServerUtils serverUtils;
    private EventUtils eventUtils;
    private ParticipantUtils participantUtils;
    private DebtUtils debtUtils;
    private JsonUtils jsonUtils;

    // Server address
    private static final String SERVER = "http://localhost:8080/";

    /**
     * Utility class for managing server-related functionality. Follows the Singleton design
     * pattern (ensures only one instance exists throughout the application).
     */
    private ServerUtils() {
        eventUtils = new EventUtils(SERVER);
        participantUtils = new ParticipantUtils(SERVER);
        debtUtils = new DebtUtils(SERVER);
        jsonUtils = new JsonUtils(SERVER);
    }

    /**
     * Gets single instance of ServerUtils.
     * If the instance does not exist, a new one is created.
     *
     * @return The instance of ServerUtils
     */
    public static synchronized ServerUtils getServerUtils() {
        if (serverUtils == null) {
            serverUtils = new ServerUtils();
        }
        return serverUtils;
    }

    /**
     * Gets the EventUtils instance.
     *
     * @return The EventUtils instance
     */
    public EventUtils getEventUtils() {
        return eventUtils;
    }

    /**
     * Gets the ParticipantUtils instance.
     *
     * @return The ParticipantUtils instance
     */
    public ParticipantUtils getParticipantUtils() {
        return participantUtils;
    }

    /**
     * Gets the DebtUtils instance.
     *
     * @return The DebtUtils instance
     */
    public DebtUtils getDebtUtils() {
        return debtUtils;
    }

    public JsonUtils getJsonDumpUtils() {
        return jsonUtils;
    }
}