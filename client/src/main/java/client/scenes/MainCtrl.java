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
import client.utils.ControllerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import commons.dto.ExpenseDTO;
import jakarta.ws.rs.ProcessingException;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.lang.reflect.InvocationTargetException;

public class MainCtrl {

    @Inject
    private ControllerUtils controllerUtils;
    @Inject
    private LanguageManager lm;

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
    private Stage adminPasswordPopup;
    private boolean passwordIsCorrect;

    private KeyCombination globalBackToStartScreen = new KeyCodeCombination(KeyCode.X, KeyCombination.ALT_DOWN);

    private Thread.UncaughtExceptionHandler defaultExceptionHandler;

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
     */
    public void initialize(Stage primaryStage,
                           Pair<StartScreenCtrl, Parent> startScreenPair,
                           Pair<EventOverviewCtrl, Parent> eventOverviewPair,
                           Pair<ContactDetailsCtrl, Parent> contactDetailsPair,
                           Pair<InvitationsCtrl, Parent> invitationsPair,
                           Pair<OpenDebtsCtrl, Parent> openDebtsPair,
                           Pair<AddEditExpenseCtrl, Parent> addEditExpensePair,
                           Pair<AdminCtrl, Parent> adminPair,
                           Pair<AdminPasswordCtrl, Parent> adminPasswordPair
    ) {
        this.primaryStage = primaryStage;

        this.startScreenCtrl = startScreenPair.getKey();
        this.startScreen = new Scene(startScreenPair.getValue());

        this.eventOverviewCtrl = eventOverviewPair.getKey();
        this.eventOverview = new Scene(eventOverviewPair.getValue());
        this.eventOverview.getAccelerators().put(globalBackToStartScreen, this::showStartScreen);

        this.contactDetailsCtrl = contactDetailsPair.getKey();
        this.contactDetails = new Scene(contactDetailsPair.getValue());
        this.contactDetails.getAccelerators().put(globalBackToStartScreen, this::showStartScreen);

        this.invitationsCtrl = invitationsPair.getKey();
        this.invitations = new Scene(invitationsPair.getValue());
        this.invitations.getAccelerators().put(globalBackToStartScreen, this::showStartScreen);

        this.openDebtsCtrl = openDebtsPair.getKey();
        this.openDebts = new Scene(openDebtsPair.getValue());
        this.openDebts.getAccelerators().put(globalBackToStartScreen, this::showStartScreen);

        this.addEditExpenseCtrl = addEditExpensePair.getKey();
        this.addEditExpense = new Scene(addEditExpensePair.getValue());
        this.addEditExpense.getAccelerators().put(globalBackToStartScreen, this::showStartScreen);

        this.adminCtrl = adminPair.getKey();
        this.admin = new Scene(adminPair.getValue());
        this.admin.getAccelerators().put(globalBackToStartScreen, this::showStartScreen);

        this.adminPasswordCtrl = adminPasswordPair.getKey();
        this.adminPassword = new Scene(adminPasswordPair.getValue());
        this.adminPasswordPopup = createAdminPasswordPopup();
        this.passwordIsCorrect = false;

        showStartScreen();
        primaryStage.show();

        Thread.setDefaultUncaughtExceptionHandler(this::exceptionHandler);
    }

    private void exceptionHandler(Thread thread, Throwable throwable) {
        if (throwable instanceof ProcessingException) showStartScreen();
        else if (throwable.getCause() instanceof InvocationTargetException invocationTargetException) {
            if (invocationTargetException.getTargetException() instanceof ProcessingException) {
                showStartScreen();
            }
            else {
                throwable.printStackTrace();
                Platform.exit();
                System.exit(1);
            }
        }
        else {
            throwable.printStackTrace();
            Platform.exit();
            System.exit(1);
        }
    }

