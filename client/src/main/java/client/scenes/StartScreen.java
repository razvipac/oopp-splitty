package client.scenes;

import client.Main;

import client.utils.ServerUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import commons.Event;

import java.util.List;
import java.util.Optional;

public class StartScreen {

    private Scene scene;
    private Main main;
    private EventOverview eventOverview;

    private final ServerUtils server = ServerUtils.getServerUtils();

    private List<Event> events;

    /**
     * Constructor for the start screen that calls the method to create the GUI
     * @param main scene of the main class
     */
    public StartScreen(Main main) {
        this.main = main;
        events = server.getEventUtils().getAllEvents();

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
        TextField createEventTextField = createTextField("Arial", 20, "Enter event name here", 300,
                new Insets(10, 10, 10, 10));
        createEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) createEventFromTextField(createEventTextField);
        });

        TextField joinEventTextField = createTextField("Arial", 20, "Enter event code here", 300,
                new Insets(10, 10, 10, 10));
        joinEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) joinEventFromTextField(joinEventTextField);
        });


        // Buttons for creating and joining events
        Button createButton = new Button("Create");
        createButton.setFont(Font.font("Arial"));
        createButton.setOnAction(e -> createEventFromTextField(createEventTextField));

        Button joinButton = new Button("Join");
        joinButton.setFont(Font.font("Arial"));
        joinButton.setOnAction(e -> joinEventFromTextField(joinEventTextField));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());

        // HBoxes for creating and joining events
        HBox createEventBox = createHBox(10, Pos.CENTER, createEventTextField, createButton);

        HBox joinEventBox = createHBox(10, Pos.CENTER, joinEventTextField, joinButton);


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
                skiTripBox, museumVisitBox, giftForJohnBox, newYearPartyBox, backButton);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        });

        layout.prefWidthProperty().bind(scene.widthProperty());
        layout.prefHeightProperty().bind(scene.heightProperty());
    }

    private void goBack() {
        main.getPrimaryStage().setScene(main.getMainScene());
    }

    private void joinEventFromTextField(TextField joinEvent) {
        String code = joinEvent.getText();
        Optional<Event> found = getEvent(code);
        if(found.isPresent()) {
            Event event = found.get();
            eventOverview = new EventOverview(main, event);
            main.getPrimaryStage().setScene(eventOverview.getScene());
        }
        else System.out.println("Event with code: " + code + " doesn't exist");
    }

    private void createEventFromTextField(TextField createEvent) {
        String eventName = createEvent.getText();
        Event event = server.getEventUtils().createEvent(eventName);
        System.out.println(event.toString());
        eventOverview = new EventOverview(main, event);
        main.getPrimaryStage().setScene(eventOverview.getScene());
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

    /**
     * Get event corresponding to the given code
     * @param code Code of the event
     * @return The event
     */
    public Optional<Event> getEvent(String code) {
        return events.stream()
                .filter(event -> event.getCode().equals(code))
                .findFirst();
    }

}
