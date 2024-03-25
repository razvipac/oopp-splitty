package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditExpense {

    private Stage window;
    private Scene scene;
    private final Main main;
    private final Event event;

    private final ServerUtils server = ServerUtils.getServerUtils();

    private List<Participant> participants;

    private final ComboBox<String> whoPaidDropdown = new ComboBox<>();
    private final TextField whatForField = new TextField();
    private final TextField howMuchField = new TextField();
    private Text errorText;

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
     * @param participants list of participants
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
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // Horizontal gap between grid cells
        gridPane.setVgap(10); // Vertical gap between grid cells
        errorText = new Text();
        errorText.setFill(Color.RED);

        // Create a label and a dropdown for "Who paid?" field
        Label whoPaidLabel = new Label("Who paid?");
        // Populate the dropdown with names from the expense list
        Map<String, Participant> participantMap = new HashMap<>();
        for (Participant p : participants) {
            String name = p.getName();
            whoPaidDropdown.getItems().add(name);
            participantMap.put(name, p);
        }
        // Add the label and dropdown to the layout
        gridPane.add(whoPaidLabel, 0, 1);
        gridPane.add(whoPaidDropdown, 1, 1);

        // Create a label and a text field for "What for?" field and add them to the layout
        Label whatForLabel = new Label("What for?");
        gridPane.add(whatForLabel, 0, 2);
        gridPane.add(whatForField, 1, 2);

        // Create a label and a text field for "How much?" field and add them to the layout
        Label howMuchLabel = new Label("How much?");
        gridPane.add(howMuchLabel, 0, 3);
        gridPane.add(howMuchField, 1, 3);

        // Create a label and a dropdown for "Currency" field and add them to the layout
        Label currencyLabel = new Label("Currency");
        ComboBox<String> currencyDropdown = new ComboBox<>();
        currencyDropdown.getItems().addAll("EUR", "USD", "GBP"); // Add currencies to the dropdown
        currencyDropdown.getSelectionModel().selectFirst();
        gridPane.add(currencyLabel, 2, 3);
        gridPane.add(currencyDropdown, 3, 3);

        // Create a label and a date picker for "When?" field and add them to the layout
        Label whenLabel = new Label("When?");
        DatePicker whenPicker = new DatePicker();
        gridPane.add(whenLabel, 0, 4);
        gridPane.add(whenPicker, 1, 4);

        // Create a label and radio buttons for "How to Split?" field and add them to the layout
        Label howToSplitLabel = new Label("How to Split?");
        RadioButton equallyButton = new RadioButton("Equally Between Everybody");
        equallyButton.setSelected(true);
        RadioButton somePeopleButton = new RadioButton("Only Some People");
        ToggleGroup radioGroup = new ToggleGroup(); // Group the radio buttons
        equallyButton.setToggleGroup(radioGroup);
        somePeopleButton.setToggleGroup(radioGroup);
        gridPane.add(howToSplitLabel, 0, 5);
        gridPane.add(equallyButton, 1, 5);
        gridPane.add(somePeopleButton, 1, 6);

        // Create a container for checkboxes
        VBox checkboxContainer = new VBox();
        checkboxContainer.setAlignment(Pos.CENTER);
        // Create a checkbox for each participant and add it to the container
        for (Participant p : participants) {
            String name = p.getName();
            CheckBox participantCheckbox = new CheckBox(name);
            participantCheckbox.setDisable(true);
            participantCheckbox.setPadding(new Insets(2, 2, 2, 2));
            checkboxContainer.getChildren().add(participantCheckbox);
        }
        gridPane.add(checkboxContainer, 1, 7); // Add the container to the layout

        radioGroup.selectedToggleProperty().addListener((v, oldValue, newValue) -> {
            if (newValue == somePeopleButton) {
                // Enable checkboxes when somePeopleButton is selected
                for (Node node : checkboxContainer.getChildren()) {
                    if (node instanceof CheckBox) {
                        node.setDisable(false);
                    }
                }
            } else {
                // Disable checkboxes when equallyButton is selected
                for (Node node : checkboxContainer.getChildren()) {
                    if (node instanceof CheckBox) {
                        node.setDisable(true);
                    }
                }
            }
        });

        // Create a label and a text field for "Expense Type" field and add them to the layout
        Label expenseTypeLabel = new Label("Expense Type");
        TextField expenseTypeField = new TextField();
        gridPane.add(expenseTypeLabel, 0, 8);
        gridPane.add(expenseTypeField, 1, 8);

        // Create "Abort" and "Add" buttons and add them to the layout
        Button abortButton = new Button("Abort");
        abortButton.setOnAction(e -> closeAlertBox());
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            if(formIsValid()) {
                Integer price = Integer.valueOf(howMuchField.getText());
                String item = whatForField.getText();
                Participant payer = participantMap.get(whoPaidDropdown.getValue());
                Expense expense = new Expense(price, item, payer);
                System.out.println(expense); // for testing
                addExpenseToServer(expense);
            }
        });
        gridPane.add(abortButton, 0, 9);
        gridPane.add(addButton, 1, 9);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(title, errorText, gridPane);

        // Create a scene with the layout and set its size
        scene = new Scene(layout, 500, 600);
    }

    /**
     * Checks if the user-inputted form is valid.
     */
    private boolean formIsValid() {
        // TODO: add validation for the optional fields

        if(whoPaidDropdown.getValue() == null || whoPaidDropdown.getValue().isEmpty()) {
            errorText.setText("Please select the participant who paid for this expense.");
            return false;
        }

        if(howMuchField.getText().isEmpty()) {
            errorText.setText("Please fill in the price of the expense.");
            return false;
        }

        if(whatForField.getText().isEmpty()) {
            errorText.setText("Please enter what the expense was for.");
            return false;
        }

        // check if price is a number
        try {
            int price = Integer.parseInt(howMuchField.getText());
            if (price < 0) {
                errorText.setText("Price cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            errorText.setText("Price must be a valid integer with no decimals.");
            return false;
        }

        return true;
    }

    /**
     * Adds the given Expense to the server, and displays an alert box with the outcome.
     * @param e The (validated) expense to add
     */
    private void addExpenseToServer(Expense e) {
        boolean success = server.getParticipantUtils().addExpense(e, event.getCode());
        if(success) {
            Alert confirmation = createAlert(Alert.AlertType.CONFIRMATION,
                    "Success", "Expense Added Successfully",
                    "Expense has been added to the event");
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        }
        else {
            Alert alert = createAlert(Alert.AlertType.ERROR,
                    "Error", "Adding Expense Failed",
                    "The expense has not been added due to an error. Please try again");
            alert.showAndWait();
        }

        closeAlertBox();
    }

    /**
     * Creates an alert window
     * @param type The type of alert (e.g. CONFIRMATION or ERROR)
     * @param title The title of the window
     * @param header The header of the window
     * @param content The content of the window
     * @return Alert object
     */
    private Alert createAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
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
        // TODO: reset fields when closed and reopened
        window.close();
    }

}
