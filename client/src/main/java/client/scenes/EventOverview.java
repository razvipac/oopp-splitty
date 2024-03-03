package client.scenes;

import client.Main;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.ArrayList;

public class EventOverview {

    // JavaFX Scene
    private Scene scene;

    private Main main;

    // Java FX Fonts
    private final Font h1 = Font.font("Arial", FontWeight.BOLD , 20);
    private final Font h2 = Font.font("Arial", FontWeight.BOLD , 14);

    // TODO: use actual Objects Participant and Expense instead of ArrayList<String>
    // Arraylists containing participants and expenses for testing purposes
    private ArrayList<String> participants;
    private ArrayList<String> expenses;

    // Currently selected participant (whose expenses to view)
    private String selectedParticipant;
    
    // Currently selected expenses view (all, from or including <selectedParticipant>)
    public enum View {
        ALL, FROM, INCLUDING
    }
    View currentView;

    // Attributes of elements needed by several methods
    private ToggleGroup chooseView;
    private RadioButton allRadio;
    private RadioButton fromRadio;
    private RadioButton includingRadio;
    private VBox expensesContainer;

    /**
     * Creates Event Overview.
     * @param main to call the main scene
     */
    public EventOverview(Main main) {
        this.main = main;
        // Participants for testing purposes
        participants = new ArrayList<>();
        participants.add("Chris");
        participants.add("John");
        participants.add("Anna");
        participants.add("David");

        // Expenses for testing purposes
        expenses = new ArrayList<>();
        expenses.add("Chris");
        expenses.add("John");
        expenses.add("John");
        expenses.add("Anna");

        // Show the expenses of the first person in the list by default
        selectedParticipant = participants.getFirst();
        // Set current view of expenses to 'all' by default
        currentView = View.ALL;
        createScene();
    }

    /**
     * Creates the scene.
     */
    public void createScene() {
        // Main layout
        VBox layout = new VBox(5);
        layout.setPadding(new Insets(10));

        // eventBox, includes Event Name and Send Invite button
        HBox eventBox = getEventBox();
        // participantsBox, includes header and buttons to edit/add participant
        HBox participantsBox = getParticipantsBox();
        // Text listing the names of the participants
        Text participantNames = new Text(participantsToString());
        // Expenses header
        Text expensesHeader = new Text("Expenses");
        expensesHeader.setFont(h2);
        // 'Add Expense' button
        // TODO: button is non-functional
        Button expenseAddButton = new Button("Add Expense");
        // radioSelectBox, includes radio button selection for currentView
        HBox radioSelectBox = getRadioSelectBox();
        // participantDropdown, ComboBox that sets selectedParticipant
        ComboBox<String> participantDropdown = getParticipantDropdown();
        // expensesScroller, which includes all expense items in arraylist expenses
        ScrollPane expensesScroller = getExpensesScroller();
        // 'Settle Debts' button
        // TODO: button is non-functional
        Button settleDebtsButton = new Button("Settle Debts");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> main.getPrimaryStage().setScene(main.getMainScene()));

        // add all elements to layout
        layout.getChildren().addAll(eventBox, participantsBox, participantNames,
                expensesHeader, expenseAddButton,
                participantDropdown, radioSelectBox, expensesScroller,
                settleDebtsButton, backButton);

