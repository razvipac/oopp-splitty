package client.scenes;

import client.interfaces.DataBasedSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import commons.dto.ExpenseDTO;
import commons.dto.ParticipantDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditExpenseCtrl implements DataBasedSceneController<EventDTO> {

    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;

    @Inject
    private ControllerUtils controllerUtils;

    private EventDTO event;
    private List<ParticipantDTO> participants;
    private ArrayList<ExpenseDTO> addEditExpenseList;
    private Map<String, ParticipantDTO> participantMap;
    private boolean everyoneSelected;

    @FXML
    private ComboBox<String> whoPaidDropdown;
    @FXML
    private TextField whatForField;
    @FXML
    private TextField howMuchField;
    @FXML
    private ComboBox<String> currencyDropdown;
    @FXML
    private DatePicker whenPicker;
    @FXML
    private TextField expenseTypeField;
    @FXML
    private Button everyoneButton;
    @FXML
    private Text errorText;
    @FXML
    private VBox checkboxContainer;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     *
     * @param mainCtrl    scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     */
    @Inject
    public AddEditExpenseCtrl(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * Generate an ui from the given object instance
     *
     * @param event event
     */
    public void initialize(EventDTO event) {
        this.event = event;
        this.participants = serverUtils.getParticipants(event.code());

        currencyDropdown.setItems(FXCollections.observableArrayList("EUR", "USD", "GBP"));
        currencyDropdown.getSelectionModel().selectFirst();

        errorText.setText("");

        refresh();

        serverUtils.registerForWebSocketUpdatesOnParticipant(event.code(), p -> Platform.runLater(this::refresh));
    }

    private void refresh(){
        this.participants = serverUtils.getParticipants(event.code());

        refreshWhoPaidDropdown();
        refreshParticipantContainer();
    }

    private void refreshWhoPaidDropdown() {
        participantMap = new HashMap<>();
        whoPaidDropdown.getItems().clear();
        this.participants = serverUtils.getParticipants(event.code());

        for (ParticipantDTO p : participants) {
            String name = p.name();
            whoPaidDropdown.getItems().add(name);
            participantMap.put(name, p);
        }
    }

    private void refreshParticipantContainer() {
        checkboxContainer.getChildren().clear();
        this.participants = serverUtils.getParticipants(event.code());

        for (ParticipantDTO p : participants) {
            String name = p.name();
            CheckBox participantCheckbox = new CheckBox(name);
            checkboxContainer.getChildren().add(participantCheckbox);
        }
    }

    /**
     * Checks if the user-inputted form is valid.
     */
    private boolean formIsValid() {
        // TODO: add validation for the optional fields

        if (whoPaidDropdown.getValue() == null || whoPaidDropdown.getValue().isEmpty()) {
            errorText.setText("Please select the participant who paid for this expense");
            return false;
        }

        if (howMuchField.getText().isEmpty()) {
            errorText.setText("Please fill in the price of the expense");
            return false;
        }

        if (whatForField.getText().isEmpty()) {
            errorText.setText("Please enter what the expense was for");
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
     *
     * @param e The (validated) expense to add
     */
    private void addExpenseToServer(ExpenseDTO e) {
        boolean success = serverUtils.addExpense(e, event.code());
        if (success) {
            Alert confirmation = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                    "Success", "Expense Added Successfully",
                    "Expense has been added to the event");
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        } else {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    "Error", "Adding Expense Failed",
                    "The expense has not been added due to an error. Please try again");
            alert.showAndWait();
        }

        goBack();
    }

    @FXML private void selectEveryone() {
        for(Node node : checkboxContainer.getChildren()) {
            CheckBox checkBox = (CheckBox) node;
            checkBox.setSelected(true);
        }
    }

    /**
     * Back button action
     */
    @FXML
    private void goBack() {
        mainCtrl.showEventOverview(event);
    }

    @FXML
    private void submit() {
        if (formIsValid()) {
            int price = Integer.parseInt(howMuchField.getText());
            String item = whatForField.getText();
            ParticipantDTO payer = participantMap.get(whoPaidDropdown.getValue());
            LocalDate date = whenPicker.getValue();
            ExpenseDTO expense = new ExpenseDTO(null, price, item, payer.name(), date);
            addExpenseToServer(expense);
        }
    }
}
