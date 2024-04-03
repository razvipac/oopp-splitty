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

    public void initialize(Stage primaryStage,
                           Pair<StartScreenCtrl, Parent> startScreenPair,
                           Pair<EventOverviewCtrl, Parent> eventOverviewPair,
                           Pair<ContactDetailsCtrl, Parent> contactDetailsPair,
                           Pair<InvitationsCtrl, Parent> invitationsPair,
                           Pair<OpenDebtsCtrl, Parent> openDebtsPair,
                           Pair<AddEditExpenseCtrl, Parent> addEditExpensePair,
                           Pair<AdminCtrl, Parent> adminPair
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

        showAdmin();
        primaryStage.show();
    }

    public void showStartScreen() {
        primaryStage.setTitle("Splitty: Start Screen");
        startScreenCtrl.initialize();
        primaryStage.setScene(startScreen);
    }

    public void showEventOverview(EventDTO eventDTO){
        primaryStage.setTitle("Splitty: Event " + eventDTO.getName());
        eventOverviewCtrl.initialize(eventDTO);
        primaryStage.setScene(eventOverview);
    }

    public void showContactDetails(EventDTO eventDTO) {
        primaryStage.setTitle("Splitty: Add/Edit Participant");
        contactDetailsCtrl.initialize(eventDTO);
        primaryStage.setScene(contactDetails);
    }

    public void showInvitations(EventDTO eventDTO){
        primaryStage.setTitle("Splitty: Send invitations to event " + eventDTO.getName());
        invitationsCtrl.initialize(eventDTO);
        primaryStage.setScene(invitations);
    }

    public void showOpenDebts(EventDTO eventDTO){
        primaryStage.setTitle("Splitty: Settle debts of event " + eventDTO.getName());
        openDebtsCtrl.initialize(eventDTO);
        primaryStage.setScene(openDebts);
    }

    public void showAddEditExpense(EventDTO eventDTO){
        primaryStage.setTitle("Splitty: Add/edit expenses for event " + eventDTO.getName());
        addEditExpenseCtrl.initialize(eventDTO);
        primaryStage.setScene(addEditExpense);
    }

    public void showAdmin() {
        primaryStage.setTitle("Splitty: Administrator Control Panel");
        adminCtrl.initialize();
        primaryStage.setScene(admin);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
