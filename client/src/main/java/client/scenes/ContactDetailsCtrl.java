package client.scenes;

import client.interfaces.DataBasedSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ContactDetailsCtrl implements DataBasedSceneController<EventDTO> {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
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

    public void initialize(EventDTO event) {
        this.event = event;
    }

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

    @FXML
    private void addParticipant() {
        // Handle adding participant to server
    }

    @FXML
    private void goBack() {
        mainCtrl.showEventOverview(event);
    }

    @FXML
    private boolean formIsValid() {
        // Validate form data
        return true; // Placeholder return value
    }

    @FXML
    private void inputIsInvalid() {
        // Handle invalid input
    }

}
