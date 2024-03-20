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

public class ServerUtils {

    private static ServerUtils serverUtils;
    private EventUtils eventUtils;
    private ParticipantUtils participantUtils;

    // Server address
    private static final String SERVER = "http://localhost:8080/";

    /**
     * Utility class for managing server-related functionality. Follows the Singleton design
     * pattern (ensures only one instance exists throughout the application).
     */
    private ServerUtils() {
        eventUtils = new EventUtils(SERVER);
        participantUtils = new ParticipantUtils(SERVER);
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

    // Code for reference

//    public void getQuotesTheHardWay() throws IOException, URISyntaxException {
//        var url = new URI("http://localhost:8080/api/quotes").toURL();
//        var is = url.openConnection().getInputStream();
//        var br = new BufferedReader(new InputStreamReader(is));
//        String line;
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
//    }
//
//    public List<Quote> getQuotes() {
//        return ClientBuilder.newClient(new ClientConfig()) //
//                .target(SERVER).path("api/quotes") //
//                .request(APPLICATION_JSON) //
//                .accept(APPLICATION_JSON) //
//                .get(new GenericType<List<Quote>>() {
//                });
//    }
//
//    public Quote addQuote(Quote quote) {
//        return ClientBuilder.newClient(new ClientConfig()) //
//                .target(SERVER).path("api/quotes") //
//                .request(APPLICATION_JSON) //
//                .accept(APPLICATION_JSON) //
//                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
//    }

}