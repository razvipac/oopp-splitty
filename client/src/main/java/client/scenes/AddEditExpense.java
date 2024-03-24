package client.scenes;

import client.Main;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AddEditExpense {
    private Scene scene;
    private Main main;

    private ArrayList<Expense> addEditExpenseList;

    private ComboBox<String> whoPaidDropdown;
    private TextField whatForField;
    private TextField howMuchField;
    private ComboBox<String> currencyDropdown;
    private VBox checkboxContainer;
    private TextField expenseTypeField;

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
     */
    public AddEditExpense(Main main) {
        this.main = main;
        // Data for testing purposes
        Participant john = new Participant("John",
                new Event("Abby's birthday party", "code1",
                        LocalDateTime.of(1900, 1, 1, 0, 0, 0)),
                "John@mail.com", "1234", "1234");
        Participant david = new Participant("David",
                new Event("Davidson's birthday party", "code2",
                        LocalDateTime.of(1900, 1, 1, 0, 0, 0)),
                "David@mail.com", "2341", "2341");
        Participant chris = new Participant("Chris",
                new Event("Stoffer's birthday party", "code3",
                        LocalDateTime.of(1900, 1, 1, 0, 0, 0)),
                "Chris@mail.com", "3412", "3412");
        Participant anna = new Participant("Anna",
                new Event("Belle's birthday party", "code4",
                        LocalDateTime.of(1900, 1, 1, 0, 0, 0)),
                "Anna@mail.com", "4123", "4123");

        addEditExpenseList = new ArrayList<>();
        addEditExpenseList.add(new Expense(123, "Food", john));
        addEditExpenseList.add(new Expense(34, "Drinks", chris));
        addEditExpenseList.add(new Expense(345, "Cake", anna));
        addEditExpenseList.add(new Expense(567, "Candles", david));


        createSceneAddEditExpense();
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

        // Create labels for additional fields
        Label whenLabel = new Label("When?");
        Label currencyLabel = new Label("Currency");
        Label whoPaidLabel = new Label("Who paid?");
        Label howMuchLabel = new Label("How much?");
        Label whatForLabel = new Label("What for?");
        Label howToSplitLabel = new Label("How to Split?");
        Label expenseTypeLabel = new Label("Expense Type");

        // Add labels to the layout
        layout.add(whenLabel, 0, 4);
        layout.add(currencyLabel, 2, 3);
        layout.add(whoPaidLabel, 0, 1);
        layout.add(whatForLabel, 0, 2);
        layout.add(howToSplitLabel, 0, 5);
        layout.add(expenseTypeLabel, 0, 8);

        // Create a label and a dropdown for "Who paid?" field
        whoPaidDropdown = new ComboBox<>();
        // Populate the dropdown with names from the expense list
        for (Expense expense : addEditExpenseList) {
            String name = expense.getPaidBy().getName();
            whoPaidDropdown.getItems().add(name);
        }
        layout.add(whoPaidDropdown, 1, 1);

        // Create a label and a text field for "What for?" field and add them to the layout
        whatForField = new TextField();
        layout.add(whatForField, 1, 2);

        // Create a label and a text field for "How much?" field and add them to the layout
        howMuchField = new TextField();
        layout.add(howMuchLabel, 0, 3);
        layout.add(howMuchField, 1, 3);

        // Create a dropdown for "Currency" field and add it to the layout
        currencyDropdown = new ComboBox<>();
        currencyDropdown.getItems().addAll("USD", "EUR", "GBP"); // Add currencies to the dropdown
        layout.add(currencyDropdown, 3, 3);

        // Create a date picker for "When?" field and add it to the layout
        DatePicker whenPicker = new DatePicker();
        layout.add(whenPicker, 1, 4);

        // Create radio buttons for "How to Split?" field and add them to the layout
        RadioButton equallyButton = new RadioButton("Equally Between Everybody");
        RadioButton somePeopleButton = new RadioButton("Only Some People");
        ToggleGroup group = new ToggleGroup(); // Group the radio buttons
        equallyButton.setToggleGroup(group);
        somePeopleButton.setToggleGroup(group);
        layout.add(equallyButton, 1, 5);
        layout.add(somePeopleButton, 1, 6);

        // Create a container for checkboxes
        checkboxContainer = new VBox();
        checkboxContainer.setAlignment(Pos.CENTER);
        // Create a checkbox for each participant and add it to the container
        for (Expense expense : addEditExpenseList) {
            String name = expense.getPaidBy().getName();
            CheckBox participantCheckbox = new CheckBox(name);
            participantCheckbox.setPadding(new Insets(2, 2, 2, 2));
            checkboxContainer.getChildren().add(participantCheckbox);
        }
        layout.add(checkboxContainer, 1, 7); // Add the container to the layout

        // Create a text field for "Expense Type" field and add it to the layout
        expenseTypeField = new TextField();
        layout.add(expenseTypeField, 1, 8);

        // Create "Abort" and "Add" buttons and add them to the layout
        Button abortButton = new Button("Abort");
        abortButton.setOnAction(e -> goBack(true)); // Pass true to clear fields
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> goBack(false)); // TODO : to be implemented
        layout.add(abortButton, 0, 9);
        layout.add(addButton, 1, 9);

        // Create a "Back" button, set its action to switch to the main scene and add it to layout
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack(false)); // Pass false to keep fields
        layout.add(backButton, 0, 10);

        // Create a scene with the layout and set its size
        scene = new Scene(layout, 570, 500);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goBack(false);
        });
    }

    /**
     * Navigates back to the main scene and optionally clears the form fields.
     *
     * @param clearFields if true, clears all form fields; otherwise, keeps them unchanged.
     */
    private void goBack(boolean clearFields) {
        if (clearFields) {
            clearFields();
        }
        main.getPrimaryStage().setScene(main.getMainScene());
    }

    /**
     * Clears all the form fields.
     */
    private void clearFields() {
        whoPaidDropdown.getSelectionModel().clearSelection();
        whatForField.clear();
        howMuchField.clear();
        currencyDropdown.getSelectionModel().clearSelection();
        for (Node node : checkboxContainer.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                checkBox.setSelected(false);
            }
        }
        expenseTypeField.clear();
    }

    /**
     * Displays a modal alert box for adding a participant.
     */
    public void displayAlertBox() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add/Edit Expense");
        window.setScene(scene);
        window.showAndWait();
    }

}
