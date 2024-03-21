package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import commons.Participant;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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

    private final GridPane gridPane = new GridPane();
    private final TextField boxName = new TextField();
    private final TextField boxEmail = new TextField();
    private final TextField boxIban = new TextField();
    private final TextField boxBic = new TextField();

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
     * Create a GUI for the contact details
     */
    public void createSceneContactDetails() {
        Text title = new Text("Add/Edit Participant");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        gridPane.setVgap(5);
        gridPane.setHgap(10);

        addToGridPane(boxName, "Name:", "Enter your name here", 200, 0);
        addToGridPane(boxEmail, "Email:", "Enter your email here", 200, 1);
        addToGridPane(boxIban, "IBAN:", "Enter your IBAN here", 200, 2);
        addToGridPane(boxBic, "BIC:", "Enter your BIC here", 200, 3);

        // Adding buttons
        Button abort = new Button("Abort");
        abort.setOnAction(e -> {
            // return back
        });

        Button ok = new Button("Ok");
        ok.setOnAction(e -> {
            Participant p = new Participant(boxName.getText(), event, boxEmail.getText(),
                    boxName.getText(), boxBic.getText());
            System.out.println(p);
            server.getParticipantUtils().addParticipant(p);
        });

        HBox hBoxButtons = new HBox(10); // 10 is the spacing between elements
        hBoxButtons.getChildren().addAll(abort, ok);

        // create the layout
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(title, gridPane, hBoxButtons);

        scene = new Scene(layout, 300, 250);
    }

    /**
     * Adds the given TextField to gridPane at the given coordinates, along with a label.
     * @param textField The (empty) TextField object
     * @param labelText The String of the Label
     * @param promptText The prompt text of the TextField
     * @param prefWidth The preferred width of the TextField
     * @param gridY The y coordinate of gridPane to add to.
     */
    public void addToGridPane(TextField textField, String labelText,
                              String promptText, int prefWidth, int gridY) {
        Label label = new Label(labelText);
        textField.setPromptText(promptText);
        textField.setPrefWidth(prefWidth);

        gridPane.add(label, 0, gridY);
        gridPane.add(textField, 1, gridY);
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