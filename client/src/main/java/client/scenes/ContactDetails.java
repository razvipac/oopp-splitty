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



        // layout
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().add(title);
        layout.getChildren().addAll(hboxName);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 333 * 16/9, 333 );

        layout.prefWidthProperty().bind(scene.widthProperty());
        layout.prefHeightProperty().bind(scene.heightProperty());
    }

    public Scene getScene() {
        return this.scene;
    }
}
