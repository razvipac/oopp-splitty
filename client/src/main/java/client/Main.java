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
package client;

import client.scenes.*;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * The entry point for the client application.
     *
     * @param args Arguments passed to the application.
     * @throws URISyntaxException If there is an error in creating a URI.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * Initializes the primary stage and loads the scenes for various controllers.
     *
     * @param primaryStage The primary stage of the JavaFX application.
     * @throws IOException If an I/O error occurs while loading the FXML files.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        var startScreen = FXML.load(StartScreenCtrl.class,
                "client", "scenes", "StartScreen.fxml");
        var eventOverview = FXML.load(EventOverviewCtrl.class,
                "client", "scenes", "EventOverview.fxml");
        var contactDetails = FXML.load(ContactDetailsCtrl.class,
                "client", "scenes", "ContactDetails.fxml");
        var invitations = FXML.load(InvitationsCtrl.class,
                "client", "scenes", "Invitations.fxml");
        var openDebts = FXML.load(OpenDebtsCtrl.class,
                "client", "scenes", "OpenDebts.fxml");
        var addEditExpense = FXML.load(AddEditExpenseCtrl.class,
                "client", "scenes", "AddEditExpense.fxml");
        var admin = FXML.load(AdminCtrl.class,
                "client", "scenes", "Admin.fxml");
        var adminPassword = FXML.load(AdminPasswordCtrl.class,
                "client", "scenes", "AdminPassword.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, startScreen, eventOverview, contactDetails,
                invitations, openDebts, addEditExpense, admin, adminPassword);

        primaryStage.setOnCloseRequest(e -> INJECTOR.getInstance(ServerUtils.class).stop());
    }
}
