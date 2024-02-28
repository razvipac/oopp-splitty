package client.scenes;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class EventOverview {

    private Scene scene;
    private final Font h1 = Font.font("Arial", FontWeight.BOLD , 20);
    private final Font h2 = Font.font("Arial", FontWeight.BOLD , 14);

    /**
     * Constructor for Event Overview page
     */
    public EventOverview() {
        createScene();
    }

    /**
     * Creates the scene
     */
    public void createScene() {
        // Layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));

        // HBox eventBox with Event Name and Send Invite button
        HBox eventBox = new HBox(5);
        // Event Name (hardcoded)
        Text eventName = new Text("Event Name");
        eventName.setFont(h1);
        // Send Invite button
        Button sendInviteButton = new Button("Send Invite");
        // Add to eventBox
        eventBox.getChildren().addAll(eventName, sendInviteButton);

        // HBox participantsBox with Participants text, Edit and Add Participants button
        HBox participantsBox = new HBox(5);
        // Participants text
        Text participants = new Text("Participants");
        participants.setFont(h2);
        // Edit Participant button
        Button editParticipantButton = new Button("Edit");
        // Add Participant button
        Button addParticipantButton = new Button("Add");
        // Add to participantBox
        participantsBox.getChildren().addAll(participants, editParticipantButton, addParticipantButton);

        // Names of the participants (hardcoded)
        Text personNames = new Text("Chris, John, Anna, David");

        layout.getChildren().addAll(eventBox, participantsBox, personNames);

        // Scene
        scene = new Scene(layout, 400, 400);
    }

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }
}
