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

    /**
     * Constructor for the start screen that calls the method to create the GUI
     */
    public StartScreen() {
        createSceneStartScreen();
    }

    /**
     * Getter for the scene
     *
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
        Label createANewEventLabel = createLabel("Create a new event", "Arial", FontWeight.BOLD,
                20, new Insets(10, 10, 10, 10));

        Label joinEventLabel = createLabel("Join an event", "Arial", FontWeight.BOLD,
                20, new Insets(10, 10, 10, 10));

        Label recentlyViewedEventsLabel = createLabel("Recently viewed events:", "Arial",
                FontWeight.BOLD, 20, new Insets(10, 10, 10, 10));


        // Text fields for creating and joining events
        TextField createEvent = createTextField("Arial", 20, "Enter event name here", 300,
                new Insets(10, 10, 10, 10));

        TextField joinEvent = createTextField("Arial", 20, "Enter event code here", 300,
                new Insets(10, 10, 10, 10));


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
        HBox createEventBox = createHBox(10, Pos.CENTER, createEvent, createButton);

        HBox joinEventBox = createHBox(10, Pos.CENTER, joinEvent, joinButton);


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
     *
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

    /**
     * Creates a label with the given parameters
     * @param text Text of the label
     * @param font Font of the label
     * @param weight Boldness of the label
     * @param size Size of the label
     * @param padding Padding of the label
     * @return the label
     */
    public Label createLabel(String text, String font, FontWeight weight,
                             int size, Insets padding) {
        Label label = new Label(text);
        label.setFont(Font.font(font, weight, size));
        label.setPadding(padding);
        return label;
    }

    /**
     * Creates a text field with the given parameters
     * @param font Font of the text field
     * @param size Size of the text field
     * @param promptText Prompt text of the text field
     * @param maxWidth  Maximum width of the text field
     * @param padding Padding of the text field
     * @return the text field
     */
    public TextField createTextField(String font, int size, String promptText,
                                     int maxWidth, Insets padding) {
        TextField textField = new TextField();
        textField.setFont(Font.font(font, size));
        textField.setPromptText(promptText);
        textField.setMaxWidth(maxWidth);
        textField.setPadding(padding);
        return textField;
    }

    /**
     * Overloaded method for creating a HBox
     * @param spacing Spacing of the HBox
     * @param alignment Alignment of the HBox
     * @param textField TextField in the HBox
     * @param button Button in the HBox
     * @return the HBox
     */
    public HBox createHBox(double spacing, Pos alignment, TextField textField, Button button) {
        HBox hbox = new HBox(textField, button);
        hbox.setSpacing(spacing);
        hbox.setAlignment(alignment);
        return hbox;
    }

}
