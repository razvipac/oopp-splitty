package client.scenes;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.ArrayList;

public class EventOverview {

    private Scene scene;
    private final Font h1 = Font.font("Arial", FontWeight.BOLD , 20);
    private final Font h2 = Font.font("Arial", FontWeight.BOLD , 14);

    private ArrayList<String> participants;
    private ArrayList<String> expenses;
    private String selectedParticipant;

    public enum View {
        ALL, FROM, INCLUDING
    }
    View currentView;

    /**
     * Constructor for Event Overview page
     */
    public EventOverview() {
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

        // show the expenses of the first person in the list by default
        selectedParticipant = participants.getFirst();
        // set current view of expenses to 'all' by default
        currentView = View.ALL;
        createScene();
    }

    /**
     * Creates the scene
     */
    public void createScene() {
        // Layout
        VBox layout = new VBox(5);
        layout.setPadding(new Insets(10));

        HBox eventBox = getEventBox();
        HBox participantsBox = getParticipantsBox();

        // Names of the participants (hardcoded)
        Text participantNames = new Text(getParticipantsAsString());

        // Expenses text
        Text expensesText = new Text("Expenses");
        expensesText.setFont(h2);

        // Add expense button
        Button expenseAddButton = new Button("Add Expense");


        HBox viewExpenseBox = new HBox(5);
        // Choose radio button
        ToggleGroup chooseView = new ToggleGroup();
        RadioButton allRadio = new RadioButton("All");
        allRadio.setToggleGroup(chooseView);
        allRadio.setSelected(true);
        RadioButton fromRadio = new RadioButton("From " + selectedParticipant);
        fromRadio.setToggleGroup(chooseView);
        RadioButton includingRadio = new RadioButton("Including " + selectedParticipant);
        includingRadio.setToggleGroup(chooseView);
        viewExpenseBox.getChildren().addAll(allRadio, fromRadio, includingRadio);

        // Dropdown comboBox with participants to select from
        ComboBox<String> participantSelect = new ComboBox<>();
        participantSelect.getItems().addAll(participants);
        //listener for participantSelect
        participantSelect.getSelectionModel().selectedItemProperty().addListener(
                (v, oldValue, newValue) -> {
                    selectedParticipant = newValue;
                    // update text of radio buttons 'from' and 'including'
                    fromRadio.setText("From " + selectedParticipant);
                    includingRadio.setText("Including " + selectedParticipant);
                }
        );

        ScrollPane expensesScroller = new ScrollPane();
        expensesScroller.setPrefHeight(140);
        VBox expensesContainer = new VBox(5);

        for(String e : expenses) {
            ExpenseItem item = new ExpenseItem(e);
            expensesContainer.getChildren().add(item);
        }
        expensesScroller.setContent(expensesContainer);

        chooseView.selectedToggleProperty().addListener(
                (v, oldValue, newValue) -> {
                    if(allRadio.isSelected()) currentView = View.ALL;
                    else if(fromRadio.isSelected()) currentView = View.FROM;
                    else if(includingRadio.isSelected()) currentView = View.INCLUDING;

                    for(Node item : expensesContainer.getChildren()) {
                        switch (currentView) {
                            case ALL -> {
                                item.setVisible(true);
                                item.setManaged(true);
                            }
                            case FROM, INCLUDING -> {
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
        );

        layout.getChildren().addAll(eventBox, participantsBox, participantNames,
                expensesText, expenseAddButton, participantSelect,
                viewExpenseBox, expensesScroller);

        // Scene
        scene = new Scene(layout, 350, 400);
    }

    private HBox getEventBox() {
        // HBox eventBox with Event Name and Send Invite button
        HBox eventBox = new HBox(5);
        // Event Name (hardcoded)
        Text eventName = new Text("Event Name");
        eventName.setFont(h1);
        // Send Invite button
        Button sendInviteButton = new Button("Send Invite");
        // Add to eventBox
        eventBox.getChildren().addAll(eventName, sendInviteButton);
        return eventBox;
    }

    private HBox getParticipantsBox() {
        // HBox participantsBox with Participants text, Edit and Add Participants button
        HBox participantsBox = new HBox(5);
        // Participants text
        Text participantsText = new Text("Participants");
        participantsText.setFont(h2);
        // Edit Participant button
        Button participantEditButton = new Button("Edit");
        // Add Participant button
        Button participantAddButton = new Button("Add");
        // Add to participantBox
        participantsBox.getChildren().addAll(participantsText, participantEditButton, participantAddButton);
        return participantsBox;
    }

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    public String getParticipantsAsString() {
        StringBuilder sb = new StringBuilder();
        for(String person : participants) {
            sb.append(person).append(", ");
        }
        // remove trailing comma and space
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public static class ExpenseItem extends GridPane {
        public String expense;
        public ExpenseItem(String expense) {
            this.expense = expense;

            this.setHgap(12);
            this.setVgap(2);

            // hardcoded date
            Text date = new Text("01-01-2024");
            this.add(date, 0, 0, 1, 2);
            // hardcoded info string
            Text expenseInfo = new Text(expense + " paid 99 Euro for Item");
            this.add(expenseInfo, 1, 0);
            Text expenseFor = new Text("(all)");
            this.add(expenseFor, 1, 1);
            // edit expense button
            Button expenseEditButton = new Button("Edit");
            this.add(expenseEditButton, 2, 0, 1, 2);
        }
    }
}
