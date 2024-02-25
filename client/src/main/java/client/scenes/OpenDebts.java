package client.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.ArrayList;

import commons.Debt;
import commons.Person;

public class OpenDebts {

    private Stage window;
    private ArrayList<Debt> debtList;

    public OpenDebts() {
        // Data for testing purposes
        Person john = new Person("John", "Doe");
        Person david = new Person("David", "Davidson");
        Person chris = new Person("Chris", "Stoffer");
        Person anna = new Person("Anna", "Belle");
        debtList = new ArrayList<>();
        debtList.add(new Debt(john, david, 123));
        debtList.add(new Debt(chris, david, 34));
        debtList.add(new Debt(anna, david, 5.60));
    }

    public void start(Stage stage) {
        window = stage;

        // Header
        Text header = new Text("Open Debts");

        // Layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        // adding elements to layout
        layout.getChildren().addAll(header);

        // Main scene
        Scene scene = new Scene(layout, 270, 200);

        // Main window
        window.setTitle("Open Debts");
        window.setScene(scene);
        window.show();
    }

}
