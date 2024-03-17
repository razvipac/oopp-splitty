package client.scenes;

import client.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Statistics {
    private Scene scene;

    private Main main;

    public Scene getScene() {
        return scene;
    }

    public Statistics(Main main) {
        this.main = main;
        createSceneStatistics();
    }

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
        backButton.setOnAction(e -> main.getPrimaryStage().setScene(main.getMainScene()));

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(titleLabel, pieChart, totalCostLabel, backButton);

        scene = new Scene(layout, 400, 200);
    }
}
