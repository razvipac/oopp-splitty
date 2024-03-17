package client.scenes;

import client.Main;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

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

        VBox vbox = new VBox(pieChart);

        scene = new Scene(vbox, 400, 200);
    }
}
