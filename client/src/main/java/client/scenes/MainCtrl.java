package client.scenes;

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
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import javafx.util.Pair;
//
//public class MainCtrl {
//
//    private Stage primaryStage;
//
//    private QuoteOverviewCtrl overviewCtrl;
//    private Scene overview;
//
//    private AddQuoteCtrl addCtrl;
//    private Scene add;
//
//    /**
//     * Initializes the main controller with the primary stage and scenes.
//     *
//     * @param primaryStage The primary stage of the application.
//     * @param overview     The overview scene.
//     * @param add          The add scene.
//     */
//    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
//                           Pair<AddQuoteCtrl, Parent> add) {
//        this.primaryStage = primaryStage;
//        this.overviewCtrl = overview.getKey();
//        this.overview = new Scene(overview.getValue());
//
//        this.addCtrl = add.getKey();
//        this.add = new Scene(add.getValue());
//
//        showOverview();
//        primaryStage.show();
//    }
//
//    /**
//     * Shows the overview scene.
//     */
//    public void showOverview() {
//        primaryStage.setTitle("Quotes: Overview");
//        primaryStage.setScene(overview);
//        overviewCtrl.refresh();
//    }
//
//    /**
//     * Shows the add scene.
//     */
//    public void showAdd() {
//        primaryStage.setTitle("Quotes: Adding Quote");
//        primaryStage.setScene(add);
//        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
//    }
//}

import client.utils.ServerUtils;
import commons.Event;
import javafx.stage.Stage;

public class MainCtrl {

    private final ServerUtils serverUtils;

    private final Stage primaryStage;

    private final OpenDebts openDebts;
    private final Invitations invitations;
    private final ContactDetails contactDetails;
    private final EventOverview eventOverview;
    private final StartScreen startScreen;
    private final AddEditExpense addEditExpense;
    private final Statistics statistics;
    private final DevScreen devScreen;


    /**
     * Constructor for the Main Controller of the application
     * @param primaryStage primary stage of the application
     */
    public MainCtrl(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.serverUtils = ServerUtils.getServerUtils();

        startScreen = new StartScreen(this, serverUtils);
        eventOverview = new EventOverview(this, serverUtils, null);
        invitations = new Invitations(this, serverUtils, null);
        contactDetails = new ContactDetails(this, serverUtils, null);
        addEditExpense = new AddEditExpense(this, serverUtils, null);
        openDebts = new OpenDebts(this, serverUtils, null);
        statistics = new Statistics(this, serverUtils, null);
        devScreen = new DevScreen(this, serverUtils);

        // default scene
        showDevScreen();
        primaryStage.show();
    }

    /**
     * Shows the Start scene
     */
    public void showStartScreen(){
        primaryStage.setScene(startScreen.initialize());
    }

    /**
     * Shows the Event Overview scene
     * @param event corresponding event instance
     */
    public void showEventOverview(Event event){
        primaryStage.setScene(eventOverview.initialize(event));
    }

    /**
     * Shows the Statistics scene
     * @param event corresponding event instance
     */
    public void showStatistics(Event event) {
        if (statistics.isOpen()){
            statistics.closeAlertBox();
        }
        statistics.initialize(event);
        statistics.displayAlertBox();
    }

    /**
     * Shows the Add/Edit Expense scene
     * @param event corresponding event instance
     */
    public void showAddExpense(Event event) {
        if (addEditExpense.isOpen()){
            addEditExpense.closeAlertBox();
        }
        addEditExpense.initialize(event);
        addEditExpense.displayAlertBox();
    }

    /**
     * Shows the open debts scene
     * @param event corresponding event instance
     */
    public void showOpenDebts(Event event) {
        if (openDebts.isOpen()){
            openDebts.closeAlertBox();
        }
        openDebts.initialize(event);
        openDebts.displayAlertBox();
    }

    /**
     * Shows the Invitation scene
     * @param event corresponding event instance
     */
    public void showInvitation(Event event) {
        if (invitations.isOpen()){
            invitations.closeAlertBox();
        }
        invitations.initialize(event);
        invitations.displayAlertBox();
    }

    /**
     * Shows the Contact Details scene
     * @param event corresponding event instance
     */
    public void showContactDetails(Event event) {
        if (contactDetails.isOpen()){
            contactDetails.closeAlertBox();
        }
        contactDetails.initialize(event);
        contactDetails.displayAlertBox();
    }

    /**
     * Shows the development scene
     */
    public void showDevScreen(){
        primaryStage.setScene(devScreen.initialize());
    }

    /**
     * Getter for primaryStage
     * @return reference to the primaryStage
     */
    public Stage getPrimaryStage(){
        return primaryStage;
    }
}