        // Scene
        scene = new Scene(layout, 350, 380);
    }

    /**
     * Gets HBox eventBox, which includes the Event Name and Send Invite button.
     * @return HBox eventBox
     */
    private HBox getEventBox() {
        HBox eventBox = new HBox(5);

        // TODO: Event name is hardcoded
        // Event Name
        Text eventName = new Text("Event Name");
        eventName.setFont(h1);
        // Send Invite button
        // TODO: Button is non-functional
        Button sendInviteButton = new Button("Send Invite");

        // Add to eventBox
        eventBox.getChildren().addAll(eventName, sendInviteButton);

        return eventBox;
    }

    /**
     * Gets HBox participantsBox, which includes a 'Participants' header,
     * and buttons to Edit and Add participants.
     * @return HBox participantsBox
     */
    private HBox getParticipantsBox() {
        HBox participantsBox = new HBox(5);

        // Participants header
        Text participantsHeader = new Text("Participants");
        participantsHeader.setFont(h2);
        // Edit Participant button
        // TODO: Button is non-functional
        Button participantEditButton = new Button("Edit");
        // Add Participant button
        // TODO: Button is non-functional
        Button participantAddButton = new Button("Add");

        // Add to participantBox
        participantsBox.getChildren().addAll(participantsHeader,
                participantEditButton, participantAddButton);

        return participantsBox;
    }

    /**
     * Returns a String representation of the participants arraylist,
     * in the form: firstname_1, ..., firstname_n.
     * @return String representation of participants
     */
    public String participantsToString() {
        StringBuilder sb = new StringBuilder();

        for(String person : participants) {
            sb.append(person).append(", ");
        }

        // remove trailing comma and space
        sb.setLength(sb.length() - 2);

        return sb.toString();
    }

    /**
     * Gets HBox radioSelectBox, which includes a ToggleGroup with the following three radio buttons:
     * 1) All expenses
     * 2) Expenses from [selectedParticipant]
     * 3) Expenses including [selectedParticipant]
     * @return HBox radioSelectBox
     */
    private HBox getRadioSelectBox() {
        HBox radioSelectBox = new HBox(5);

        // Toggle Group chooseView with three radio buttons
        chooseView = new ToggleGroup();
        // 'All expenses' radio button
        allRadio = new RadioButton("All");
        allRadio.setToggleGroup(chooseView);
        allRadio.setSelected(true);
        // 'Expenses from <selectedParticipant>' radio button
        fromRadio = new RadioButton("From " + selectedParticipant);
        fromRadio.setToggleGroup(chooseView);
        // 'Expenses including <selectedParticipant>' radio button
        includingRadio = new RadioButton("Including " + selectedParticipant);
        includingRadio.setToggleGroup(chooseView);

        // Add to radioSelectBox
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
        participantDropdown.getItems().addAll(participants);
        participantDropdown.getSelectionModel().selectFirst();

        // Listener for participantDropdown, sets selectedParticipant and updates radio button text
        participantDropdown.getSelectionModel().selectedItemProperty().addListener(
                (v, oldValue, newValue) -> {
                    selectedParticipant = newValue;
                    // update text of radio buttons 'from' and 'including'
                    fromRadio.setText("From " + selectedParticipant);
                    includingRadio.setText("Including " + selectedParticipant);
                    expenseItemVisibility(expensesContainer);
                }
        );

        return participantDropdown;
    }

    /**
     * Gets expensesScroller, which includes a VBox container for all ExpenseItem objects.
     * @return ScrollPane expensesScroller
     */
    private ScrollPane getExpensesScroller() {
        // The scrollable pane
        ScrollPane expensesScroller = new ScrollPane();
        expensesScroller.setPrefHeight(140);

        // The VBox that actually contains all ExpenseItem objects
        expensesContainer = new VBox(5);
        // for every expense, add ExpenseItem to expensesContainer
        for(String expense : expenses) {
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
                    if (((ExpenseItem) item).expense.equals(selectedParticipant)) {
                        item.setVisible(true);
                        item.setManaged(true);
                    } else {
                        item.setVisible(false);
                        item.setManaged(false);
                    }
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

        // the participant tied to the Expense
        public String expense;

        /**
         * Creates ExpenseItem, a GridPane containing an Expense's date, participant,
         * and an 'Edit' button.
         * @param expense the participant tied to the Expense
         */
        public ExpenseItem(String expense) {
            this.expense = expense;

            this.setHgap(12);
            this.setVgap(2);

            // TODO: implement this for actual Expense values
            // Date of expense, hardcoded
            Text date = new Text("01-01-2024");
            this.add(date, 0, 0, 1, 2);
            // Participant tied to expense
            Text expenseInfo = new Text(expense + " paid 99 Euro for Item");
            this.add(expenseInfo, 1, 0);
            // Expense includes ..., hardcoded to 'all'
            Text expenseIncludes = new Text("(all)");
            this.add(expenseIncludes, 1, 1);
            // Edit Expense button
            Button expenseEditButton = new Button("Edit");
            this.add(expenseEditButton, 2, 0, 1, 2);
        }
    }
}
