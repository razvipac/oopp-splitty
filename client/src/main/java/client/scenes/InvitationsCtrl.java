package client.scenes;

import client.LanguageManager;
import client.interfaces.DataBasedSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;

import java.net.URL;
import java.util.ResourceBundle;

public class InvitationsCtrl implements DataBasedSceneController<EventDTO> {
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
    private TextArea emailAddressesTextArea;

    /**
     * Constructor for the InvitationsCtrl
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     */
    @Inject
    public InvitationsCtrl(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
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
        emailAddressesTextArea.setText("");
        mainCtrl.showEventOverview(event);
    }

    @FXML
    private void sendInvites(){
        System.out.println("Sending invites...");
        // TODO: implement sending emails
        String title = "Invitations sent successfully";
        String header = "Invitations were sent successfully!";
        String content = "The invitations sent successfully to: \n";
        if(lm != null){
            title = lm.get("Invitations sent successfully");
            header = lm.get("Invitations were sent successfully!");
            content = lm.get("The invitations sent successfully to: \n");
        }
        Alert successAlert = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                title,
                header,
                content + emailAddressesTextArea.getText());
        successAlert.showAndWait();
        goBack();
    }

    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
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
            inviteFollowing.setText(lm.get("Invite the following people by email (one address per line)"));
            cancel.setText(lm.get("Cancel"));
            giveInviteCode.setText(lm.get("Give people the following Invite Code: "));
            sendInvites.setText(lm.get("Send Invite"));
        }
    }
}
