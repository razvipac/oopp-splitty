package client.scenes;

import client.interfaces.StaticSceneController;
import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.dto.EventDTO;
import commons.response_body.EventResponseBody;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Admin implements StaticSceneController {
    private final MainCtrl mainCtrl;
    private Scene scene;
    private ServerUtils serverUtils;
    private VBox eventsField;
    private List<EventDTO> events;

    /**
     * Constructor for the Admin page that creates the GUI
     * @param mainCtrl main controller for the application
     * @param serverUtils server utilities for the application
     */
    public Admin(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * Generates the Scene
     * @return the generated scene
     */
    public Scene initialize(){
        events = serverUtils.getAllEvents();

        createSceneAdmin();

        return scene;
    }

    /**
     * Creates GUI
     */
    public void createSceneAdmin() {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());

        // Creating GridPane for displaying data
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));

        // Adding the grid and back button to the layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        //layout.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

        // VBox to hold event entries
        eventsField = new VBox();
        eventsField.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        eventsField.setSpacing(5);

        ComboBox<String> sortingOptions = new ComboBox<>();
        sortingOptions.getItems().addAll("Title", "Creation Date", "Last Activity");
        sortingOptions.setValue("Title"); // Default selection
        sortingOptions.setOnAction(event -> {
            String selectedOption = sortingOptions.getValue();
            if(Objects.equals(selectedOption, "Creation Date")) {
                eventsField.getChildren().clear();
                showEvent(2);
            } else if(Objects.equals(selectedOption, "Last Activity")){
                eventsField.getChildren().clear();
                showEvent(3);
            } else {
                eventsField.getChildren().clear();
                showEvent(1);}
            System.out.println("Sorting by: " + selectedOption);
            // For now, let's just print the selected option
        });
        Button importEvent = importJSON();
        layout.getChildren().addAll(sortingOptions ,gridPane, eventsField, backButton, importEvent);
        // Show events
        showEvent(1);

        scene = new Scene(layout, 400, 500); // changed this line
    }

    void showEvent(int compare) {
        List<EventDTO> list = serverUtils.getAllEvents();
        if(compare==1) list.sort(Comparator.comparing(EventDTO::getName));
        else if(compare==2) list.sort(Comparator.comparing(EventDTO::getCreationDate));
        else list.sort(Comparator.comparing(EventDTO::getLastActivity));

        for (EventDTO event : list) {
            HBox eventEntry = new HBox();
            eventEntry.setSpacing(15);

            Button openPage = openOverview(event);
            eventEntry.getChildren().add(openPage);
            Button deleteEvent = deleteEvent(event);
            eventEntry.getChildren().add(deleteEvent);
            List<EventResponseBody> dump = serverUtils.getJSON();
            EventResponseBody op=null;
            for(EventResponseBody body : dump){
                if(body.event().getCode().equals(event.getCode()))
                    op = body;
            }
            if(op==null)
                throw new IllegalArgumentException();
            Button download = downloadEvent(op);
            eventEntry.getChildren().add(download);
            eventsField.getChildren().add(eventEntry);
        }
    }

    private Button getJSON(EventDTO event) {
        Button get = new Button("Download");
        get.setOnAction(e -> {
            //server.getJsonDumpUtils().getJSON();
            StringSelection stringSelection = new StringSelection ("111");
            Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
            clpbrd.setContents (stringSelection, null);
        });
        return get;
    }

    /**
     *
     * @param event puts the JSON of an event in a file that is downloaded
     * @return the button
     */
    public Button downloadEvent(EventResponseBody event) {
        // Get the selected events
        Button get = new Button("Download");
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
                // Make a separate file for each event
                String filename = selectedFile.getAbsolutePath();
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

    private Button deleteEvent(EventDTO event) {
        Button delete = new Button("Delete");
        delete.setOnAction(e -> {
            //eventsField.getChildren().clear();
            serverUtils.deleteEvent(event.getCode());
            eventsField.getChildren().clear();
            showEvent(2);
        });
        return delete;
    }

    private Button openOverview(EventDTO event) {
        Button openPage = new Button(event.getName());
        openPage.setOnAction(e -> mainCtrl.showEventOverview(event));

        return openPage;
    }

    /**
     * Opens fileChooser when clicked
     * @return the button
     */
    public Button importJSON() {
        Button importEvent = new Button("Import Event");
        importEvent.setOnAction(b -> {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose JSON File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));

            // Show open dialog
            Stage stage = new Stage();
            File selectedFile = fileChooser.showOpenDialog(stage);

            // Check if file is selected
            if (selectedFile != null) {
                try {
                    // Import event from JSON
                    EventResponseBody result = importEventFromJSON(selectedFile.getAbsolutePath());

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
                    alert.setContentText("An error occurred while importing the event from JSON: " + e.getMessage());
                    alert.showAndWait();
                }

            }
        });
        return importEvent;
    }

    /**
     * Copies the contents of the file, and transforms them into entities
     * @param jsonPath the path to the file
     * @return returns the response entity with which the event is restored
     */
    public EventResponseBody importEventFromJSON(String jsonPath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            File jsonFile = new File(jsonPath);
            EventResponseBody event = objectMapper.readValue(jsonFile, EventResponseBody.class);
            serverUtils.restoreEvent(event);
            return event;
        } catch(JsonParseException | JsonMappingException e) {
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
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    private void goBack(){
        mainCtrl.showDevScreen();
    }
}
