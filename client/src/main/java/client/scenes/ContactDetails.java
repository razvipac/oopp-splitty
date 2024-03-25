package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import commons.Participant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    private Text errorText;

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

    /**
     * Creates the scene for if there is no event found
     */
    private void createSceneNoEvent() {
        VBox layout = new VBox(5);
        Text noEventText = new Text("No event found");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(noEventText, backButton);
        layout.setPadding(new Insets(20));

        scene = new Scene(layout, 340, 280);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        });
    }

    private void goBack() {
        main.getPrimaryStage().setScene(main.getMainScene());
    }

    /**
     * Create a GUI for the contact details
     */
    public void createSceneContactDetails() {
        Text title = new Text("Add/Edit Participant");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        errorText = new Text();
        errorText.setFill(Color.RED);

        gridPane.setVgap(5);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(0, 0, 10, 0));

        addToGridPane(boxName, "Name*:", "John", 200, 0);
        addToGridPane(boxEmail, "Email:", "johndoe@email.com", 200, 1);
        addToGridPane(boxIban, "IBAN:", "NL12 3456 7890 1234 56", 200, 2);
        addToGridPane(boxBic, "BIC:", "ABCDEFGH", 200, 3);

        // IBAN max character limit: 18
        TextFormatter<String> ibanFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            int nonSpaceCount = (int) newText.chars().filter(c -> c != ' ').count();
            if (nonSpaceCount <= 18) {
                return change;
            } else {
                return null;
            }
        });
        boxIban.setTextFormatter(ibanFormatter);

        // properly format the IBAN
        boxIban.focusedProperty().addListener((v, oldValue, newValue) -> {
            if(!newValue) {
                formatIban();
            }
        } );

        // BIC max character limit: 8
        TextFormatter<String> bicFormatter = new TextFormatter<>(change -> {
            if(change.getControlNewText().length() > 8) return null;
            else return change;
        });
        boxBic.setTextFormatter(bicFormatter);

        // turn the BIC to uppercase
        boxBic.focusedProperty().addListener((v, oldValue, newValue) -> {
            if(!newValue) {
                boxBic.setText(boxBic.getText().toUpperCase());
            }
        } );

        // Adding buttons
        Button abort = new Button("Abort");
        abort.setOnAction(e -> closeAlertBox());

        Button ok = new Button("Ok");
        ok.setOnAction(e -> {
            if(formIsValid())
                addParticipantToServer(new Participant(boxName.getText(), event, boxEmail.getText(),
                        boxIban.getText(), boxBic.getText()));
        });

        HBox hBoxButtons = new HBox(10); // 10 is the spacing between elements
        hBoxButtons.getChildren().addAll(abort, ok);

        // create the layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(title, errorText, gridPane, hBoxButtons);

        scene = new Scene(layout, 300, 260);
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
     * Formats the user-inputted IBAN by:
     *  a) Adding spaces to the right places
     *  b) Converting all letters to uppercase
     * The format of IBAN used is 'NL12 XXXX 0123 4567 89'
     */
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
     * Checks if the user-inputted Strings are valid.
     */
    private boolean formIsValid() {
        // Should in theory never be true
        if(event.getCode().isEmpty()) {
            errorText.setText("This event is invalid");
            return false;
        }

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
    private boolean inputIsInvalid(String input, String regex, String errorMessage) {
        if(!(input.isEmpty()) && !(input.matches(regex))) {
            errorText.setText(errorMessage);
            return true;
        }
        else return false;
    }

    /**
     * Adds the given Participant to the server, and displays an alert box with the outcome.
     * @param p The (validated) participant to add
     */
    private void addParticipantToServer(Participant p) {
        boolean success = server.getParticipantUtils().addParticipant(p);

        if(success) {
            Alert confirmation = createAlert(Alert.AlertType.CONFIRMATION,
                    "Success", "Participant Added Successfully",
                    p.getName() + " has been added to the event");
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().add(ButtonType.OK);
            confirmation.showAndWait();
        }
        else {
            Alert alert = createAlert(Alert.AlertType.ERROR,
                    "Error", "Adding Participant Failed",
                    "The participant has not been added due to an error. Please try again");
            alert.showAndWait();
        }

        closeAlertBox();
    }

    /**
     * Creates an alert window
     * @param type The type of alert (e.g. CONFIRMATION or ERROR)
     * @param title The title of the window
     * @param header The header of the window
     * @param content The content of the window
     * @return Alert object
     */
    private Alert createAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
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
        errorText.setText("");
        window.close();
    }
}