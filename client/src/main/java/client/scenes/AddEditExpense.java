package client.scenes;

import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class AddEditExpense {
    private Scene scene;

    private ArrayList<Expense> addEditExpenseList;

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     */
    public AddEditExpense() {
        // Data for testing purposes
        Participant john = new Participant("John",
                new Event("Abby's birthday party", "code1",
                        LocalDateTime.of(1900, 1, 1, 0, 0, 0)),
                "John@mail.com", "1234", "1234");
        Participant david = new Participant("David",
                new Event("Davidson's birthday party", "code2",
                        LocalDateTime.of(1900, 1, 1, 0, 0, 0)),
                "David@mail.com", "2341", "2341");
        Participant chris = new Participant("Chris",
                new Event("Stoffer's birthday party", "code3",
                        LocalDateTime.of(1900, 1, 1, 0, 0, 0)),
                "Chris@mail.com", "3412", "3412");
        Participant anna = new Participant("Anna",
                new Event("Belle's birthday party", "code4",
                        LocalDateTime.of(1900, 1, 1, 0, 0, 0)),
                "Anna@mail.com", "4123", "4123");

        addEditExpenseList = new ArrayList<>();
        addEditExpenseList.add(new Expense(123, "Food", john));
        addEditExpenseList.add(new Expense(34, "Drinks", chris));
        addEditExpenseList.add(new Expense(345, "Cake", anna));
        addEditExpenseList.add(new Expense(567, "Candles", david));


        createSceneAddEditExpense();
    }

    /**
     * Creates the GUI for the Add/Edit Expense
     */
    public void createSceneAddEditExpense() {
        // Title of the Add/Edit Expense (hardcoded)
        Text title = new Text("Add/Edit Expense");
        title.setFont(Font.font("Arial", FontWeight.BOLD , 20));

        // Labels and fields for the expense details
        Label whoPaidLabel = new Label("Who paid?");
        ComboBox<String> whoPaidDropdown = new ComboBox<>();


        Label whatForLabel = new Label("What for?");
        TextField whatForField = new TextField();

        Label howMuchLabel = new Label("How much?");
        TextField howMuchField = new TextField();

        Label whenLabel = new Label("When?");
        DatePicker whenPicker = new DatePicker();

        Label howToSplitLabel = new Label("How to Split?");
        RadioButton equallyButton = new RadioButton("Equally Between Everybody");
        RadioButton somePeopleButton = new RadioButton("Only Some People");
        ToggleGroup group = new ToggleGroup();
        equallyButton.setToggleGroup(group);
        somePeopleButton.setToggleGroup(group);

        CheckBox johnCheckbox = new CheckBox("John");
        CheckBox chrisCheckbox = new CheckBox("Chris");
        CheckBox annaCheckbox = new CheckBox("Anna");
        CheckBox davidCheckbox = new CheckBox("David");

        Label expenseTypeLabel = new Label("Expense Type");
        TextField expenseTypeField = new TextField();

        // Buttons for abort and add
        Button abortButton = new Button("Abort");
        Button addButton = new Button("Add");

        // layout
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(title, whoPaidLabel, whoPaidDropdown,
                whatForLabel, whatForField,
                howMuchLabel, howMuchField, whenLabel, whenPicker, howToSplitLabel, equallyButton,
                somePeopleButton, johnCheckbox, chrisCheckbox, annaCheckbox, davidCheckbox,
                expenseTypeLabel, expenseTypeField, abortButton, addButton);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 270, 300);

        layout.prefWidthProperty().bind(scene.widthProperty());
        layout.prefHeightProperty().bind(scene.heightProperty());
    }
}
