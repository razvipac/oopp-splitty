package client.scenes;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class EventOverview {

    private Scene scene;

    /**
     * Constructor for Event Overview page
     */
    public EventOverview() {
        createScene();
    }

    /**
     * Creates the scene
     */
    public void createScene() {
        // Layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));

        // HBox with Header and Send Invite button
        HBox boxTop = new HBox(5);

        // Header
        Text header = new Text("Header");
        header.setFont(Font.font("Arial", FontWeight.BOLD , 20));
        // Send Invite Button
        Button sendInviteButton = new Button("Send Invite");
        // Add to boxTop
        boxTop.getChildren().addAll(header, sendInviteButton);

        layout.getChildren().add(boxTop);

        // Scene
        scene = new Scene(layout, 400, 400);
    }

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }
}
