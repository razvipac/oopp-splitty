package client.scenes;

import client.LanguageManager;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
    @FXML
    private Label whoPaid;
    @FXML
    private Label whatFor;
    @FXML
    private Label when;
    @FXML
    private Label howMuch;
    @FXML
    private Label expenseType;
    @FXML
    private Label splitBetween;
    @FXML
    private Button everyoneButton;
    @FXML
    private Text required;
    @FXML
    private Button addButton;
    @FXML
    private Button abortButton;

    @Inject
    private LanguageManager lm;
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
     * Initializes the scene
     * @param location passed URL location
     * @param resources passed ResourceBundle
     */
    public void initialize(URL location, ResourceBundle resources) {
        controllerUtils.bindComboBoxForKeyboardInput(whoPaidDropdown);
        controllerUtils.bindComboBoxForKeyboardInput(currencyDropdown);
    }

    /**
     * Refresh the page. Clears text fields and adjusts title based on if the user is
     * adding or editing.
     * @param event event corresponding to this scene
     * @param expense expense to modify/delete or null otherwise
     */
    public void refresh(EventDTO event, ExpenseDTO expense){
        this.expense = expense;
        this.event = event;
        this.participants = serverUtils.getParticipants(event.code());

        serverUtils.registerForWebSocketUpdatesOnParticipant(event.code(), event.code(), p -> {
            Platform.runLater(() -> refresh(this.event, this.expense));
        });

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
            header.setText(lm.get("Add Expense"));
        }
        else {
            header.setText(lm.get("Edit Expense"));
            whatForField.setText(expense.item());
            howMuchField.setText(expense.price().toString());
            whenPicker.setValue(expense.date());
        }
        setLanguageForAllAddEditExpenseCtrl();
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
            participantCheckbox.setDisable(true);
            checkboxContainer.getChildren().add(participantCheckbox);
        }
    }

    /**
     * Checks if the user-inputted form is valid.
     */
    private boolean formIsValid() {
        if (whoPaidDropdown.getValue() == null || whoPaidDropdown.getValue().isEmpty()) {
            errorText.setText(lm.get("Please select the participant who paid for this expense"));
            return false;
        }

        // Only for Adding Expense
        if(expense == null) {
            if (howMuchField.getText().isEmpty()) {
                errorText.setText(lm.get("Please fill in the price of the expense"));
                return false;
            }

            if (whatForField.getText().isEmpty()) {
                errorText.setText(lm.get("Please enter what the expense was for"));
                return false;
            }
        }
        // Only for Editing Expense
        else {
            if(howMuchField.getText().isEmpty() && whatForField.getText().isEmpty()
                && whenPicker.getValue() == null) {
                errorText.setText(lm.get("Enter at least one field to edit"));
                return false;
            }
        }

        // check if price is entered and a valid number (not negative and maximum of 2 decimal places)
        if (!howMuchField.getText().isEmpty()) {
            try {
                double price = Double.parseDouble(howMuchField.getText());

                // Check if the price is negative
                if (price < 0) {
                    errorText.setText(lm.get("Price cannot be negative"));
                    return false;
                }

                // Check if the price has more than 2 decimal places
                String[] priceParts = howMuchField.getText().split("\\.");
                if (priceParts.length > 1 && priceParts[1].length() > 2) {
                    errorText.setText("Price must have a maximum of 2 decimal places");
                    return false;
                }

            } catch (NumberFormatException e) {
                errorText.setText(lm.get("Price must be a valid positive integer with no decimals"));
                return false;
            }
        }
        else if(expense == null) {
            errorText.setText(lm.get("Please enter a price"));
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
                    lm.get("Success"), lm.get("Expense Added Successfully"),
                    lm.get("Expense has been added to the event"));
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        } else {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"), lm.get("Adding Expense Failed"),
                    lm.get("The expense has not been added due to an error. Please try again."));
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
                    lm.get("Success"), lm.get("Expense Edited Successfully"),
                    lm.get("Expense has been updated successfully."));
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        } else {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"), lm.get("Editing Expense Failed"),
                    lm.get("The expense has not been updated due to an error. Please try again."));
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
        serverUtils.disconnectWSSession(event.code());
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
                        lm.get("Confirm Edit"),
                        lm.get("Are you sure you want to edit this expense?"));
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

    /**
     * Sets the text in correct language
     */
    public void setLanguageForAllAddEditExpenseCtrl(){
        whoPaid.setText(lm.get("Who paid?*"));
        whatFor.setText(lm.get( "What for?*"));
        whoPaidDropdown.setPromptText(lm.get("Choose..."));
        howMuch.setText(lm.get("How much?*"));
        when.setText(lm.get("When?"));
        expenseType.setText(lm.get("Expense Type"));
        whatForField.setPromptText(lm.get("Drinks"));
        expenseTypeField.setPromptText(lm.get("food, restaurant"));
        required.setText(lm.get( "*required"));
        splitBetween.setText(lm.get("Split between:"));
        everyoneButton.setText(lm.get("Select Everyone"));
        abortButton.setText(lm.get("Abort"));
        addButton.setText(lm.get("OK"));
    }
}
