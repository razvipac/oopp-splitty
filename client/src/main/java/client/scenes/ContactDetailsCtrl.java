package client.scenes;

import client.interfaces.DataBasedSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import commons.dto.ParticipantDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class ContactDetailsCtrl implements DataBasedSceneController<EventDTO> {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @Inject
    private ControllerUtils controllerUtils;

    private EventDTO event;

    @FXML
    private TextField boxName;
    @FXML
    private TextField boxEmail;
    @FXML
    private TextField boxIban;
    @FXML
    private TextField boxBic;
    @FXML
    private Text errorText;

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
     * Initialize the scene
     * @param event data to populate the scene with
     */
    public void initialize(EventDTO event) {
        this.event = event;
        errorText.setText("");
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
     * Adds the given Participant to the server, and displays an alert box with the outcome.
     * @param p The (validated) participant to add
     */
    @FXML
    private void addParticipantToServer(ParticipantDTO p) {
        boolean success = serverUtils.addParticipant(p, event.code());
        if(success) {
            Alert confirmation = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                    "Success", "Participant Added Successfully",
                    p.name() + " has been added to the event");
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        }
        else {
            Alert alert = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    "Error", "Adding Participant Failed",
                    "The participant has not been added due to an error. Please try again");
            alert.showAndWait();
        }

        goBack();
    }

    @FXML
    private void goBack() {
        mainCtrl.showEventOverview(event);
    }

    /**
     * Checks if the user-inputted Strings are valid.
     */
    @FXML
    private boolean formIsValid() {
        if(boxName.getText().isEmpty()) {
            errorText.setText("Please fill in the name field");
            return false;
        }

        String regexEmail = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(inputIsInvalid(boxEmail.getText(), regexEmail, "Please enter a valid email"))
            return false;

        // NL12 XXXX 0123 4567 89
        String regexIban = "^NL\\d{2}\\s[A-Z0-9]{4}\\s\\d{4}\\s\\d{4}\\s\\d{2}$";
        if(inputIsInvalid(boxIban.getText(), regexIban, "Please enter a valid IBAN"))
            return false;

        // XXXXXXXX
        String regexBic = "\\b[A-Z0-9]{8}\\b";
        return !inputIsInvalid(boxBic.getText(), regexBic, "Please enter a valid BIC");
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

    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
    }

    @FXML
    private void ok(){
        if(formIsValid())
            addParticipantToServer(new ParticipantDTO(boxName.getText(),
                    boxEmail.getText(), boxIban.getText(), boxBic.getText()));
    }

    public void setErrorText(){
        this.errorText = new Text();
    }
}
