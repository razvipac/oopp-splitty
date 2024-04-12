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
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    private String lastActivity;
    @FXML
    private String recentActivity;
    @FXML
    private String oldDate;
    @FXML
    private String newDate;
    @FXML
    private String title;
    @FXML
    private GridPane eventGrid;
    @FXML
    private ComboBox<String> orderByComboBox;

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
     * @param location passed URL location
     * @param resources passed ResourceBundle
     */
    public void initialize(URL location, ResourceBundle resources) {
        orderByComboBox.getSelectionModel().selectFirst();    // default selection
    }

    /**
     * Refreshes the page.
     * Gets all events from server and orders them.
     */
    public void refresh() {
        events = serverUtils.getAllEvents();
        orderEvents();
        setLanguageForAllAdminCtrl();
    }

    /**
     * Orders the list events based on the selected option, and calls addEventsToGrid to show
     * the new ordering.
     */
    @FXML
    public void orderEvents() {
        switch (orderByComboBox.getValue()) {
            case "Title" ->
                    events.sort(Comparator.comparing
                            (EventDTO::name, String.CASE_INSENSITIVE_ORDER));
            case "Creation Date (Newest)" ->
                    events.sort(Comparator.comparing(EventDTO::creationDate,
                            Comparator.reverseOrder()));
            case "Creation Date (Oldest)" ->
                    events.sort(Comparator.comparing(EventDTO::creationDate));
            case "Last Activity (Most recent)" ->
                    events.sort(Comparator.comparing(EventDTO::lastActivity,
                            Comparator.reverseOrder()));
            case "Last Activity (Least recent)" ->
                    events.sort(Comparator.comparing(EventDTO::lastActivity));
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

            // Download Button
            List<JSONDumpEventDTO> allDumps = serverUtils.getJSON();
            JSONDumpEventDTO thisDump = null;
            for(JSONDumpEventDTO body : allDumps){
                if(body.eventDTO().code().equals(e.code()))
                    thisDump = body;
            }
            Button downloadButton = createDownloadEventButton(thisDump);

            eventGrid.add(eventNameButton, 0, i);
            eventGrid.add(deleteButton, 1, i);
            eventGrid.add(downloadButton, 2, i);
        }
    }

    /**
     * Creates button with the event's name, that takes the user to the event's page.
     * @param event The event to link to.
     * @return Button Object.
     */
    private Button createEventNameButton(EventDTO event) {
        Button openPage = new Button(event.name());
        openPage.setOnAction(e -> mainCtrl.showEventOverview(event));

        return openPage;
    }

    /**
     * Creates button that deletes the event.
     * @param event Event to link to.
     * @return Button Object.
     */
    private Button createDeleteEventButton(EventDTO event) {
        LanguageManager lm = mainCtrl.getLanguageManager();
        Button delete = new Button(lm.get("Delete"));
        delete.setOnAction(e -> {
            boolean confirmed = controllerUtils.createConfirmationAlert(
                    lm.get("Confirm Deletion"),
                    lm.get("Are you sure you want to delete event '") + event.name() + "'?\n" +
                            lm.get("This action cannot be undone."));
            if (confirmed) {
                serverUtils.deleteEvent(event.code());
                Alert alert = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                        lm.get("Success"),
                        lm.get("Deleted successfully"),
                        lm.get("Event '") + event.name() + lm.get("' has been deleted."));
                alert.showAndWait();
                refresh();
            }
        });
        return delete;
    }

    /**
     * Creates the download button for the given event.
     * @param event puts the JSON of an event in a file that is downloaded
     * @return the button
     */
    public Button createDownloadEventButton(JSONDumpEventDTO event) {
        LanguageManager lm = mainCtrl.getLanguageManager();
        Button get = new Button(lm.get("Download"));
        // If the given event is null for any reason, the button is disabled.
        if(event == null) {
            get.setDisable(true);
            return get;
        }

        get.setOnAction(p -> {
            refresh();  // refresh events
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
                            lm.get("Event has been downloaded successfully"),
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
        LanguageManager lm = mainCtrl.getLanguageManager();
        fileChooser.setTitle(lm.get("Choose JSON File"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));

        // Show open dialog
        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Check if file is selected
        if (selectedFile != null) {
            try {
                // Import event from JSON
                JSONDumpEventDTO result = importEventFromJSON(selectedFile.getAbsolutePath());
                // Throw an exception if result is null
                if (result == null) throw new IllegalArgumentException();
                // Else show success message
                refresh();  // refresh events
                Alert alert = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                        lm.get("Success"),
                        lm.get("Event has been imported and restored successfully"),
                        "");
                alert.showAndWait();
            } catch (Exception e) {
                // Display an error message
                refresh();  // refresh events
                Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                        lm.get("Error"),
                        lm.get("Failed to import event"),
                        lm.get("An error occurred while importing the event from JSON.\n\n") +
                                lm.get("Exception details: \n") + e.getMessage());
                alert.showAndWait();
            }
        }

    }

    /**
     * Copies the contents of the file, and transforms them into entities
     *
     * @param jsonPath the path to the file
     * @return returns the response entity with which the event is restored
     */
    public JSONDumpEventDTO importEventFromJSON(String jsonPath) {
        LanguageManager lm = mainCtrl.getLanguageManager();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            File jsonFile = new File(jsonPath);
            JSONDumpEventDTO event = objectMapper.readValue(jsonFile, JSONDumpEventDTO.class);
            serverUtils.restoreEvent(event);
            return event;
        } catch (JsonParseException | JsonMappingException e) {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"),
                    lm.get("Error while parsing JSON."),
                    lm.get("Please ensure the JSON content is properly formatted."));
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"),
                    lm.get("File not found"),
                    lm.get("Check the file name and try again."));
            alert.showAndWait();
        }
        return null;
    }

    /**
     * Back button action
     */
    @FXML
    private void goBack() {
        mainCtrl.showStartScreen();
    }

    public void setLanguageForAllAdminCtrl(){
        LanguageManager lm = mainCtrl.getLanguageManager();
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
    }
}
