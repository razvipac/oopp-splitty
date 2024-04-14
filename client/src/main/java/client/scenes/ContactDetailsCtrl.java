package client.scenes;

import client.LanguageManager;
import client.interfaces.DataBasedSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import commons.dto.ParticipantDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContactDetailsCtrl implements DataBasedSceneController<EventDTO> {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @Inject
    private ControllerUtils controllerUtils;
    public enum View {
        ADD, EDIT, DELETE
    }
    private View currentView;

    private EventDTO event;

    @FXML
    private ToggleGroup toggleView;
    @FXML
    private ToggleButton addButton;
    @FXML
    private TextField boxName;
    @FXML
    private ComboBox<String> comboBoxName;
    @FXML
    private TextField boxEmail;
    @FXML
    private TextField boxIban;
    @FXML
    private TextField boxBic;
    @FXML
    private Text errorText;
    @FXML
    private Text manageParticipants;
    @FXML
    private ToggleButton deleteButton;
    @FXML
    private ToggleButton editButton;
    @FXML
    private Button okButton;
    @FXML
    private Button abortButton;
    @FXML
    private Text required;
    @FXML
    private Label labelName;
    @FXML
    private Label labelEmail;
    @FXML
    private Label labelIBAN;
    @FXML
    private Label labelBIC;

    @Inject
    private LanguageManager lm;
    private List<ParticipantDTO> participants;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     */
    @Inject
    public ContactDetailsCtrl(MainCtrl mainCtrl, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * Initializes the scene
     * @param location passed URL location
     * @param resources passed ResourceBundle
     */
    public void initialize(URL location, ResourceBundle resources) {
        participants = new ArrayList<>();
        controllerUtils.bindComboBoxForKeyboardInput(comboBoxName);

        // IBAN: sets character limit to 18
        boxIban.setTextFormatter(getTextFormatterIBAN());
        // properly format the IBAN
        boxIban.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                formatIban();
            }
        } );

        // Name & Email: sets character limit to 255
        boxName.setTextFormatter(getTextFormatterCharacterLimit(255));
        boxEmail.setTextFormatter(getTextFormatterCharacterLimit(255));

        // BIC: sets character limit to 8
        boxBic.setTextFormatter(getTextFormatterCharacterLimit(8));
        // convert to uppercase
        boxBic.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                boxBic.setText(boxBic.getText().toUpperCase());
            }
        } );

        comboBoxName.setOnAction(actionEvent -> autoFillForm());

        // listen to toggleView changes
        toggleViewListen();
    }

    /**
     * Refreshes the page, changes everything to current values.
     * @param event event to which the scene corresponds
     */
    public void refresh(EventDTO event) {
        this.event = event;

        errorText.setText("");
        boxName.clear();
        boxEmail.clear();
        boxIban.clear();
        boxBic.clear();
        toggleView.selectToggle(addButton);
        setView(View.ADD);  // set to ADD by default

        participants = serverUtils.getParticipants(event.code());
        comboBoxName.getItems().setAll(
                participants
                        .stream()
                        .map(ParticipantDTO::name)
                        .toList()
        );
        setLanguageForAllContactDetailsCtrl();
    }

    /**
     * Creates and gets TextFormatter with character limit, specifically for IBAN
     * @return TextFormatter Object
     */
    private TextFormatter<String> getTextFormatterIBAN() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            int nonSpaceCount = (int) newText.chars().filter(c -> c != ' ').count();
            if (nonSpaceCount <= 18) {
                return change;
            } else {
                return null;
            }
        });
    }

    /**
     * Creates a TextFormatter with a character limit
     * @param limit The limit on the number of characters
     * @return TextFormatter Object
     */
    private TextFormatter<String> getTextFormatterCharacterLimit(int limit) {
        if(limit < 0) limit = 0;
        int finalLimit = limit;
        return new TextFormatter<>(change -> {
            if(change.getControlNewText().length() > finalLimit) return null;
            else return change;
        });
    }

    /**
     * Adds listener to toggleView. Sets currentView based on the ToggleGroups selected button.
     */
    private void toggleViewListen() {
        System.out.print("lm is null: ");
        System.out.println(lm == null);
        toggleView.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                ToggleButton selected = (ToggleButton) newValue;
                String selectedText = selected.getText();
                if(selectedText.equals("Add") || selectedText.equals("Adauga") || selectedText.equals("Toevoegen")) {
                    setView(View.ADD);
                    boxEmail.setText("");
                    boxIban.setText("");
                    boxBic.setText("");
                }
                else if(selectedText.equals("Edit") || selectedText.equals("Bewerk")) {
                    setView(View.EDIT);
                    autoFillForm();
                }
                else if (selectedText.equals("Delete")
                        || selectedText.equals("Sterge")
                        || selectedText.equals("Verwijderen")) {
                    setView(View.DELETE);
                    autoFillForm();
                }
            }
        }));
    }

    /**
     * Changes the currentView and the corresponding elements of the page.
     * Iff view equals ADD, boxName is visible and comboBoxName is invisible.
     * Otherwise, the opposite is true.
     * @param view The view to compare to.
     */
    public void setView(View view) {
        currentView = view;

        // visible if view equals ADD
        boxName.setVisible(view == View.ADD);
        // visible if view equals EDIT or DELETE
        comboBoxName.setVisible(view != View.ADD);
        // visible if view equals DELETE
        boxEmail.setDisable(view == View.DELETE);
        boxIban.setDisable(view == View.DELETE);
        boxBic.setDisable(view == View.DELETE);
    }

    /**
     * Formats the user-inputted IBAN by:
     *  a) Adding spaces to the right places
     *  b) Converting all letters to uppercase
     * The format of IBAN used is 'NL12 XXXX 0123 4567 89'
     */
    @FXML
    private void formatIban() {
        // delete all spaces
        String formattedIban = boxIban.getText().replaceAll("\\s+", "");
        // make uppercase
        formattedIban = formattedIban.toUpperCase();

        // add spaces at right places
        if (formattedIban.length() > 2 && formattedIban.length() <= 18) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < formattedIban.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    sb.append(" ");
                }
                sb.append(formattedIban.charAt(i));
            }
            formattedIban = sb.toString();
        }

        // change if not equal to formattedIban already
        if (!boxIban.getText().equals(formattedIban)) {
            boxIban.setText(formattedIban);
        }
    }

    /**
     * Adds the given participant to the server, and displays an alert box with the outcome.
     * @param participant The (validated) participant to add
     */
    @FXML
    private void addParticipantToServer(ParticipantDTO participant) {
        boolean success = serverUtils.addParticipant(participant, event.code());
        if(success) {
            Alert confirmation = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                    lm.get("Success"), lm.get("Participant Added Successfully"),
                    participant.name() + lm.get(" has been added to the event"));
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        }
        else {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"), lm.get("Adding Participant Failed"),
                    lm.get("The participant has not been added due to an error. Please try again."));
            alert.showAndWait();
        }

        goBack();
    }

    /**
     * Updates participant in the server, and displays an alert box with the outcome.
     * @param body ParticipantDTO containing the values that need to be updated
     * @param name Name of Participant to be updated
     */
    @FXML
    private void updateParticipantToServer(ParticipantDTO body, String name) {
        boolean success = serverUtils.updateParticipant(body, event.code(), name);
        if(success) {
            Alert confirmation = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                    lm.get("Success"), lm.get("Participant Updated Successfully"),
                    name + lm.get(" has been updated successfully."));
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        }
        else {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"), lm.get("Updating Participant Failed"),
                    name + lm.get(" has not been updated due to an error. Please try again."));
            alert.showAndWait();
        }

        goBack();
    }

    /**
     * Deletes participant from server, and displays an alert box with the outcome.
     * @param name Name of participant to delete.
     */
    @FXML
    private void deleteParticipantFromServer(String name) {
        boolean success = serverUtils.deleteParticipant(event.code(), name);
        if(success) {
            Alert confirmation = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                    lm.get("Success"), lm.get("Participant Deleted Successfully"),
                    name + lm.get(" has been deleted from this event."));
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        }
        else {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"), lm.get("Deleting Participant Failed"),
                    name + lm.get(" has not been deleted due to an error. Please try again."));
            alert.showAndWait();
        }

        goBack();
    }

    /**
     * Checks if the user-inputted Strings are valid.
     */
    @FXML
    private boolean formIsValid() {
        if(currentView == View.EDIT || currentView == View.DELETE) {
            if(comboBoxName.getSelectionModel().getSelectedItem() == null) {
                errorText.setText(lm.get("Please select participant"));
                return false;
            }
        }

        if(currentView == View.EDIT) {
            if(boxEmail.getText().isEmpty() && boxIban.getText().isEmpty()
                && boxBic.getText().isEmpty()) {
                errorText.setText(lm.get("Enter at least one field to edit"));
            }
        }

        if(currentView == View.ADD
                && boxName.getText().isEmpty()) {
            errorText.setText(lm.get("Please fill in the name field"));
            return false;
        }

        String regexEmail = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(inputIsInvalid(boxEmail.getText(), regexEmail, "Please enter a valid email"))
            return false;

        // NL12 XXXX 0123 4567 89
        String regexIban = "^NL\\d{2}\\s[A-Z0-9]{4}\\s\\d{4}\\s\\d{4}\\s\\d{2}$";
        if(inputIsInvalid(boxIban.getText(), regexIban, lm.get("Please enter a valid IBAN")))
            return false;

        // XXXXXXXX
        String regexBic = "\\b[A-Z0-9]{8}\\b";
        return !inputIsInvalid(boxBic.getText(), regexBic, lm.get("Please enter a valid BIC"));
    }

    /**
     * Checks whether the given input is invalid (not empty and doesn't match regex), and displays
     * an error message if it is.
     * @param input Input String to check
     * @param regex Specifies pattern that decides if it's invalid
     * @param errorMessage The error message to set errorText to
     * @return true iff input is invalid, false iff it's valid
     */
    @FXML
    private boolean inputIsInvalid(String input, String regex, String errorMessage) {
        if(!(input.isEmpty()) && !(input.matches(regex))) {
            errorText.setText(errorMessage);
            return true;
        }
        else return false;
    }

    /**
     * Keyboard shortcut. Pressing ESC brings you back to the previous page.
     * @param keyEvent Which keystroke occurred
     */
    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        if (keyEvent.isAltDown() && keyEvent.getCode() ==  KeyCode.ENTER) ok();
    }

    /**
     * Handles action based on currentView, only if the entered form is validated.
     */
    @FXML
    private void ok(){
        // Check if form has been filled in correctly
        if(formIsValid()) {
            switch(currentView) {
                case ADD -> addParticipantToServer(
                        new ParticipantDTO(
                                boxName.getText(),
                                boxEmail.getText(),
                                boxIban.getText(),
                                boxBic.getText()
                        )
                );

                case EDIT -> {
                    // Confirmation box
                    boolean confirmed = controllerUtils.createConfirmationAlert(
                            lm.get("Confirm Edit"),
                            lm.get("Are you sure you want to edit this participant?"));

                    if (confirmed) {
                        updateParticipantToServer(
                                new ParticipantDTO(
                                        "",     // name can't be changed
                                        boxEmail.getText(),
                                        boxIban.getText(),
                                        boxBic.getText()
                                ),
                                comboBoxName.getSelectionModel().getSelectedItem()
                        );
                    }
                }

                case DELETE -> {
                    // Confirmation box
                    boolean confirmed = controllerUtils.createConfirmationAlert(
                            lm.get("Confirm Delete"),
                            lm.get("Are you sure you want to delete this participant?"));

                    if (confirmed) {
                        deleteParticipantFromServer(
                                comboBoxName.getSelectionModel().getSelectedItem()
                        );
                    }
                }

            }

        }
    }

    /**
     * Goes back to EventOverview
     */
    @FXML
    private void goBack() {
        mainCtrl.showEventOverview(event);
    }

    /**
     * Initializes the errorText instance variable by creating a new Text object.
     * This method is typically called to reset or clear the errorText.
     */
    public void setErrorText(){
        this.errorText = new Text();
    }

    /**
     * Sets the language for all elements in the ContactDetailsCtrl scene.
     */
    public void setLanguageForAllContactDetailsCtrl() {
        addButton.setText(lm.get("Add"));
        manageParticipants.setText(lm.get("Manage Participants"));
        deleteButton.setText(lm.get("Delete"));
        editButton.setText(lm.get("Edit"));
        okButton.setText(lm.get("OK"));
        abortButton.setText(lm.get("Abort"));
        required.setText(lm.get("*required"));
        labelName.setText(lm.get("Name"));
        labelEmail.setText(lm.get("Email"));
        labelIBAN.setText("IBAN");
        labelBIC.setText("BIC");
        comboBoxName.setPromptText(lm.get("Choose..."));
    }

    private void autoFillForm() {
        String selection = comboBoxName.getSelectionModel().getSelectedItem();
        if (selection != null && (currentView == View.EDIT || currentView == View.DELETE)) {
            Optional<ParticipantDTO> found = participants.stream()
                    .filter(participantDTO -> participantDTO.name().equals(selection))
                    .findFirst();

            if (found.isPresent()) {
                boxEmail.setText(found.get().email());
                boxIban.setText(found.get().iban());
                boxBic.setText(found.get().bic());
            }
        }
    }

}
