package client.scenes;

import client.Main;
import commons.Participant;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import commons.Event;
import client.utils.EventUtils;

public class EventOverview {

    private EventUtils server = new EventUtils();
    private Scene scene;
    private Main main;

    // Java FX Fonts
    private final Font h1 = Font.font("Arial", FontWeight.BOLD , 20);
    private final Font h2 = Font.font("Arial", FontWeight.BOLD , 14);

    // Event attributes
    private String eventName;
    // TODO: use actual Objects Participant and Expense instead of ArrayList<String>
    private List<Participant> participants;
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
    public EventOverview(Main main, Event event) {
        this.main = main;

        if(event == null) {
            VBox layout = new VBox(5);
            Text noEventText = new Text("No event found");
            layout.getChildren().add(noEventText);
            scene = new Scene(layout, 350, 380);
            return;
        }

        eventName = event.getName();
        // Participants for testing purposes
        participants = server.getParticipants(event.getCode());
        participants.add(new Participant("test", event, "test", "test", "test"));

        // Expenses for testing purposes
        expenses = new ArrayList<>();
        expenses.add("Chris");
        expenses.add("John");
        expenses.add("John");
        expenses.add("Anna");

        // Show the expenses of the first person in the list by default
        selectedParticipant = participants.getFirst().getName();
        // Set current view of expenses to 'all' by default
        currentView = View.ALL;
        createScene();
    }

    /**
     * Creates the scene.
     */
    public void createScene() {
        VBox layout = new VBox(5);
        layout.setPadding(new Insets(10));

        // eventBox, includes Event Name and Send Invite button
        HBox eventBox = getEventBox();

        // participantsBox, includes header and buttons to edit/add participant
        HBox participantsBox = getParticipantsBox();
        Text participantNames = new Text(participantsToString());
        Text expensesHeader = new Text("Expenses");
        expensesHeader.setFont(h2);
        // TODO: add expense button is non-functional
        Button expenseAddButton = new Button("Add Expense");

        HBox radioSelectBox = getRadioSelectBox();
        ComboBox<String> participantDropdown = getParticipantDropdown();

        // expensesScroller, which includes all expense items in arraylist expenses
        ScrollPane expensesScroller = getExpensesScroller();
        // TODO: button is non-functional
        Button settleDebtsButton = new Button("Settle Debts");

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
        fromRadio = new RadioButton("From " + selectedParticipant);
        fromRadio.setToggleGroup(chooseView);
        // 'Expenses including <selectedParticipant>' radio button
        includingRadio = new RadioButton("Including " + selectedParticipant);
        includingRadio.setToggleGroup(chooseView);

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
                    ExpenseItem e = (ExpenseItem) item;
                    boolean isMatchingParticipant = e.expense.equals(selectedParticipant);
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
            Text date = new Text("01-01-2024");
            this.add(date, 0, 0, 1, 2);

            Text expenseInfo = new Text(expense + " paid 99 Euro for Item");
            this.add(expenseInfo, 1, 0);

            // TODO: 'expense includes ...' is currently hardcoded to 'all'
            Text expenseIncludes = new Text("(all)");
            this.add(expenseIncludes, 1, 1);

            Button expenseEditButton = new Button("Edit");
            this.add(expenseEditButton, 2, 0, 1, 2);
        }

    }
}
