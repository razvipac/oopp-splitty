package client.scenes;

import javafx.geometry.Insets;
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
    private String selectedParticipant;

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
        // show the expenses of the first person in the list by default
        selectedParticipant = participants.getFirst();

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

        // Dropdown comboBox with participants to select from
        ComboBox<String> participantSelect = new ComboBox<>();
        participantSelect.getItems().addAll(participants);
        //listener for participantSelect
        participantSelect.getSelectionModel().selectedItemProperty().addListener(
                (v, oldValue, newValue) -> selectedParticipant = newValue
        );

        HBox viewExpenseBox = new HBox(5);
        // Choose radio button
        ToggleGroup chooseView = new ToggleGroup();
        RadioButton allRadio = new RadioButton("All");
        allRadio.setToggleGroup(chooseView);
        RadioButton fromRadio = new RadioButton("From " + selectedParticipant);
        fromRadio.setToggleGroup(chooseView);
        RadioButton includingRadio = new RadioButton("Including " + selectedParticipant);
        includingRadio.setToggleGroup(chooseView);
        viewExpenseBox.getChildren().addAll(allRadio, fromRadio, includingRadio);

        layout.getChildren().addAll(eventBox, participantsBox, participantNames,
                expensesText, expenseAddButton, participantSelect,
                viewExpenseBox);

        // Scene
        scene = new Scene(layout, 400, 400);
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
}
