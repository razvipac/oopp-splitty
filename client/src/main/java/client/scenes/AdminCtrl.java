package client.scenes;

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
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class AdminCtrl implements VoidSceneController {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @Inject
    private ControllerUtils controllerUtils;
    private List<EventDTO> events;

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
     * Initializes the Scene
     */
    public void initialize() {
        orderByComboBox.getSelectionModel().selectFirst();    // default selection
        refresh();
    }

    /**
     * Refreshes the page.
     * Gets all events from server and orders them.
     */
    public void refresh() {
        events = serverUtils.getAllEvents();
        orderEvents();
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
        Button delete = new Button("Delete");
        delete.setOnAction(e -> {
            boolean confirmed = controllerUtils.createConfirmationAlert(
                    "Confirm Deletion",
                    "Are you sure you want to delete event '" + event.name() + "'?\n" +
                            "This action cannot be undone.");
            if (confirmed) {
                serverUtils.deleteEvent(event.code());
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
        Button get = new Button("Download");
        // If the given event is null for any reason, the button is disabled.
        if(event == null) {
            get.setDisable(true);
            return get;
        }

        get.setOnAction(p -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Create a file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Download Location");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));

            File selectedFile = fileChooser.showSaveDialog(mainCtrl.getPrimaryStage());

            if (selectedFile != null) {
                try {
                    // Write the JSON to the file using the objectMapper instance
                    objectMapper.writeValue(selectedFile, event);
                    System.out.println("(SUCCESS) Event downloaded");
                } catch (IOException ex) {
                    ex.printStackTrace();
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
        fileChooser.setTitle("Choose JSON File");
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
                if (result == null) {
                    throw new IllegalArgumentException();
                }

                // Do something with the result if needed
            } catch (Exception e) {
                // Display an error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to import event");
                alert.setContentText("An error occurred while importing the event from JSON: "
                        + e.getMessage());
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
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            File jsonFile = new File(jsonPath);
            JSONDumpEventDTO event = objectMapper.readValue(jsonFile, JSONDumpEventDTO.class);
            System.out.println(serverUtils.restoreEvent(event));
            return event;
        } catch (JsonParseException | JsonMappingException e) {
            System.err.println("Error while parsing JSON: " + e.getMessage());
            System.err.println("Please ensure that the JSON content is correctly formatted.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("File not found");
            e.printStackTrace();
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
}
