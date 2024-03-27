package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Admin {
    private Main main;
    private Scene scene;
    private EventOverview eventOverview;
    private ServerUtils server = ServerUtils.getServerUtils();
    private VBox eventsField;

    /**
     * Constructor for the Admin page that creates the GUI
     * @param main
     */
    public Admin(Main main) {
        this.main = main;
        createSceneAdmin();
    }

    /**
     * Creates GUI
     */
    public void createSceneAdmin() {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> main.getPrimaryStage().setScene(main.getMainScene()));

        // Creating GridPane for displaying data
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));


        List<Event> events = server.getAllEvents();

        // Adding the grid and back button to the layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        //layout.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        scene = new Scene(layout, 400, 500); // changed this line


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

        layout.getChildren().addAll(sortingOptions ,gridPane, eventsField, backButton);


        // Show events
        showEvent(1);
    }

    void showEvent(int compare) {
        List<Event> list = server.getAllEvents();
        if(compare==1) list.sort(Comparator.comparing(Event::getName));
        else if(compare==2) list.sort(Comparator.comparing(Event::getCreationDate));
        else if(compare==3) list.sort(Comparator.comparing(Event::getCreationDate));

        for (Event event : list) {
            HBox eventEntry = new HBox();
            eventEntry.setSpacing(15);

            Button openPage = openOverview(event);
            eventEntry.getChildren().add(openPage);
            Button deleteEvent = deleteEvent(event);
            eventEntry.getChildren().add(deleteEvent);
            Button getJSON = getJSON(event);
            eventEntry.getChildren().add(getJSON);
            eventsField.getChildren().add(eventEntry);
        }
    }

    private Button getJSON(Event event) {
        Button get = new Button("Download");
        get.setOnAction(e -> {

        });
        return get;
    }

    private Button deleteEvent(Event event) {
        Button delete = new Button("Delete");
        delete.setOnAction(e -> {
            //eventsField.getChildren().clear();
            server.deleteEvent(event.getCode());
            eventsField.getChildren().clear();
            showEvent(2);
        });
        return delete;
    }

    private Button openOverview(Event event) {
        Button openPage = new Button(event.getName());
        openPage.setOnAction(e -> {
            eventOverview = new EventOverview(main, event);
            main.getPrimaryStage().setScene(eventOverview.getScene());
        });

        return openPage;
    }

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }
}
