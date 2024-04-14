package client.scenes;

import client.LanguageManager;
import client.interfaces.DataBasedSceneController;
import client.utils.ControllerUtils;
import client.utils.InviteUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import commons.dto.ParticipantDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.net.URL;
import java.util.ResourceBundle;

public class InvitationsCtrl implements DataBasedSceneController<EventDTO> {
    private final InviteUtils inviteUtils;

    private final MainCtrl mainCtrl; // reference to MainCtrl class
    private final ServerUtils serverUtils;

    @Inject
    private ControllerUtils controllerUtils;
    @Inject
    private LanguageManager lm;

    private EventDTO event;
    @FXML
    private Label inviteFollowing;
    @FXML
    private Button cancel;
    @FXML
    private Button sendInvites;
    @FXML
    private Label giveInviteCode;
    @FXML
    private Label eventTitleLabel;
    @FXML
    private Label eventCodeLabel;
    @FXML
    private TextField email;

    /**
     * Constructor for the InvitationsCtrl
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     * @param inviteUtils singleton
     */
    @Inject
    public InvitationsCtrl(MainCtrl mainCtrl, ServerUtils serverUtils, InviteUtils inviteUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
        this.inviteUtils = inviteUtils;
    }

    /**
     * Initializes the scene
     * @param location passed URL location
     * @param resources passed ResourceBundle
     */
    public void initialize(URL location, ResourceBundle resources) {
        setLanguageForAllInvitationsCtrl();
    }


    /**
     * Refreshes the scene with fresh data
     * @param event event to which the scene corresponds
     */
    public void refresh(EventDTO event){
        this.event = event;
        this.eventCodeLabel.setText(event.code());
        this.eventTitleLabel.setText(event.name());
        setLanguageForAllInvitationsCtrl();
    }

    @FXML
    private void goBack(){
        email.setText("");
        mainCtrl.showEventOverview(event);
    }

    @FXML
    private void sendInvites(){
        String emailString = email.getText();
        String regexEmail = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(emailString.isEmpty() || !(emailString.matches(regexEmail))) {
            Alert error = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"),
                    lm.get("Email entered incorrectly"),
                    lm.get("Please enter a valid email"));
            error.showAndWait();
            return;
        }

        System.out.println("Sending invites to" + emailString);
        String title = "Invitations sent successfully";
        String header = "Invitations were sent successfully!";
        String content = "The invitations sent successfully to: \n";
        if(lm != null){
            title = lm.get("Invitations sent successfully");
            header = lm.get("Invitations were sent successfully!");
            content = lm.get("The invitations sent successfully to: \n");
        }

        if(inviteUtils.sendInvitation(emailString, event.code())) {
            String username = emailString.substring(0, emailString.indexOf('@'));
            ParticipantDTO p = new ParticipantDTO(username, emailString, "", "");
            serverUtils.addParticipant(p, event.code());

            Alert successAlert = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                    title,
                    header,
                    content + email.getText());
            successAlert.showAndWait();
            goBack();
        }
        else {
            System.out.println("Sending invites failed");
            Alert error = controllerUtils.createAlert(Alert.AlertType.ERROR,
                    lm.get("Error"),
                    lm.get("Sending email failed"),
                    lm.get("An error has occurred. Please try again."));
            error.showAndWait();
        }
    }

    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        if (keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.ENTER) {
            sendInvites();
        }
    }

    /**
     * Handles the copying of the invitation code when the label is double-clicked.
     *
     * @param event The MouseEvent representing the double click event.
     */
    @FXML
    private void handleCopyInvitationCode(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            // Get the source of the event, which should be the invitation code label
            Label invitationCodeLabel = (Label) event.getSource();
            String invitationCode = invitationCodeLabel.getText();

            // Create a clipboard and add the invitation code to its content
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(invitationCode);
            clipboard.setContent(content);

            invitationCodeLabel.setStyle("-fx-background-color: lightblue;");
        }
    }

    /**
     * Handles the hover-in event for a label.
     *
     * @param event The MouseEvent representing the hover-in event.
     */
    @FXML
    private void handleHoverIn(MouseEvent event) {
        Label label = (Label) event.getSource();
        label.setStyle("-fx-background-color: lightgray; -fx-cursor: hand;");
    }

    /**
     * Handles the hover-out event for a label.
     *
     * @param event The MouseEvent representing the hover-out event.
     */
    @FXML
    private void handleHoverOut(MouseEvent event) {
        Label label = (Label) event.getSource();
        label.setStyle("-fx-background-color: transparent; -fx-cursor: default;");
    }

    /**
     * Sets the language for all elements in the invitations control panel.
     * This method retrieves translations for various UI elements
     * from the LanguageManager and updates the corresponding
     * text accordingly.
     * If LanguageManager is not available, no action is taken.
     */
    public void setLanguageForAllInvitationsCtrl(){
        if(lm != null){
            inviteFollowing.setText(lm.get("Invite the following people by email"));
            cancel.setText(lm.get("Cancel"));
            giveInviteCode.setText(lm.get("Give people the following Invite Code: "));
            sendInvites.setText(lm.get("Send Invite"));
        }
    }
}
