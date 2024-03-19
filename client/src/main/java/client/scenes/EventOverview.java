package client.scenes;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.ArrayList;
import java.util.List;

import client.utils.ServerUtils;
import client.Main;
import commons.Expense;
import commons.Participant;
import commons.Event;

public class EventOverview {

    private Scene scene;
    private Main main;
    private ContactDetails contactDetails;
    private Invitations invitations;
    private AddEditExpense addEditExpense;
    private OpenDebts openDebts;

    private final ServerUtils server = ServerUtils.getServerUtils();

    // Java FX Fonts
    private final Font h1 = Font.font("Arial", FontWeight.BOLD , 20);
    private final Font h2 = Font.font("Arial", FontWeight.BOLD , 14);

    // Event attributes
    private Event event;
    private String eventName;
    private String eventCode;
    // TODO: use actual Objects Participant and Expense instead of ArrayList<String>
    private List<Participant> participants;
    private List<Expense> expenses;

    // Currently selected participant (whose expenses to view)
    private Participant selectedParticipant;
    
    // Currently selected expenses view (all, from or including <selectedParticipant>)
    public enum View {
        ALL, FROM, INCLUDING
    }
    private View currentView;

    // Attributes of elements needed by several methods
    private ToggleGroup chooseView;
    private RadioButton allRadio;
    private RadioButton fromRadio;
    private RadioButton includingRadio;
    private VBox expensesContainer;

    /**
     * Creates an Event Overview.
     * @param main to call the main scene
     * @param event the event to show
     */
    public EventOverview(Main main, Event event) {
        this.main = main;
        this.event = event;

        if(event == null) {
            createSceneNoEvent();
            return;
        }

        contactDetails = new ContactDetails(main, event);
        invitations = new Invitations(main, event);
        addEditExpense = new AddEditExpense(main);
        openDebts = new OpenDebts(main);

        eventName = event.getName();
        eventCode = event.getCode();

        // Participants for testing purposes
        participants = server.getParticipantUtils().getParticipants(event.getCode());
        Participant test = new Participant("test", event, "test", "test", "test");
        participants.add(test);

        // Expenses for testing purposes
        expenses = new ArrayList<>();
        expenses.add(new Expense(10, "Drinks", test));

        // If participants isn't empty, select the first participant by default
        if(!(participants.isEmpty())) {
            selectedParticipant = participants.getFirst();
        }

        // Set current view of expenses to 'all' by default
        currentView = View.ALL;
        createScene();
    }

    /**
     * Creates the scene for if there is no event found
     */
    private void createSceneNoEvent() {
        VBox layout = new VBox(5);
        Text noEventText = new Text("No event found");
        layout.getChildren().add(noEventText);
        scene = new Scene(layout, 350, 380);
    }

