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

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import client.scenes.OpenDebts;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class Main extends Application {

    // add your page as a private object below
    private final OpenDebts od = new OpenDebts(this);
    private final Invitations inv = new Invitations(this, null);
    private final ContactDetails cd = new ContactDetails(this, null);
    private final EventOverview eo = new EventOverview(this, null);
    private final StartScreen sc = new StartScreen(this);
    private final AddEditExpense aed = new AddEditExpense(this);
    private final Statistics statistics = new Statistics(this);

    private Stage primaryStage; // added this line
    private Scene mainScene; // added this line

    /**
     * Main method that starts the application
     * @param args command line arguments
     * @throws URISyntaxException if the URI is invalid or cannot be constructed
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * The start method to call the different scenes
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage; // added this line

        // Button for StartScreen page
        Button startButton = new Button("Start Screen");
        startButton.setOnAction(e -> showStartScreen());

        // Button for EventOverview page
        Button eoButton = new Button("Event Overview");
        eoButton.setOnAction(e -> showEventOverview());

        // Button for ContactDetails page
        Button cdButton = new Button("Contact Details");
        cdButton.setOnAction(e -> showContactDetails());

        // Button for Invitation page
        Button invitationButton = new Button("Invitation");
        invitationButton.setOnAction(e -> showInvitation());

        // Button for OpenDebts page
        Button odButton = new Button("Open Debts");
        odButton.setOnAction(e -> showOpenDebts());

        Button addExpenseButton = new Button("Add Expense");
        addExpenseButton.setOnAction(e -> showAddExpense());

        Button statisticsButton = new Button("Statistics");
        statisticsButton.setOnAction(e -> showStatistics());

        // Button for language selection
        Button languageButton = new LanguageButton();

        // Hbox for the language button
        HBox languageBox = new HBox(languageButton);
        languageBox.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(languageButton, Priority.ALWAYS);

        // Layout
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        layout.setSpacing(15);

        // adding elements to layout
        layout.getChildren().addAll(languageBox, startButton, eoButton, cdButton,
                invitationButton, odButton, addExpenseButton, statisticsButton);
        // Scene
        mainScene = new Scene(layout, 600, 480); // changed this line

        // Window
        primaryStage.setTitle("Main");
        primaryStage.setScene(mainScene); // changed this line
        primaryStage.show();
    }

    public void showStatistics() {
        primaryStage.setScene(statistics.getScene());
    }

    public void showAddExpense() {
        primaryStage.setScene(aed.getScene());
    }

    public void showOpenDebts() {
        primaryStage.setScene(od.getScene());
    }

    public void showInvitation() {
        primaryStage.setScene(inv.getScene());
    }

    public void showContactDetails() {
        primaryStage.setScene(cd.getScene());
    }

    public void showEventOverview() {
        primaryStage.setScene(eo.getScene());
    }

    public void showStartScreen() {
        primaryStage.setScene(sc.getScene());
    }

    /**
     * Getter for the primary stage
     * @return the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Getter for the main scene
     * @return the main scene
     */
    public Scene getMainScene() {
        return mainScene;
    }
}




