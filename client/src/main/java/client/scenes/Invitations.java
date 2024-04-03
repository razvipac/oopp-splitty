package client.scenes;

import commons.dto.EventDTO;
import client.interfaces.DataBasedPopupController;
import client.utils.ServerUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Invitations {
    private final MainCtrl mainCtrl; // reference to MainCtrl class
    private final ServerUtils serverUtils;

    private Scene scene;
    private final Stage window;
    private boolean isOpen;

    private TextArea boxToPutEmails;
    private String eventName;
    private String eventCode;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     * @param event event entity corresponding to this window
     */
    public Invitations(MainCtrl mainCtrl, ServerUtils serverUtils, EventDTO event) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;

        window = new Stage();
        window.setTitle("Send Invite");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOnCloseRequest(e -> closeAlertBox());

        initialize(event);
    }

    /**
     * Generate an ui from the given object instance
     * @param event Event instance to populate the UI with
     * @return the newly generated scene
     */
    public Scene initialize(EventDTO event) {
        if(event == null) createSceneNoEvent();
        else {
            eventName = event.getName();
            eventCode = event.getCode();
            createSceneInvitation();
        }
        return scene;
    }

    /**
     * Creates the scene for if there is no event found
     */
    private void createSceneNoEvent() {
        VBox layout = new VBox(5);
        Text noEventText = new Text("You have to join an event before inviting people.");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(noEventText, backButton);
        layout.setPadding(new Insets(20));

        scene = new Scene(layout, 360, 290);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        });
    }

    /**
     * Creates the GUI for the invitation
     */
    public void createSceneInvitation() {
        // Title of the invitation
        Text title = new Text(eventName);
        title.setFont(Font.font("Arial", FontWeight.BOLD , 20));

        // Labels for the invite code and the email addresses
        Label inviteCode = new Label("Give people the following Invite Code: "
                + eventCode);
        Label inviteByEmailAddress = new Label("Invite the following people " +
                "by email (one address per line):");

        // text area for email addresses
        boxToPutEmails = new TextArea();
        boxToPutEmails.setPromptText("Enter email addresses here");
        boxToPutEmails.setMaxWidth(350);
        boxToPutEmails.setMaxHeight(50);

        // send invites button (not functional)
        Button sendInvitesButton = new Button("Send Invites");
        sendInvitesButton.setFont(Font.font("Arial"));
        sendInvitesButton.setOnAction(e -> {
            // send the invites to the email addresses in the box
            System.out.println("Invites sent to: " + boxToPutEmails.getText());
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());

        // layout
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(title, inviteCode, inviteByEmailAddress,
                boxToPutEmails, sendInvitesButton, backButton);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 450, 300);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        });
    }

    /**
     * Displays a modal alert box for the Invitations page.
     */
    public void displayAlertBox() {
        isOpen = true;
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Closes a modal alert box for viewing Open Debts.
     */
    public void closeAlertBox() {
        isOpen = false;
        boxToPutEmails.clear();
        window.close();
    }

    /**
     * Back button action
     */
    private void goBack() {
        closeAlertBox();
    }

    /**
     * Getter for isOpen, true - window is open - false otherwise
     * @return value for isOpen
     */
    public boolean isOpen(){
        return isOpen;
    }
}
