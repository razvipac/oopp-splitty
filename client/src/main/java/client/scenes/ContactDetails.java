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

    private Stage window;
    private Scene scene;
    private final Main main;
    private final Event event;

    private final ServerUtils server = ServerUtils.getServerUtils();

    private final GridPane gridPane = new GridPane();
    private final TextField boxName = new TextField();
    private final TextField boxEmail = new TextField();
    private final TextField boxIban = new TextField();
    private final TextField boxBic = new TextField();
    private final Text errorText = new Text();

    /**
     * Constructor for the contact details that calls the method to create the scene
     * @param main scene of the main class
     * @param event the event to add to
     */
    public ContactDetails(Main main, Event event){
        this.main = main;
        this.event = event;

        if(event == null) createSceneNoEvent();
        else createSceneContactDetails();
    }

    private void createSceneNoEvent() {
        VBox layout = new VBox(5);
        Text noEventText = new Text("No event found");
        layout.getChildren().add(noEventText);
        scene = new Scene(layout, 300, 250);
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
        abort.setOnAction(e -> closeAlertBox());

        Button ok = new Button("Ok");
        ok.setOnAction(e -> validateAndAddParticipant());

        HBox hBoxButtons = new HBox(10); // 10 is the spacing between elements
        hBoxButtons.getChildren().addAll(abort, ok);

        // create the layout
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(title, errorText, gridPane, hBoxButtons);

        scene = new Scene(layout, 300, 250);
    }

    private void validateAndAddParticipant() {
        String name = boxName.getText();
        String email = boxEmail.getText();
        String iban = boxIban.getText();
        String bic = boxBic.getText();

        // Should in theory never be true
        if(event.getCode().isEmpty()) {
            errorText.setText("This event is invalid");
            return;
        }

        if(name.isEmpty()) {
            errorText.setText("Please fill in the name field");
            return;
        }

        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(!(email.isEmpty()) && !(email.matches(regexPattern))) {
            errorText.setText("Please enter a valid email address");
            return;
        }

        Participant p = new Participant(boxName.getText(), event, boxEmail.getText(),
                boxIban.getText(), boxBic.getText());
        System.out.println(p);
        server.getParticipantUtils().addParticipant(p);
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
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Participant");
        window.setScene(scene);
        window.setOnCloseRequest(e -> closeAlertBox());
        window.showAndWait();
    }

    /**
     * Closes the modal alert box and clears up the input fields.
     */
    public void closeAlertBox() {
        boxName.clear();
        boxEmail.clear();
        boxIban.clear();
        boxBic.clear();
        window.close();
    }
}