    /**
     * Creates the scene.
     */
    private void createScene() {
        VBox layout = new VBox(5);
        layout.setPadding(new Insets(10));

        // eventBox, includes Event Name and Send Invite button
        HBox eventBox = getEventBox();

        // participantsBox, includes header and buttons to edit/add participant
        HBox participantsBox = getParticipantsBox();
        Text participantNames = new Text(participantsToString());
        Text expensesHeader = new Text("Expenses");
        expensesHeader.setFont(h2);

        Button expenseAddButton = new Button("Add Expense");
        expenseAddButton.setOnAction(e -> {
            addEditExpense.displayAlertBox();
        });

        HBox radioSelectBox = getRadioSelectBox();
        ComboBox<String> participantDropdown = getParticipantDropdown();

        // expensesScroller, which includes all expense items in arraylist expenses
        ScrollPane expensesScroller = getExpensesScroller();
        // TODO: button is non-functional
        Button settleDebtsButton = new Button("Settle Debts");
        settleDebtsButton.setOnAction(e -> {
            openDebts.displayAlertBox();
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> main.getPrimaryStage().setScene(main.getMainScene()));

        layout.getChildren().addAll(eventBox, participantsBox, participantNames,
                expensesHeader, expenseAddButton,
                participantDropdown, radioSelectBox, expensesScroller,
                settleDebtsButton, backButton);

        scene = new Scene(layout, 350, 380);
    }

    /**
     * Gets HBox eventBox, which includes the Event Name and Send Invite button.
     * @return HBox eventBox
     */
    private HBox getEventBox() {
        HBox eventBox = new HBox(5);

        // TODO: Event name is hardcoded
        Text eventNameText = new Text(eventName);
        eventNameText.setFont(h1);

        // TODO: Button is non-functional
        Button sendInviteButton = new Button("Send Invite");
        sendInviteButton.setOnAction(e -> {
            invitations.displayAlertBox();
        });

        eventBox.getChildren().addAll(eventNameText, sendInviteButton);

        return eventBox;
    }

    /**
     * Gets HBox participantsBox, which includes a 'Participants' header,
     * and buttons to Edit and Add participants.
     * @return HBox participantsBox
     */
    private HBox getParticipantsBox() {
        HBox participantsBox = new HBox(5);

        Text participantsHeader = new Text("Participants");
        participantsHeader.setFont(h2);

        // TODO: Button is non-functional
        Button participantEditButton = new Button("Edit");

        // TODO: Button is non-functional
        Button participantAddButton = new Button("Add");
        participantAddButton.setOnAction(e -> {
            contactDetails.displayAlertBox();
        });

        participantsBox.getChildren().addAll(participantsHeader,
                participantEditButton, participantAddButton);

        return participantsBox;
    }

    /**
     * Returns a String representation of the participants arraylist,
     * in the form: firstname_1, ..., firstname_n.
     * @return String representation of participants
     */
    private String participantsToString() {
        if(participants.isEmpty()) return "(No participants in event)";

        StringBuilder sb = new StringBuilder();

        for(Participant person : participants) {
            sb.append(person.getName()).append(", ");
        }

        // remove trailing comma and space
        sb.setLength(sb.length() - 2);

        return sb.toString();
    }

    /**
     * Gets HBox radioSelectBox, which includes a ToggleGroup with the following radio buttons:
     * 1) All expenses
     * 2) Expenses from [selectedParticipant]
     * 3) Expenses including [selectedParticipant]
     * @return HBox radioSelectBox
     */
    private HBox getRadioSelectBox() {
        HBox radioSelectBox = new HBox(5);

        chooseView = new ToggleGroup();
        // 'All expenses' radio button
        allRadio = new RadioButton("All");
        allRadio.setToggleGroup(chooseView);
        allRadio.setSelected(true);
        // 'Expenses from <selectedParticipant>' radio button
        fromRadio = new RadioButton();
        fromRadio.setToggleGroup(chooseView);
        // 'Expenses including <selectedParticipant>' radio button
        includingRadio = new RadioButton();
        includingRadio.setToggleGroup(chooseView);

        setRadioButton();

        radioSelectBox.getChildren().addAll(allRadio, fromRadio, includingRadio);

        return radioSelectBox;
    }

    /**
     * Gets participantDropDown, a ComboBox containing all the participants of the event.
     * The selected option is saved to selectedParticipant.
     * @return ComboBox participantDropDown
     */
    private ComboBox<String> getParticipantDropdown() {
        // Dropdown comboBox with all participants of event to select from
        ComboBox<String> participantDropdown = new ComboBox<>();
        participantDropdown.getItems().addAll(
                participants
                        .stream()
                        .map(Participant::getName)
                        .toList()
        );
        participantDropdown.getSelectionModel().selectFirst();

        // Listener for participantDropdown, sets selectedParticipant and updates radio button text
        participantDropdown.getSelectionModel().selectedIndexProperty().addListener(
                (v, oldValue, newValue) -> {
                    selectedParticipant = participants.get((Integer) newValue);
                    setRadioButton();
                    expenseItemVisibility(expensesContainer);
                }
        );

        return participantDropdown;
    }

    /**
     * Sets the text of the radio buttons to match selectedParticipant, and disables
     * them if there are no expenses
     */
    private void setRadioButton() {
        if(expenses.isEmpty()) {
            allRadio.setDisable(true);
            fromRadio.setDisable(true);
            includingRadio.setDisable(true);
        }

        String name;
        if(selectedParticipant == null) {
            name = "(participant)";
        }
        else name = selectedParticipant.getName();

        fromRadio.setText("From " + name);
        includingRadio.setText("Including " + name);
    }

    /**
     * Gets expensesScroller, which includes a VBox container for all ExpenseItem objects.
     * @return ScrollPane expensesScroller
     */
    private ScrollPane getExpensesScroller() {
        ScrollPane expensesScroller = new ScrollPane();
        expensesScroller.setPrefHeight(140);

        // The VBox that actually contains all ExpenseItem objects
        expensesContainer = new VBox(5);
        // for every expense, add ExpenseItem to expensesContainer
        for(Expense expense : expenses) {
            ExpenseItem item = new ExpenseItem(expense);
            expensesContainer.getChildren().add(item);
        }

        // Set the content of the scrollable pane to the container
        expensesScroller.setContent(expensesContainer);

        // Show/Hide expense items based on currentView
        chooseView.selectedToggleProperty().addListener(
                (v, oldValue, newValue) -> expenseItemVisibility(expensesContainer)
        );

        return expensesScroller;
    }

    /**
     * Sets the visibility of the given ExpenseItem according to currentView.*
     * If currentView == ALL, then set the ExpenseItem to be visible and managed.
     * If currentView == FROM or currentView == INCLUDING, then set node to be visible and
     * managed iff selectedParticipant equals the participant tied to the ExpenseItem.
     * @param expensesContainer VBox expensesContainer, containing the ExpenseItems
     */
    private void expenseItemVisibility(VBox expensesContainer) {
        // set currentView to the selected radio button
        if(allRadio.isSelected()) currentView = View.ALL;
        else if(fromRadio.isSelected()) currentView = View.FROM;
        else if(includingRadio.isSelected()) currentView = View.INCLUDING;

        // set visibility according to currentView
        for(Node item : expensesContainer.getChildren()) {
            // TODO: properly implement the 'INCLUDING' view (currently does the same as 'ALL')
            switch (currentView) {
                case ALL, INCLUDING -> {
                    item.setVisible(true);
                    item.setManaged(true);
                }
                case FROM -> {
                    ExpenseItem e = (ExpenseItem) item;
                    boolean isMatchingParticipant = e.paidBy.equals(selectedParticipant);
                    item.setVisible(isMatchingParticipant);
                    item.setManaged(isMatchingParticipant);
                }
            }
        }
    }

    /**
     * Getter for the scene.
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Nested class Expense Item.
     */
    public static class ExpenseItem extends GridPane {

        private Expense expense;
        private int price;
        private String item;
        private Participant paidBy;
        private String paidByName;

        /**
         * Creates ExpenseItem, a GridPane containing an Expense's date, participant,
         * and an 'Edit' button.
         * @param expense the expense to create an item for
         */
        public ExpenseItem(Expense expense) {
            this.expense = expense;
            price = expense.getPrice();
            item = expense.getItem();
            paidBy = expense.getPaidBy();
            paidByName = paidBy.getName();

            createItemBox();
        }

        private void createItemBox() {
            this.setHgap(12);
            this.setVgap(2);

            // TODO: implement this for actual Expense date
            Text date = new Text("01-01-2024");
            this.add(date, 0, 0, 1, 2);

            Text expenseInfo = new Text(paidByName + " paid " + price + " Euro for " + item);
            this.add(expenseInfo, 1, 0);

            // TODO: 'paidBy includes ...' is currently hardcoded to 'all'
            Text expenseIncludes = new Text("(all)");
            this.add(expenseIncludes, 1, 1);

            Button expenseEditButton = new Button("Edit");
            this.add(expenseEditButton, 2, 0, 1, 2);
        }

    }
}
