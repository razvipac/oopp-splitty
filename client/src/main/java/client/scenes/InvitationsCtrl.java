package client.scenes;

import client.interfaces.DataBasedSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;

public class InvitationsCtrl implements DataBasedSceneController<EventDTO> {
    private final MainCtrl mainCtrl; // reference to MainCtrl class
    private final ServerUtils serverUtils;

    @Inject
    private ControllerUtils controllerUtils;

    private EventDTO event;

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
     * Populate the scene from the given event DTO
     * @param event Event instance to populate the UI with
     */
    public void initialize(EventDTO event) {
        this.event = event;
        this.eventCodeLabel.setText(event.getCode());
        this.eventTitleLabel.setText(event.getName());
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
        Alert successAlert = controllerUtils.createAlert(Alert.AlertType.CONFIRMATION,
                "Invitations sent successfully",
                "Invitations were sent successfully!",
                "The invitations sent successfully to: \n" + emailAddressesTextArea.getText());
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
}
