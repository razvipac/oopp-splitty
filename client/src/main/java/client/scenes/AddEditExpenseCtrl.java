package client.scenes;

import client.interfaces.DualDataBasedSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import commons.dto.ExpenseDTO;
import commons.dto.ParticipantDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditExpenseCtrl implements DualDataBasedSceneController<EventDTO, ExpenseDTO> {

    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;

    @Inject
    private ControllerUtils controllerUtils;

    private EventDTO event;
    private ExpenseDTO expense;
    private List<ParticipantDTO> participants;
    private Map<String, ParticipantDTO> participantMap;

    @FXML
    private Text header;
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
     * @param event Event instance to populate the UI with
     * @param expense Expense to edit. If user wants to add, this can be null.
     */
    public void initialize(EventDTO event, ExpenseDTO expense) {
        // If user is adding, expense is null
        this.expense = expense;
        this.event = event;
        this.participants = serverUtils.getParticipants(event.code());

        controllerUtils.bindComboBoxForKeyboardInput(whoPaidDropdown);
        controllerUtils.bindComboBoxForKeyboardInput(currencyDropdown);

        refresh();

        serverUtils.registerForWebSocketUpdatesOnParticipant(event.code(), p -> Platform.runLater(this::refresh));
    }

    /**
     * Refresh the page. Clears text fields and adjusts title based on if the user is
     * adding or editing.
     */
    private void refresh(){
        this.participants = serverUtils.getParticipants(event.code());

        errorText.setText("");
        whatForField.clear();
        whenPicker.setValue(null);
        howMuchField.clear();
        currencyDropdown.getSelectionModel().selectFirst();
        expenseTypeField.clear();

        refreshWhoPaidDropdown();
        refreshParticipantContainer();

        // Sets header based on if user is adding/editing
        if(expense == null) {
            header.setText("Add Expense");
        }
        else {
            header.setText("Edit Expense");
        }
    }

    /**
     * Refreshes the whoPaidDropdown ComboBox. Adds all participants. If the user is editing,
     * it auto selects the expense's participant and disables the box.
     */
    private void refreshWhoPaidDropdown() {
        participantMap = new HashMap<>();
        whoPaidDropdown.getItems().clear();
        this.participants = serverUtils.getParticipants(event.code());

        for (ParticipantDTO p : participants) {
            String name = p.name();
            whoPaidDropdown.getItems().add(name);
            participantMap.put(name, p);
        }

        if(expense != null) whoPaidDropdown.getSelectionModel().select(expense.paidByName());
        whoPaidDropdown.setDisable(expense != null);
    }

    /**
     * Refreshes the container for participants. Adds all participants.
     */
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
        if (whoPaidDropdown.getValue() == null || whoPaidDropdown.getValue().isEmpty()) {
            errorText.setText("Please select the participant who paid for this expense");
            return false;
        }

        // Only for Adding Expense
        if(expense == null) {
            if (howMuchField.getText().isEmpty()) {
                errorText.setText("Please fill in the price of the expense");
                return false;
            }

            if (whatForField.getText().isEmpty()) {
                errorText.setText("Please enter what the expense was for");
                return false;
            }
        }
        // Only for Editing Expense
        else {
            if(howMuchField.getText().isEmpty() && whatForField.getText().isEmpty()
                && whenPicker.getValue() == null) {
                errorText.setText("Enter at least one field to edit");
                return false;
            }
        }

        // check if price is entered and a valid number (not negative and no decimals)
        if(!howMuchField.getText().isEmpty()) {
            try {
                int price = Integer.parseInt(howMuchField.getText());
                if (price < 0) {
                    errorText.setText("Price cannot be negative");
                    return false;
                }
            } catch (NumberFormatException e) {
                errorText.setText("Price must be a valid positive integer with no decimals");
                return false;
            }
        }
        else if(expense == null) {
            errorText.setText("Please enter a price");
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
                    "The expense has not been added due to an error. Please try again.");
            alert.showAndWait();
        }

        goBack();
    }

    /**
     * Updates the given Expense in the server, and displays an alert box with the outcome.
     *
     * @param e DTO with updated values
     */
    private void updateExpenseToServer(ExpenseDTO e) {
        boolean success = serverUtils.updateExpense(e, event.code());
        if (success) {
            Alert confirmation = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                    "Success", "Expense Edited Successfully",
                    "Expense has been updated successfully.");
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        } else {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    "Error", "Editing Expense Failed",
                    "The expense has not been updated due to an error. Please try again.");
            alert.showAndWait();
        }

        goBack();
    }

    /**
     * Selects all participants if the Select Everyone button is clicked.
     */
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

    /**
     * Submits the form
     */
    @FXML
    private void submit() {
        if (formIsValid()) {
            if(expense == null) {
                double price = Double.parseDouble(howMuchField.getText());
                String item = whatForField.getText();
                ParticipantDTO payer = participantMap.get(whoPaidDropdown.getValue());
                LocalDate date = whenPicker.getValue();
                ExpenseDTO expenseDTO = new ExpenseDTO(null, price, item, payer.name(), date);
                addExpenseToServer(expenseDTO);
            }
            else {
                boolean confirmed = controllerUtils.createConfirmationAlert(
                        "Confirm Edit",
                        "Are you sure you want to edit this expense?");
                if (confirmed) {
                    double price = -1.0;
                    if (!howMuchField.getText().isEmpty()) price = Double.parseDouble(howMuchField.getText());
                    String item = whatForField.getText();
                    LocalDate date = whenPicker.getValue();
                    ExpenseDTO expenseDTO = new ExpenseDTO(expense.id(), price, item,
                            expense.paidByName(), date);
                    updateExpenseToServer(expenseDTO);
                }
            }
        }
    }

    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        if (keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.ENTER) submit();
    }
}
