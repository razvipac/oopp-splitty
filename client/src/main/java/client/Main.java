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

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.Invitations;
import client.scenes.StartScreen;
import com.google.inject.Injector;

import client.scenes.OpenDebts;
//import client.scenes.AddQuoteCtrl;
//import client.scenes.MainCtrl;
//import client.scenes.QuoteOverviewCtrl;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private final OpenDebts od = new OpenDebts();
    // add your page as a private object here
    private final Invitations inv = new Invitations();

    private final StartScreen sc = new StartScreen();

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        // Code that was already in this class
//        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
//        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
//
//        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
//        mainCtrl.initialize(primaryStage, overview, add);

        // Button for StartScreen page
        Button startButton = new Button("Start Screen");
        startButton.setOnAction(e -> primaryStage.setScene(sc.getScene()));
        // set the button's action to open your scene
        // startButton.setOnAction(e -> primaryStage.setScene(...));

        // Button for ContactDetails page
        Button cdButton = new Button("Contact Details");
        // set the button's action to open your scene
        // cdButton.setOnAction(e -> primaryStage.setScene(...));

        // Button for Invitation page
        Button invitationButton = new Button("Invitation");
        // set the button's action to open your scene
        invitationButton.setOnAction(e -> primaryStage.setScene(inv.getScene()));

        // Button for OpenDebts page
        Button odButton = new Button("Open Debts");
        odButton.setOnAction(e -> primaryStage.setScene(od.getScene()));

        // Layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        // adding elements to layout
        layout.getChildren().addAll(startButton, cdButton, invitationButton, odButton);

        // Scene
        Scene scene = new Scene(layout, 400, 300);

        // Window
        primaryStage.setTitle("Main");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}