    /**
     * Sets the title of the primary stage and switches to the Start Screen.
     */
    public void showStartScreen() {
        while (true) {
            try {
                String string = "Splitty: Start Screen";
                string = lm.get(string);
                primaryStage.setTitle(string);
                primaryStage.setScene(startScreen);
                startScreenCtrl.refresh();
                break;
            } catch (ProcessingException e) {
                boolean reconnect = controllerUtils.createServerUnavailableAlert(
                        lm.get("Server unavailable!"),
                        lm.get("The server is unavailable!\n") +
                                lm.get("Check if the server address is correct and try reconnecting."),
                        lm
                );
                if (!reconnect) {
                    Platform.exit();
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Sets the title of the primary stage and switches to the Event Overview screen for
     * the specified event.
     *
     * @param eventDTO The EventDTO representing the event to display.
     */
    public void showEventOverview(EventDTO eventDTO){
        primaryStage.setTitle(lm.get("Splitty: Event ") + eventDTO.name());
        primaryStage.setScene(eventOverview);
        eventOverviewCtrl.refresh(eventDTO);
    }

    /**
     * Sets the title of the primary stage and switches to the Contact Details screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to display contact details.
     */
    public void showContactDetails(EventDTO eventDTO) {
        primaryStage.setTitle(lm.get("Splitty: Add/Edit Participant"));
        primaryStage.setScene(contactDetails);
        contactDetailsCtrl.refresh(eventDTO);
    }

    /**
     * Sets the title of the primary stage and switches to the Invitations screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to manage invitations.
     */
    public void showInvitations(EventDTO eventDTO){
        String string = "Splitty: Send invitations to event ";
        string = lm.get(string);
        primaryStage.setTitle(string + eventDTO.name());
        primaryStage.setScene(invitations);
        invitationsCtrl.refresh(eventDTO);
    }

    /**
     * Sets the title of the primary stage and switches to the Open Debts screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to manage open debts.
     */
    public void showOpenDebts(EventDTO eventDTO){
        primaryStage.setTitle(lm.get("Splitty: Settle debts of event ") + eventDTO.name());
        primaryStage.setScene(openDebts);
        openDebtsCtrl.refresh(eventDTO);
    }

    /**
     * Sets the title of the primary stage and switches to the Add Expense screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to add expenses.
     */
    public void showAddExpense(EventDTO eventDTO){
        primaryStage.setTitle(lm.get("Splitty: Add expense for event ") + eventDTO.name());
        primaryStage.setScene(addEditExpense);
        addEditExpenseCtrl.refresh(eventDTO, null);
    }

    /**
     * Sets the title of the primary stage and switches to the Add Expense screen for the
     * specified event.
     *
     * @param eventDTO The EventDTO representing the event for which to edit expenses.
     * @param expense The expense to edit.
     */
    public void showEditExpense(EventDTO eventDTO, ExpenseDTO expense){
        primaryStage.setTitle(lm.get("Splitty: Edit expense for event ") + eventDTO.name());
        primaryStage.setScene(addEditExpense);
        addEditExpenseCtrl.refresh(eventDTO, expense);
    }


    /**
     * Sets the title of the primary stage and switches to the Administrator Control Panel.
     */
    public void showAdmin() {
        if(passwordIsCorrect) {
            primaryStage.setTitle(lm.get("Splitty: Administrator Control Panel"));
            primaryStage.setScene(admin);
            adminCtrl.refresh();
        }
        else openAdminPasswordPopup();
    }

    /**
     * Creates the popup where the Admin Password must be entered.
     * @return Popup with the Admin Password Scene.
     */
    private Stage createAdminPasswordPopup() {
        Stage popup = new Stage();
        popup.initOwner(primaryStage);
        popup.initModality(Modality.APPLICATION_MODAL);
        String string = "Administrator Control Panel";
        if(lm != null){
            string = lm.get(string);
        }
        popup.setTitle(string);
        popup.setResizable(false);  // popup can't be resized
        popup.setScene(adminPassword);
        return popup;
    }

    /**
     * Opens the popup where the Admin Password must be entered.
     */
    public void openAdminPasswordPopup() {
        adminPasswordCtrl.setLanguageForAllAdminPasswordCtrl();
        adminPasswordPopup.showAndWait();
    }

    /**
     * Closes the popup where the Admin Password must be entered.
     */
    public void closeAdminPasswordPopup() {
        adminPasswordPopup.close();
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
     * Sets whether the password has been entered correctly or not
     * @param passwordIsCorrect Passes either true or false, depending on if the password is correct.
     */
    public void setPasswordIsCorrect(boolean passwordIsCorrect) {
        this.passwordIsCorrect = passwordIsCorrect;
    }

    /**
     * Reloads all language settings across different components of the application.
     * This method updates the language settings for the start screen, admin password control panel,
     * and invitations control panel by invoking their respective setLanguage methods.
     * Additionally, it recreates the admin password popup window to ensure its language is updated.
     */
    public void reloadAllLanguages(){
        startScreenCtrl.setLanguageForAll();
        adminPasswordCtrl.setLanguageForAllAdminPasswordCtrl();
        invitationsCtrl.setLanguageForAllInvitationsCtrl();
        this.adminPasswordPopup = createAdminPasswordPopup();
        this.adminCtrl.setLanguageForAllAdminCtrl();
        openDebtsCtrl.setLanguageForAllOpenDebtsCtrl();
        addEditExpenseCtrl.setLanguageForAllAddEditExpenseCtrl();
        eventOverviewCtrl.setLanguageForAllEventOverviewCtrl();
        contactDetailsCtrl.setLanguageForAllContactDetailsCtrl();
    }
}
