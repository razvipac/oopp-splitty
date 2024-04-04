///*
// * Copyright 2021 Delft University of Technology
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package client.scenes;
//
//import client.LanguageButton;
//import client.LanguageManager;
//import client.utils.ServerUtils;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.VBox;
//
//public class DevScreen {
//
//    private final MainCtrl mainCtrl;
//    private final ServerUtils serverUtils;
//
//    private Scene scene;
//
//    /**
//     * Constructor for the AddEditExpense that calls the method to create the scene
//     * @param mainCtrl scene of the mainCtrl class
//     * @param serverUtils global serverUtils singleton
//     */
//    public DevScreen(MainCtrl mainCtrl, ServerUtils serverUtils){
//        this.mainCtrl = mainCtrl;
//        this.serverUtils = serverUtils;
//
//        initialize();
//    }
//
//    /**
//     * Generates a scene
//     * @return the newly generated scene
//     */
//    public Scene initialize() {
//        Parent layout = generateLayout();
//
//        scene = new Scene(layout, 600, 480); // changed this line
//
//        return scene;
//    }
//
//    private Parent generateLayout(){
//        // Button for language selection
//        Button languageButton = new LanguageButton("client/src/main/resources" +
//                "/userSettings/userPreferences.json");
//        LanguageManager lm = new LanguageManager("client/src/main/resources" +
//                "/userSettings/userPreferences.json");
//
//        // Button for StartScreen page
//        Button startButton = new Button(lm.get("StartScreen"));
//        startButton.setOnAction(e -> mainCtrl.showStartScreen());
//
//        // Button for EventOverview page
//        Button eoButton = new Button(lm.get("EventOverview"));
////        eoButton.setOnAction(e -> mainCtrl.showEventOverview(null));
//
//        // Button for ContactDetails page
//        Button cdButton = new Button(lm.get("ContactDetails"));
////        cdButton.setOnAction(e -> mainCtrl.showContactDetails(null));
//
//        // Button for Invitation page
//        Button invitationButton = new Button(lm.get("Invitation"));
////        invitationButton.setOnAction(e -> mainCtrl.showInvitation(null));
//
//        // Button for OpenDebts page
//        Button odButton = new Button(lm.get("OpenDebts"));
////        odButton.setOnAction(e -> mainCtrl.showOpenDebts(null));
//
//        Button addExpenseButton = new Button(lm.get("AddExpense"));
////        addExpenseButton.setOnAction(e -> mainCtrl.showAddExpense(null));
//
//        Button statisticsButton = new Button(lm.get("Statistics"));
////        statisticsButton.setOnAction(e -> mainCtrl.showStatistics(null));
//
//        Button adminButton = new Button(lm.get("Admin"));
////        adminButton.setOnAction(e -> mainCtrl.showAdminScreen());
//
//
//
//        // Hbox for the language button
//        HBox languageBox = new HBox(languageButton);
//        languageBox.setAlignment(Pos.TOP_RIGHT);
//        HBox.setHgrow(languageButton, Priority.ALWAYS);
//
//        // Layout
//        VBox layout = new VBox();
//        layout.setAlignment(Pos.CENTER);
//        layout.setPadding(new Insets(10));
//        layout.setSpacing(15);
//
//        // adding elements to layout
//        layout.getChildren().addAll(languageBox, startButton, eoButton, cdButton,
//                invitationButton, odButton, addExpenseButton, statisticsButton, adminButton);
//
//        return layout;
//    }
//}
//
//
//
//
