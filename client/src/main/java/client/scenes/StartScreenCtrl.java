package client.scenes;

import client.LanguageManager;
import client.LanguageOption;
import client.interfaces.VoidSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.*;

public class StartScreenCtrl implements VoidSceneController{

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @Inject
    private ControllerUtils controllerUtils;
    @Inject
    private LanguageManager lm;

    private static final String STORAGE_PATH =
            "client/src/main/resources/userSettings/savedData/recently_joined_event_codes.ser";

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
    private ComboBox<LanguageOption> languageButton;
    @FXML
    private Label createNewEvent;
    @FXML
    private Button createButton;
    @FXML
    private Label join;
    @FXML
    private GridPane recentViewedEvents;

    private Scene scene;

    private Deque<String> recentlyJoinedEventCodes = new LinkedList<>();

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
     * Initializes the scene
     * @param location passed URL location
     * @param resources passed ResourceBundle
     */
    public void initialize(URL location, ResourceBundle resources) {
        createEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) createEvent();
        });

        joinEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) joinEvent();
        });

        controllerUtils.bindComboBoxForKeyboardInput(languageButton);

        // check if storage file exists
        File f = new File(STORAGE_PATH);
        if (!f.exists()) {
            controllerUtils.saveObject(STORAGE_PATH, new LinkedList<>());
        }

        recentlyJoinedEventCodes = controllerUtils.readObject(STORAGE_PATH);

        setLanguageForAll();

        loadLanguageButton();

        class LanguageOptionCellClass extends ListCell<LanguageOption> {
            private Label name = new Label();
            private ImageView icon = new ImageView();
            private final HBox cell;
            {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                icon.setFitHeight(16);
                icon.setPreserveRatio(true);
                cell = new HBox();
                cell.setSpacing(5);
                cell.getChildren().add(icon);
                cell.getChildren().add(name);
            }

            public Label getName() {
                return name;
            }

            @Override
            protected void updateItem(LanguageOption languageOption, boolean b) {
                super.updateItem(languageOption, b);

                if (languageOption == null) {
                    setGraphic(null);
                } else {
                    name.setText(lm.get(languageOption, languageOption.toString()));
                    icon.setImage(LanguageManager.getFlagImage(languageOption));
                    setGraphic(cell);
                }
            }
        }

        languageButton.setCellFactory(new Callback<ListView<LanguageOption>, ListCell<LanguageOption>>() {
            @Override
            public ListCell<LanguageOption> call(ListView<LanguageOption> languageOptionListView) {
                return new LanguageOptionCellClass();
            }
        });

        languageButton.setButtonCell(new LanguageOptionCellClass() {
            {
                getName().setStyle("-fx-text-fill: #000000");
            }
        });

    }

    /**
     * Refreshes the scene with fresh data from the server
     */
    public void refresh(){
        scene = joinButton.getScene();
        createEventTextField.clear();
        joinEventTextField.clear();
        events = server.getAllEvents();
        updateRecentEvents();
        setLanguageForAll();
    }

    private void loadLanguageButton() {
        System.out.println("Loading language button");

        languageButton.getItems().clear();
        languageButton.getItems().addAll(
                new LanguageOption(LanguageOption.Language.ENGLISH),
                new LanguageOption(LanguageOption.Language.DUTCH),
                new LanguageOption(LanguageOption.Language.ROMANIAN)
        );

        switch (lm.getLanguageOption().getLanguage()){
            case ENGLISH -> languageButton.getSelectionModel().select(0);
            case DUTCH -> languageButton.getSelectionModel().select(1);
            case ROMANIAN -> languageButton.getSelectionModel().select(2);
        }
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
            addRecentlyJoinedEventCode(code);
            mainCtrl.showEventOverview(found.get());
        }
        else {
            controllerUtils.createAlert(Alert.AlertType.ERROR, 
                    lm.get("Event does not exist"),
                    lm.get("Event with code") + " \"" + code + "\" " + lm.get("does not exist") + "!",
                    lm.get("Event with code") + " \"" + code + "\" " + lm.get("does not exist") + "! " +
                            lm.get("Check your event code again!")
            ).showAndWait();
        }
    }

    private void addRecentlyJoinedEventCode(String code) {
        recentlyJoinedEventCodes.removeIf(eventCode -> eventCode.equals(code));
        recentlyJoinedEventCodes.addLast(code);
        controllerUtils.saveObject(STORAGE_PATH, recentlyJoinedEventCodes);
        updateRecentEvents();
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

        addRecentlyJoinedEventCode(event.code());

        mainCtrl.showEventOverview(event);
    }


    /**
     * Sets the language for all elements in the user interface.
     * This method retrieves translations for various UI elements
     * from the LanguageManager and updates the corresponding
     * text or prompt text accordingly.
     */
    public void setLanguageForAll(){
        System.out.println("Setting language for start screen");
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

        List<EventDTO> recentlyJoinedEventDTOs = new ArrayList<>();
        recentlyJoinedEventCodes.forEach(code -> {
            EventDTO found = server.getEvent(code);
            if (found != null) recentlyJoinedEventDTOs.add(found);
        });

        int amountOfEvents = 0;
        int lastIndex = recentlyJoinedEventDTOs.size() - 1;
        for (int i = lastIndex; i >= 0 && amountOfEvents < 4; i--) {
            EventDTO event = new ArrayList<>(recentlyJoinedEventDTOs).get(i);

            Label eventName = new Label(event.name());
            Button overviewButton = new Button();
            overviewButton.getStyleClass().add("small-button");
            FontAwesomeIconView arrow = new FontAwesomeIconView(FontAwesomeIcon.ARROW_RIGHT);
            arrow.getStyleClass().add("small-button-icon");
            overviewButton.setGraphic(arrow);
            overviewButton.setOnAction(e -> {
                addRecentlyJoinedEventCode(event.code());
                mainCtrl.showEventOverview(event);
                updateRecentEvents();
            });

            Button removeButton = new Button();
            removeButton.getStyleClass().add("small-button-delete");
            FontAwesomeIconView cross = new FontAwesomeIconView(FontAwesomeIcon.TIMES);
            cross.getStyleClass().add("small-button-delete-icon");
            removeButton.setGraphic(cross);
            removeButton.setOnAction(e -> {
                recentlyJoinedEventCodes.remove(event.code());
                controllerUtils.saveObject(STORAGE_PATH, recentlyJoinedEventCodes);
                refresh();
            });

            recentViewedEvents.add(eventName, 0, amountOfEvents);
            recentViewedEvents.add(overviewButton, 1, amountOfEvents);
            recentViewedEvents.add(removeButton, 2, amountOfEvents);
            amountOfEvents++;
        }
    }

    /**
     * Handles the language translation action.
     * @param actionEvent The event that triggered the action.
     */
    public void translate(ActionEvent actionEvent) {
        int option = languageButton.getSelectionModel().getSelectedIndex();
        // Add your custom logic here based on the selected language;
        switch (option){
            case -1:
                break;
            case 0:
                lm.saveLanguage(
                        new LanguageOption(LanguageOption.Language.ENGLISH));
                System.out.println("Saved english");
                mainCtrl.reloadAllLanguages();
                break;
            case 1:
                lm.saveLanguage(
                        new LanguageOption(LanguageOption.Language.DUTCH));
                System.out.println("Saved dutch");
                mainCtrl.reloadAllLanguages();
                break;
            case 2:
                lm.saveLanguage(
                        new LanguageOption(LanguageOption.Language.ROMANIAN));
                System.out.println("Saved romanian");
                mainCtrl.reloadAllLanguages();
                break;
        }
    }

    @FXML
    private void onGlobalKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.J) {
            joinEventTextField.requestFocus();
        }
        if (keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.C) {
            createEventTextField.requestFocus();
        }
    }
}
