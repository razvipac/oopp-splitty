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
package client.scenes;

import client.LanguageManager;
import commons.dto.EventDTO;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private StartScreenCtrl startScreenCtrl;
    private Scene startScreen;

    private EventOverviewCtrl eventOverviewCtrl;
    private Scene eventOverview;

    private ContactDetailsCtrl contactDetailsCtrl;
    private Scene contactDetails;

    private InvitationsCtrl invitationsCtrl;
    private Scene invitations;

    private OpenDebtsCtrl openDebtsCtrl;
    private Scene openDebts;

    private AddEditExpenseCtrl addEditExpenseCtrl;
    private Scene addEditExpense;

    private AdminCtrl adminCtrl;
    private Scene admin;

    private AdminPasswordCtrl adminPasswordCtrl;
    private Scene adminPassword;

    private LanguageManager languageManager;

    /**
     * Initializes the MainCtrl with the primary stage and scenes for various controllers.
     *
     * @param primaryStage primary stage
     * @param startScreenPair the startScreenPair from MyFXML output
     * @param eventOverviewPair the eventOverviewPair from MyFXML output
     * @param contactDetailsPair the contactDetailsPair from MyFXML output
     * @param invitationsPair the invitationsPair from MyFXML output
     * @param openDebtsPair the openDebtsPair from MyFXML output
     * @param addEditExpensePair the addEditExpensePair from MyFXML output
     * @param adminPair the adminPair from MyFXML output
     * @param adminPasswordPair the adminPasswordPair from MyFXML output
     * @param languageManager the languageManager of the whole app
     */
    public void initialize(Stage primaryStage,
                           Pair<StartScreenCtrl, Parent> startScreenPair,
                           Pair<EventOverviewCtrl, Parent> eventOverviewPair,
                           Pair<ContactDetailsCtrl, Parent> contactDetailsPair,
                           Pair<InvitationsCtrl, Parent> invitationsPair,
                           Pair<OpenDebtsCtrl, Parent> openDebtsPair,
                           Pair<AddEditExpenseCtrl, Parent> addEditExpensePair,
                           Pair<AdminCtrl, Parent> adminPair,
                           Pair<AdminPasswordCtrl, Parent> adminPasswordPair,
                           LanguageManager languageManager
    ) {
        this.primaryStage = primaryStage;

        this.startScreenCtrl = startScreenPair.getKey();
        this.startScreen = new Scene(startScreenPair.getValue());

        this.eventOverviewCtrl = eventOverviewPair.getKey();
        this.eventOverview = new Scene(eventOverviewPair.getValue());

        this.contactDetailsCtrl = contactDetailsPair.getKey();
        this.contactDetails = new Scene(contactDetailsPair.getValue());

        this.invitationsCtrl = invitationsPair.getKey();
        this.invitations = new Scene(invitationsPair.getValue());

        this.openDebtsCtrl = openDebtsPair.getKey();
        this.openDebts = new Scene(openDebtsPair.getValue());

        this.addEditExpenseCtrl = addEditExpensePair.getKey();
        this.addEditExpense = new Scene(addEditExpensePair.getValue());

        this.adminCtrl = adminPair.getKey();
        this.admin = new Scene(adminPair.getValue());

        this.adminPasswordCtrl = adminPasswordPair.getKey();
        this.adminPassword = new Scene(adminPasswordPair.getValue());

        this.languageManager = languageManager;

        showStartScreen();
        primaryStage.show();
    }

    /**
     * Sets the title of the primary stage and switches to the Start Screen.
     */
    public void showStartScreen() {
        String string = "Splitty: Start Screen";
        string = languageManager.get("Splitty: Start Screen");
        primaryStage.setTitle(string);
        startScreenCtrl.initialize();
        primaryStage.setScene(startScreen);
    }

    /**
     * Sets the title of the primary stage and switches to the Event Overview screen for
     * the specified event.
     *
     * @param eventDTO The EventDTO representing the event to display.
     */
    public void showEventOverview(EventDTO eventDTO){
        primaryStage.setTitle("Splitty: Event " + eventDTO.name());
        eventOverviewCtrl.initialize(eventDTO);
        primaryStage.setScene(eventOverview);
    }

    /**
     * Sets the title of the primary stage and switches to the Contact Details screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to display contact details.
     */
    public void showContactDetails(EventDTO eventDTO) {
        primaryStage.setTitle("Splitty: Add/Edit Participant");
        contactDetailsCtrl.initialize(eventDTO);
        primaryStage.setScene(contactDetails);
    }

    /**
     * Sets the title of the primary stage and switches to the Invitations screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to manage invitations.
     */
    public void showInvitations(EventDTO eventDTO){
        primaryStage.setTitle("Splitty: Send invitations to event " + eventDTO.name());
        invitationsCtrl.initialize(eventDTO);
        primaryStage.setScene(invitations);
    }

    /**
     * Sets the title of the primary stage and switches to the Open Debts screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to manage open debts.
     */
    public void showOpenDebts(EventDTO eventDTO){
        primaryStage.setTitle("Splitty: Settle debts of event " + eventDTO.name());
        openDebtsCtrl.initialize(eventDTO);
        primaryStage.setScene(openDebts);
    }

    /**
     * Sets the title of the primary stage and switches to the Add/Edit Expense screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to add or edit expenses.
     */
    public void showAddEditExpense(EventDTO eventDTO){
        primaryStage.setTitle("Splitty: Add/edit expenses for event " + eventDTO.name());
        addEditExpenseCtrl.initialize(eventDTO);
        primaryStage.setScene(addEditExpense);
    }


    /**
     * Sets the title of the primary stage and switches to the Administrator Control Panel.
     */
    public void showAdmin() {
        primaryStage.setTitle("Splitty: Administrator Control Panel");
        adminCtrl.initialize();
        primaryStage.setScene(admin);
    }

    /**
     * Retrieves the primary stage associated with this MainCtrl instance.
     *
     * @return The primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Retrieves the languageManager associated with this MainCtrl Instance
     * @return the language manager
     */
    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }
}
