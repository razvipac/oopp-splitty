package client.scenes;

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

public class ContactDetails {
    private Scene scene;
    private TextArea boxName;
    private TextArea boxEmail;
    private TextArea boxIBAN;
    private TextArea boxBIC;

    public ContactDetails(){
        createSceneContactDetails();
    }

    /**
     * create a GUI for the contact details
     */
    public void createSceneContactDetails() {
        Text title = new Text("Add/Edit Expense");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        /**
         * creating the name hbox
         */

        // Create a label
        Label nameLabel = new Label("Name:");
        // Create a text field
        boxName = new TextArea();
        boxName.setPromptText("Enter the name here");
        boxName.setMaxWidth(350);
        boxName.setMaxHeight(100);

        // Create an HBox to hold the label and text field
        HBox hboxName = new HBox(10); // 10 is the spacing between elements
        hboxName.setPadding(new Insets(10)); // Padding around the HBox
        hboxName.getChildren().addAll(nameLabel, boxName);

        /**
         * creating the email hbox
         */

        Label emailLabel = new Label("Email:");
        // Create a text field
        boxEmail = new TextArea();
        boxEmail.setPromptText("Enter the email here");
        boxEmail.setMaxWidth(350);
        boxEmail.setMaxHeight(100);

        // Create an HBox to hold the label and text field
        HBox hboxEmail = new HBox(10); // 10 is the spacing between elements
        hboxEmail.setPadding(new Insets(10)); // Padding around the HBox
        hboxEmail.getChildren().addAll(emailLabel, boxEmail);

        /**
         * creating the IBAN hbox
         */
        Label ibanLabel = new Label("IBAN:");
        // Create a text field
        boxIBAN = new TextArea();
        boxIBAN.setPromptText("Enter the IBAN here");
        boxIBAN.setMaxWidth(350);
        boxIBAN.setMaxHeight(100);

        // Create an HBox to hold the label and text field
        HBox hboxiban = new HBox(10); // 10 is the spacing between elements
        hboxiban.setPadding(new Insets(10)); // Padding around the HBox
        hboxiban.getChildren().addAll(ibanLabel, boxIBAN);

        /**
         * creating the BIC hbox
         */
        Label bicLabel = new Label("BIC:");
        // Create a text field
        boxBIC = new TextArea();
        boxBIC.setPromptText("Enter the BIC here");
        boxBIC.setMaxWidth(350);
        boxBIC.setMaxHeight(100);

        // Create an HBox to hold the label and text field
        HBox hboxbic = new HBox(10); // 10 is the spacing between elements
        hboxbic.setPadding(new Insets(10)); // Padding around the HBox
        hboxbic.getChildren().addAll(bicLabel, boxBIC);


        /**
         * adding the buttons
         */

        Button abort = new Button("Abort");
        abort.setOnAction(e -> {
            // return back
        });

        // non functional
        Button ok = new Button("Ok");
        abort.setOnAction(e -> {
            // add it to the server
        });

        HBox hboxButtons = new HBox(10); // 10 is the spacing between elements
        hboxbic.setPadding(new Insets(10)); // Padding around the HBox
        hboxbic.getChildren().addAll(abort, ok);

        /**
         * creating the layout
         */
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().add(title);

        layout.getChildren().addAll(hboxName);
        layout.getChildren().addAll(hboxEmail);
        layout.getChildren().addAll(hboxiban);
        layout.getChildren().addAll(hboxbic);
        // the button is placed along side the rest, not below, it needs fixing
        layout.getChildren().addAll(hboxButtons);

        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 555, 555);

        layout.prefWidthProperty().bind(scene.widthProperty());
        layout.prefHeightProperty().bind(scene.heightProperty());
    }

    public Scene getScene() {
        return this.scene;
    }
}