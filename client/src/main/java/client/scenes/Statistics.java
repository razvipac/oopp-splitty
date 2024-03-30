package client.scenes;

import client.utils.ServerUtils;
import commons.dto.EventDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Statistics {
    private final MainCtrl mainCtrl; // reference to MainCtrl class
    private final ServerUtils serverUtils;

    private Scene scene;
    private final Stage window;
    private boolean isOpen;

    private EventDTO event;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     * @param event event entity corresponding to this window
     */
    public Statistics(MainCtrl mainCtrl, ServerUtils serverUtils, EventDTO event) {
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
        this.event = event;

        createSceneStatistics();

        return scene;
    }

    /**
     * Creates the GUI for the statistics
     */
    public void createSceneStatistics() {
        // Created a pie chart hard coded for now
        PieChart pieChart = new PieChart();
        PieChart.Data slice1 = new PieChart.Data("Drinks", 213);
        PieChart.Data slice2 = new PieChart.Data("Food", 67);
        PieChart.Data slice3 = new PieChart.Data("Transportation", 36);
        PieChart.Data slice4 = new PieChart.Data("Other", 29);

        pieChart.getData().add(slice1);
        pieChart.getData().add(slice2);
        pieChart.getData().add(slice3);
        pieChart.getData().add(slice4);


        int totalCost = (int) (slice1.getPieValue() + slice2.getPieValue() +
                slice3.getPieValue() + slice4.getPieValue());


        Label totalCostLabel = new Label("Total Cost of Event: " + totalCost + "€");
        totalCostLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));


        Label titleLabel = new Label("Statistics");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(titleLabel, pieChart, totalCostLabel, backButton);

        scene = new Scene(layout, 600, 550);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        });
    }

    /**
     * Back button action
     */
    private void goBack() {
        closeAlertBox();
    }

    /**
     * Displays a modal alert box for viewing Open Debts.
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
        window.close();
    }

    /**
     * Getter for isOpen, true - window is open - false otherwise
     * @return value for isOpen
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }
}
