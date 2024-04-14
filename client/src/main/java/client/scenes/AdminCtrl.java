package client.scenes;

import client.LanguageManager;
import client.interfaces.VoidSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import commons.dto.JSONDumpEventDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class AdminCtrl implements VoidSceneController {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @Inject
    private ControllerUtils controllerUtils;
    private List<EventDTO> events;
    @FXML
    private Text adminPanel;
    @FXML
    private Text allEvents;
    @FXML
    private Button backButton;
    @FXML
    private Label orderBy;
    @FXML
    private Button importEventButton;
    @FXML
    private GridPane eventGrid;
    @FXML
    private ComboBox<String> orderByComboBox;

    private String lastActivity;
    private String recentActivity;
    private String oldDate;
    private String newDate;
    private String title;

    private ObservableList<String> orderByItems;

    @Inject
    private LanguageManager lm;

    /**
     * Constructor for AdminCtrl.
     *
     * @param mainCtrl    MainCtrl class
     * @param serverUtils Global ServerUtils singleton
     */
    @Inject
    public AdminCtrl(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * Initializes the scene
     *
     * @param location  passed URL location
     * @param resources passed ResourceBundle
     */
    public void initialize(URL location, ResourceBundle resources) {
        controllerUtils.bindComboBoxForKeyboardInput(orderByComboBox);
        orderByItems = FXCollections.observableArrayList();
        setLanguageForAllAdminCtrl();
        orderByComboBox.setItems(orderByItems);
    }

    /**
     * Refreshes the page.
     * Gets all events from server and orders them.
     */
    public void refresh() {
        serverUtils.registerForWebSocketUpdatesForAllEvents(this, q -> Platform.runLater(this::refresh));
        events = serverUtils.getAllEvents();
        setLanguageForAllAdminCtrl();
        orderByComboBox.getSelectionModel().selectFirst();    // default selection
    }

    /**
     * Orders the list events based on the selected option, and calls addEventsToGrid to show
     * the new ordering.
     */
    @FXML
    public void orderEvents() {
        String selected = orderByComboBox.getValue();

        if (selected != null){
            if(selected.equals(title)) {
                events.sort(Comparator.comparing(EventDTO::name, String.CASE_INSENSITIVE_ORDER));
            }
            else if(selected.equals(newDate)) {
                events.sort(Comparator.comparing(EventDTO::creationDate, Comparator.reverseOrder()));
            }
            else if(selected.equals(oldDate)) {
                events.sort(Comparator.comparing(EventDTO::creationDate));
            }
            else if(selected.equals(recentActivity)) {
                events.sort(Comparator.comparing(EventDTO::lastActivity, Comparator.reverseOrder()));
            }
            else if(selected.equals(lastActivity)) {
                events.sort(Comparator.comparing(EventDTO::lastActivity));
            }
        }

        addEventsToEventGrid();
    }

    /**
     * Adds all events to the eventGrid GridPane.
     */
    public void addEventsToEventGrid() {
        eventGrid.getChildren().clear();
        for (int i = 0; i < events.size(); i++) {
            EventDTO e = events.get(i);
            Button eventNameButton = createEventNameButton(e);
            Button deleteButton = createDeleteEventButton(e);

            Button downloadButton = new Button(lm.get("Download"));
            // Download Button
            try {
                List<JSONDumpEventDTO> allDumps = serverUtils.getJSON();
                JSONDumpEventDTO thisDump = null;
                for (JSONDumpEventDTO body : allDumps) {
                    if (body.eventDTO().code().equals(e.code()))
                        thisDump = body;
                }
                downloadButton = createDownloadEventButton(thisDump);
            }
            catch (Exception ex) {
                // If server returns an error, disable the download button
                downloadButton.setDisable(true);
            }

            eventGrid.add(eventNameButton, 0, i);
            eventGrid.add(deleteButton, 1, i);
            eventGrid.add(downloadButton, 2, i);
        }
    }

    /**
     * Creates button with the event's name, that takes the user to the event's page.
     *
     * @param event The event to link to.
     * @return Button Object.
     */
    private Button createEventNameButton(EventDTO event) {
        Button openPage = new Button(event.name());
        openPage.getStyleClass().add("edit-button");
        openPage.setOnAction(e -> mainCtrl.showEventOverview(event));

        return openPage;
    }

    /**
     * Creates button that deletes the event.
     *
     * @param event Event to link to.
     * @return Button Object.
     */
    private Button createDeleteEventButton(EventDTO event) {
        Button delete = new Button(lm.get("Delete"));
        delete.getStyleClass().add("delete-button");
        delete.setOnAction(e -> {
            boolean confirmed = controllerUtils.createConfirmationAlert(
                    lm.get("Confirm Deletion"),
                    lm.get("Are you sure you want to delete event '") + event.name() + "'?\n" +
                            lm.get("This action cannot be undone."));
            if (confirmed) {
                if(serverUtils.deleteEvent(event.code())) {
                    Alert alert = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                            lm.get("Success"),
                            lm.get("Deleted successfully"),
                            lm.get("Event '") + event.name() + lm.get("' has been deleted."));
                    alert.showAndWait();
                }
                else {
                    Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                            lm.get("Error"),
                            lm.get("Deletion unsuccessful"),
                            lm.get("Event has not been deleted due to an error. Please try again later."));
                    alert.showAndWait();
                }
            }
        });
        return delete;
    }

    /**
     * Creates the download button for the given event.
     *
     * @param event puts the JSON of an event in a file that is downloaded
     * @return the button
     */
    public Button createDownloadEventButton(JSONDumpEventDTO event) {
        Button get = new Button(lm.get("Download"));
        get.getStyleClass().add("edit-button");
        // If the given event is null for any reason, the button is disabled.
        if (event == null) {
            get.setDisable(true);
            return get;
        }

        get.setOnAction(p -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Create a file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(lm.get("Choose Download Location"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));

            File selectedFile = fileChooser.showSaveDialog(mainCtrl.getPrimaryStage());

            if (selectedFile != null) {
                try {
                    // Write the JSON to the file using the objectMapper instance
                    objectMapper.writeValue(selectedFile, event);
                    refresh();  // refresh events
                    Alert alert = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                            lm.get("Success"),
                            lm.get("Event downloaded successfully"),
                            selectedFile.toString());
                    alert.showAndWait();
                } catch (IOException ex) {
                    refresh();  // refresh events
                    Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                            lm.get("Error"),
                            lm.get("Downloading event has failed"),
                            lm.get("Please check that the program has permissions to download the event") +
                                    lm.get("to the specified location.\n\n") +
                                    lm.get("Exception details:\n") + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
        return get;
    }

    /**
     * Prompts user to select JSON file to import from with a FileChooser.
     */
    @FXML
    public void importEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(lm.get("Choose JSON File"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));

        // Show open dialog
        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Check if file is selected
        if (selectedFile != null) {
            importEventFromJSON(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Copies the contents of the file, and transforms them into entities.
     * If the operation fails, this method throws the error corresponding to the exception.
     *
     * @param jsonPath the path to the file
     */
    public void importEventFromJSON(String jsonPath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            File jsonFile = new File(jsonPath);
            JSONDumpEventDTO event = objectMapper.readValue(jsonFile, JSONDumpEventDTO.class);

            for (EventDTO ev : events) {
                if (event.eventDTO().code().equals(ev.code())) {
                    // Event with same code already exists
                    throw new IllegalArgumentException(ev.name() + " (" + ev.code() + ")");
                }
            }

            if (serverUtils.restoreEvent(event)) {
                Alert alert = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                        lm.get("Success"),
                        lm.get("Event has been imported and restored successfully"),
                        "");
                alert.showAndWait();
            }
            else {
                // Internal server error
                Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                        lm.get("Error"),
                        lm.get("Error while importing event"),
                        lm.get("Event has not been imported due to an error. The event's code in the " +
                                "JSON file may be invalid."));
                alert.showAndWait();
            }
        } catch (IllegalArgumentException e) {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"),
                    lm.get("Event with the same code already exists:"),
                    e.getMessage());
            alert.showAndWait();
        } catch (JsonParseException | JsonMappingException e) {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"),
                    lm.get("Error while parsing JSON"),
                    lm.get("Please ensure the JSON content is properly formatted."));
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"),
                    lm.get("File not found"),
                    lm.get("Check the file name and try again."));
            alert.showAndWait();
        }
    }

    /**
     * Back button action
     */
    @FXML
    private void goBack() {
        serverUtils.disconnectWSSession(this);
        mainCtrl.showStartScreen();
    }

    /**
     * Sets various Strings to the currently selected language
     */
    public void setLanguageForAllAdminCtrl() {
        adminPanel.setText(lm.get("Administrator Control Panel"));
        allEvents.setText(lm.get("All Events"));
        backButton.setText(lm.get("Back"));
        orderBy.setText(lm.get("Order by:"));
        importEventButton.setText(lm.get("Import Event"));
        lastActivity = lm.get("Last Activity (Least recent)");
        recentActivity = lm.get("Last Activity (Most recent)");
        oldDate = lm.get("Creation Date (Oldest)");
        newDate = lm.get("Creation Date (Newest)");
        title = lm.get("Title");

        setOrderOptions();
    }

    /**
     * Adds the ordering options to orderByComboBox, with the Strings of the currently selected
     * language.
     */
    private void setOrderOptions() {
        orderByItems.setAll(title, newDate, oldDate, recentActivity, lastActivity);
    }

    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            goBack();
        }
    }
}
