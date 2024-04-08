package client.scenes;

import client.LanguageManager;
import client.LanguageOption;
import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.*;

public class StartScreenCtrl implements VoidSceneController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private Label recentlyViewed;
    @FXML
    private Text welcome;
    @FXML
    private Label changeLanguage;
    @FXML
    private Button controlPanel;
    @FXML
    private Label administrator;
    @FXML
    private Button joinButton;
    @FXML
    private TextField createEventTextField;
    @FXML
    private TextField joinEventTextField;
    @FXML
    private ComboBox<HBox> languageButton;
    @FXML
    private Label createNewEvent;
    @FXML
    private Button createButton;
    @FXML
    private Label join;
    @FXML
    private GridPane recentViewedEvents;

    private Set<EventDTO> recentlyJoinedEvents = new LinkedHashSet<>();

    private List<EventDTO> events;

    /**
     * Constructor for the StartScreenCtrl class.
     * @param server The global serverUtils singleton.
     * @param mainCtrl The scene of the mainCtrl class.
     */
    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Initializes the controller.
     * Sets up event listeners and refreshes the scene.
     */
    public void initialize() {
        createEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) createEvent();
        });

        joinEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) joinEvent();
        });
        setLanguageForAll();
        refresh();
    }


    private void loadLanguageButton() {
        System.out.println("Loading language button");
        HBox hbox1 = new HBox();
        hbox1.getChildren().addAll(
                mainCtrl.getLanguageManager().createFlagIcon(
                        new LanguageOption(LanguageOption.Language.ENGLISH)),
                new Label("English"));
        HBox hbox2 = new HBox();
        hbox2.getChildren().addAll(
                mainCtrl.getLanguageManager().createFlagIcon(
                        new LanguageOption(LanguageOption.Language.DUTCH)),
                new Label("Nederlands"));
        HBox hbox3 = new HBox();
        hbox3.getChildren().addAll(
                mainCtrl.getLanguageManager().createFlagIcon(
                        new LanguageOption(LanguageOption.Language.ROMANIAN)),
                new Label("Romana"));
        languageButton.getItems().clear();
        languageButton.getItems().addAll(hbox1, hbox2, hbox3);

        HBox hbox4 = new HBox();
        if(mainCtrl.getLanguageManager() != null){
            hbox4.getChildren().add(
                    mainCtrl.getLanguageManager().createFlagIcon(
                            mainCtrl.getLanguageManager().getLanguageOption())
            );
        }else{
        }
        languageButton.getSelectionModel().select(hbox4);
    }

    /**
     * Handles the "Join Event" button action.
     * Retrieves the event with the provided code and navigates to its overview.
     */
    @FXML
    private void joinEvent() {
        String code = joinEventTextField.getText();
        Optional<EventDTO> found = getEvent(code);
        if(found.isPresent()) {
            recentlyJoinedEvents.removeIf(event -> event.code().equals(code));
            recentlyJoinedEvents.add(found.get());
            updateRecentEvents();
            mainCtrl.showEventOverview(found.get());
        }
        else System.out.println("Event with code: " + code + " doesn't exist");
    }

    /**
     * Handles the "Create Event" button action.
     * Creates a new event with the provided name and navigates to its overview.
     */
    @FXML
    private void createEvent() {
        String eventName = createEventTextField.getText();
        EventDTO event = server.createEvent(eventName);
        events = server.getAllEvents();
        System.out.println(event.toString());
        recentlyJoinedEvents.add(event);
        mainCtrl.showEventOverview(event);
        updateRecentEvents();
    }


    /**
     * Sets the language for all elements in the user interface.
     * This method retrieves translations for various UI elements
     * from the LanguageManager and updates the corresponding
     * text or prompt text accordingly.
     */
    public void setLanguageForAll(){
        LanguageManager lm = mainCtrl.getLanguageManager();
        if(lm == null){
            return;
        }
        createEventTextField.setPromptText(lm.get("Enter event name"));
        joinEventTextField.setPromptText(lm.get("Enter event code"));
        createNewEvent.setText(lm.get("Create a new event"));
        createButton.setText(lm.get("Create"));
        welcome.setText(lm.get("Welcome to"));
        changeLanguage.setText(lm.get("Change language:"));
        controlPanel.setText(lm.get("Control Panel"));
        administrator.setText(lm.get("Administrator"));
        joinButton.setText(lm.get("Join"));
        join.setText(lm.get("Join an existing event"));
        recentlyViewed.setText(lm.get("Recently viewed events:"));


    }
    /**
     * Opens the admin control panel popup
     */
    @FXML
    private void openAdminControlPanel() {
        mainCtrl.showAdmin();
    }

    /**
     * Retrieves the event corresponding to the given code.
     * @param code The code of the event.
     * @return The event with the provided code, if found.
     */
    private Optional<EventDTO> getEvent(String code) {
        return events.stream()
                .filter(event -> event.code().equals(code))
                .findFirst();
    }

    /**
     * Updates the recent events view. Displays the last 4 events that the user has joined.
     */
    private void updateRecentEvents() {
        recentViewedEvents.getChildren().clear();
        List<String> recentlyJoinedEventsCodes = (new ArrayList<>(recentlyJoinedEvents))
                .stream().map(EventDTO::code).toList();

        recentlyJoinedEvents.clear();
        recentlyJoinedEventsCodes.forEach(code -> recentlyJoinedEvents.add(server.getEvent(code)));

        int amountOfEvents = 0;
        int lastIndex = recentlyJoinedEvents.size() - 1;
        for (int i = lastIndex; i >= 0 && amountOfEvents < 4; i--) {
            EventDTO event = new ArrayList<>(recentlyJoinedEvents).get(i);
            Label eventName = new Label(event.name());
            Button overviewButton = new Button("\u2192");
            overviewButton.setOnAction(e -> {
                mainCtrl.showEventOverview(event);
                recentlyJoinedEvents.remove(event);
                recentlyJoinedEvents.add(event);
                updateRecentEvents();
            });
            Button removeButton = new Button("\u0078");
            removeButton.setOnAction(e -> {
                recentlyJoinedEvents.remove(event);
                refresh();
            });

            recentViewedEvents.add(eventName, 0, amountOfEvents);
            recentViewedEvents.add(overviewButton, 1, amountOfEvents);
            recentViewedEvents.add(removeButton, 2, amountOfEvents);
            amountOfEvents++;
        }
    }

    /**
     * Refreshes the start screen by clearing text fields and updating event data.
     */
    public void refresh(){
        createEventTextField.clear();
        joinEventTextField.clear();
        events = server.getAllEvents();
        loadLanguageButton();
        updateRecentEvents();
    }

    /**
     * Handles the language translation action.
     * @param actionEvent The event that triggered the action.
     */
    public void translate(ActionEvent actionEvent) {
        int option = languageButton.getSelectionModel().getSelectedIndex();
        // Add your custom logic here based on the selected language;
        HBox hBox = new HBox();
        switch (option){
            case 0:
                mainCtrl.getLanguageManager().saveLanguage(
                        new LanguageOption(LanguageOption.Language.ENGLISH));
                System.out.println("Saved english");
                mainCtrl.reloadAllLanguages();
                mainCtrl.showStartScreen();
                //TODO - refresh the page
                break;
            case 1:
                mainCtrl.getLanguageManager().saveLanguage(
                        new LanguageOption(LanguageOption.Language.DUTCH));
                System.out.println("Saved dutch");
                mainCtrl.reloadAllLanguages();
                mainCtrl.showStartScreen();
                //TODO - refresh the page
                break;
            case 2:
                mainCtrl.getLanguageManager().saveLanguage(
                        new LanguageOption(LanguageOption.Language.ROMANIAN));
                System.out.println("Saved romanian");
                mainCtrl.reloadAllLanguages();
                mainCtrl.showStartScreen();
                //TODO - refresh the page
                break;
        }
    }
}
