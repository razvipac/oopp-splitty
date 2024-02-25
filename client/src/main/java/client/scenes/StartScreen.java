package client.scenes;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class StartScreen {

    private Scene scene;

    public StartScreen() {
    createSceneStartScreen();
    }

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }
    /**
     * Creates the GUI for the start screen
     */
    public void createSceneStartScreen() {
        // Labels for the start screen
        Label createANewEventLabel = new Label("Create a new event");
        createANewEventLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        createANewEventLabel.setPadding(new Insets(10, 10, 10, 10));

        Label joinEventLabel = new Label("Join an event");
        joinEventLabel.setFont(Font.font("Arial", FontWeight.BOLD,20));
        joinEventLabel.setPadding(new Insets(10, 10, 10, 10));

        Label recentlyViewedEventsLabel = new Label("Recently viewed events:");
        recentlyViewedEventsLabel.setFont(Font.font("Arial", FontWeight.BOLD,20));
        recentlyViewedEventsLabel.setPadding(new Insets(10, 10, 10, 10));

        // Text fields for creating and joining events
        TextField createEvent = new TextField();
        createEvent.setFont(Font.font("Arial", 20));
        createEvent.setPromptText("Enter event name here");
        createEvent.setMaxWidth(300);
        createEvent.setPadding(new Insets(10, 10, 10, 10));

        TextField joinEvent = new TextField();
        joinEvent.setFont(Font.font("Arial", 20));
        joinEvent.setPromptText("Enter event code here");
        joinEvent.setMaxWidth(300);
        joinEvent.setPadding(new Insets(10, 10, 10, 10));

        // Buttons for creating and joining events
        Button createButton = new Button("Create");
        createButton.setFont(Font.font("Arial"));
        createButton.setOnAction(e -> {
            // create the event (not implemented)
            System.out.println("Event created: " + createEvent.getText());
        });

        Button joinButton = new Button("Join");
        joinButton.setFont(Font.font("Arial"));
        joinButton.setOnAction(e -> {
            // join the event (not implemented)
            System.out.println("Event joined: " + joinEvent.getText());
        });

        // HBoxes for creating and joining events
        HBox createEventBox = new HBox(createEvent, createButton);
        createEventBox.setSpacing(10);
        createEventBox.setAlignment(Pos.CENTER);

        HBox joinEventBox = new HBox(joinEvent, joinButton);
        joinEventBox.setSpacing(10);
        joinEventBox.setAlignment(Pos.CENTER);

        // hardcoded data of recently viewed events
        HBox skiTripBox = createEventBox("• Ski Trip");
        skiTripBox.setAlignment(Pos.CENTER);

        HBox museumVisitBox = createEventBox("• Museum Visit");
        museumVisitBox.setAlignment(Pos.CENTER);

        HBox giftForJohnBox = createEventBox("• Gift for John's Birthday");
        giftForJohnBox.setAlignment(Pos.CENTER);

        HBox newYearPartyBox = createEventBox("• New Year Party");
        newYearPartyBox.setAlignment(Pos.CENTER);

        // layout for the start screen
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(createANewEventLabel, createEventBox,
                joinEventLabel, joinEventBox, recentlyViewedEventsLabel,
                skiTripBox, museumVisitBox, giftForJohnBox, newYearPartyBox);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 300, 300);

        layout.prefWidthProperty().bind(scene.widthProperty());
        layout.prefHeightProperty().bind(scene.heightProperty());
    }

    /**
     * Creates a HBox for an event
     * @param eventName the name of the event
     * @return the HBox for the event
     */
    private HBox createEventBox(String eventName) {
        Text eventText = new Text(eventName);
        eventText.setFont(Font.font("Arial", 12));
        Button deleteButton = new Button("x");
        HBox eventBox = new HBox(eventText, deleteButton);
        eventBox.setSpacing(10);
        return eventBox;
    }


}
