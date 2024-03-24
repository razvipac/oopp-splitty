package client.scenes;

import client.Main;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AddEditExpense {

    private Stage window;
    private Scene scene;
    private final Main main;
    private final Event event;

    private List<Participant> participants;
    private ArrayList<Expense> addEditExpenseList;

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param main scene of the main class
     * @param event the event to add to
     */
    public AddEditExpense(Main main, Event event, List<Participant> participants) {
        this.main = main;
        this.event = event;
        // TODO: handle empty participants list
        this.participants = participants;

        if(event == null) createSceneNoEvent();
        else createSceneAddEditExpense();
    }

    /**
     * Creates the scene for if there is no event found
     */
    private void createSceneNoEvent() {
        VBox layout = new VBox(5);
        Text noEventText = new Text("No event found");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> main.getPrimaryStage().setScene(main.getMainScene()));

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(noEventText, backButton);
        layout.setPadding(new Insets(20));

        scene = new Scene(layout, 340, 280);
    }

    /**
     * Creates the GUI for the Add/Edit Expense
     */
    public void createSceneAddEditExpense() {
        // Create a title for the scene and set its font
        Text title = new Text("Add/Edit Expense");
        title.setFont(Font.font("Arial", FontWeight.BOLD , 20));

        // Initialize a GridPane for the layout with specific gaps and padding
        GridPane layout = new GridPane();
        layout.setHgap(10); // Horizontal gap between grid cells
        layout.setVgap(10); // Vertical gap between grid cells
        layout.setPadding(new Insets(20, 20, 20, 20)); // Padding around the grid
        layout.add(title, 0, 0); // Add the title to the layout at position (0,0)

        // Create a label and a dropdown for "Who paid?" field
        Label whoPaidLabel = new Label("Who paid?");
        ComboBox<String> whoPaidDropdown = new ComboBox<>();
        // Populate the dropdown with names from the expense list
        for (Participant p : participants) {
            String name = p.getName();
            whoPaidDropdown.getItems().add(name);
        }
        // Add the label and dropdown to the layout
        layout.add(whoPaidLabel, 0, 1);
        layout.add(whoPaidDropdown, 1, 1);

        // Create a label and a text field for "What for?" field and add them to the layout
        Label whatForLabel = new Label("What for?");
        TextField whatForField = new TextField();
        layout.add(whatForLabel, 0, 2);
        layout.add(whatForField, 1, 2);

        // Create a label and a text field for "How much?" field and add them to the layout
        Label howMuchLabel = new Label("How much?");
        TextField howMuchField = new TextField();
        layout.add(howMuchLabel, 0, 3);
        layout.add(howMuchField, 1, 3);

        // Create a label and a dropdown for "Currency" field and add them to the layout
        Label currencyLabel = new Label("Currency");
        ComboBox<String> currencyDropdown = new ComboBox<>();
        currencyDropdown.getItems().addAll("USD", "EUR", "GBP"); // Add currencies to the dropdown
        layout.add(currencyLabel, 2, 3);
        layout.add(currencyDropdown, 3, 3);

        // Create a label and a date picker for "When?" field and add them to the layout
        Label whenLabel = new Label("When?");
        DatePicker whenPicker = new DatePicker();
        layout.add(whenLabel, 0, 4);
        layout.add(whenPicker, 1, 4);

        // Create a label and radio buttons for "How to Split?" field and add them to the layout
        Label howToSplitLabel = new Label("How to Split?");
        RadioButton equallyButton = new RadioButton("Equally Between Everybody");
        RadioButton somePeopleButton = new RadioButton("Only Some People");
        ToggleGroup group = new ToggleGroup(); // Group the radio buttons
        equallyButton.setToggleGroup(group);
        somePeopleButton.setToggleGroup(group);
        layout.add(howToSplitLabel, 0, 5);
        layout.add(equallyButton, 1, 5);
        layout.add(somePeopleButton, 1, 6);

        // Create a container for checkboxes
        VBox checkboxContainer = new VBox();
        checkboxContainer.setAlignment(Pos.CENTER);
        // Create a checkbox for each participant and add it to the container
        for (Participant p : participants) {
            String name = p.getName();
            CheckBox participantCheckbox = new CheckBox(name);
            participantCheckbox.setPadding(new Insets(2, 2, 2, 2));
            checkboxContainer.getChildren().add(participantCheckbox);
        }
        layout.add(checkboxContainer, 1, 7); // Add the container to the layout

        // Create a label and a text field for "Expense Type" field and add them to the layout
        Label expenseTypeLabel = new Label("Expense Type");
        TextField expenseTypeField = new TextField();
        layout.add(expenseTypeLabel, 0, 8);
        layout.add(expenseTypeField, 1, 8);

        // Create "Abort" and "Add" buttons and add them to the layout
        Button abortButton = new Button("Abort");
        abortButton.setOnAction(e -> closeAlertBox());
        Button addButton = new Button("Add");
        layout.add(abortButton, 0, 9);
        layout.add(addButton, 1, 9);

        // Create a scene with the layout and set its size
        scene = new Scene(layout, 500, 600);
    }

    /**
     * Displays a modal alert box for adding a participant.
     */
    public void displayAlertBox() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Expense");
        window.setScene(scene);
        window.setOnCloseRequest(e -> closeAlertBox());
        window.showAndWait();
    }

    /**
     * Closes the modal alert box and clears up the input fields.
     */
    public void closeAlertBox() {
        window.close();
    }

}
