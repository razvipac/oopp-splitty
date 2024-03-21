package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import commons.Participant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ContactDetails {
    private Scene scene;
    private final Main main;
    private final Event event;

    private final ServerUtils server = ServerUtils.getServerUtils();

    /**
     * Constructor for the contact details that calls the method to create the scene
     * @param main scene of the main class
     * @param event the event to add to
     */
    public ContactDetails(Main main, Event event){
        this.main = main;
        this.event = event;

        if(event == null) {
            createSceneNoEvent();
            return;
        }

        createSceneContactDetails();
    }

    private void createSceneNoEvent() {
        VBox layout = new VBox(5);
        Text noEventText = new Text("No event found");
        layout.getChildren().add(noEventText);
        scene = new Scene(layout, 350, 380);
    }

    /**
     * create a GUI for the contact details
     */
    public void createSceneContactDetails() {
        Text title = new Text("Add/Edit Participant");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Create a label
        Label nameLabel = new Label("Name:");
        // Create a text field
        TextArea boxName = createTextArea("Enter the name here", 350, 100);

        // Create an HBox to hold the label and text field
        HBox hBoxName = createHBox(nameLabel, boxName, 10, 10);
        hBoxName.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email:");
        // Create a text field
        TextArea boxEmail = createTextArea("Enter the email here", 350, 100);

        // Create an HBox to hold the label and text field
        HBox hBoxEmail = createHBox(emailLabel, boxEmail, 10, 10);
        hBoxEmail.setAlignment(Pos.CENTER);

        Label ibanLabel = new Label("IBAN:");
        // Create a text field
        TextArea boxIBAN = createTextArea("Enter the IBAN here", 350, 100);

        // Create an HBox to hold the label and text field
        HBox hBoxIban = new HBox(10); // 10 is the spacing between elements
        hBoxIban.setPadding(new Insets(10)); // Padding around the HBox
        hBoxIban.getChildren().addAll(ibanLabel, boxIBAN);
        hBoxIban.setAlignment(Pos.CENTER);

        Label bicLabel = new Label("BIC:");
        // Create a text field
        TextArea boxBIC = createTextArea("Enter the BIC here", 350, 100);

        // Create an HBox to hold the label and text field
        HBox hBoxBic = createHBox(bicLabel, boxBIC, 10, 10);

        // Adding buttons

        Button abort = new Button("Abort");
        abort.setOnAction(e -> {
            // return back
        });

        // non functional
        Button ok = new Button("Ok");
        ok.setOnAction(e -> {
            Participant p = new Participant(boxName.getText(), event, boxEmail.getText(),
                    boxIBAN.getText(), boxBIC.getText());
            System.out.println(p.toString());
            server.getParticipantUtils().addParticipant(p);
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> main.getPrimaryStage().setScene(main.getMainScene()));

        HBox hBoxButtons = new HBox(10); // 10 is the spacing between elements
        hBoxBic.setPadding(new Insets(10)); // Padding around the HBox
        hBoxBic.getChildren().addAll(abort, ok, backButton);
        hBoxButtons.setAlignment(Pos.CENTER);

        // create the layout
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().add(title);

        layout.getChildren().addAll(hBoxName, hBoxEmail, hBoxIban, hBoxBic);
        // the button is placed alongside the rest, not below, it needs fixing
        layout.getChildren().addAll(hBoxButtons);

        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 555, 555);

        layout.prefWidthProperty().bind(scene.widthProperty());
        layout.prefHeightProperty().bind(scene.heightProperty());
    }

    /**
     * Create an HBox to hold the label and text area
     * @param label label to be added to the HBox
     * @param textArea text area to be added to the HBox
     * @param spacing spacing between elements
     * @param padding padding around the HBox
     * @return the HBox
     */
    public HBox createHBox(Label label, TextArea textArea, int spacing, int padding) {
        // Create an HBox to hold the label and text area
        HBox hbox = new HBox(spacing);
        hbox.setPadding(new Insets(padding));
        hbox.getChildren().addAll(label, textArea);

        return hbox;
    }

    /**
     * Create a text area with the given parameters
     * @param promptText Prompt text of the text area
     * @param maxWidth Maximum width of the text area
     * @param maxHeight Maximum height of the text area
     * @return the text area
     */
    public TextArea createTextArea(String promptText, int maxWidth, int maxHeight) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setMaxWidth(maxWidth);
        textArea.setMaxHeight(maxHeight);

        return textArea;
    }

    /**
     * Get the scene
     * @return the scene
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Displays a modal alert box for adding a participant.
     */
    public void displayAlertBox() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Participant");
        window.setScene(scene);
        window.showAndWait();
    }
}