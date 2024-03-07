package client.scenes;

import client.Main;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AddEditExpense {
    private Scene scene;
    private Main main;

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
     * @param main scene of the main class
     */
    public AddEditExpense(Main main) {
        this.main = main;
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

    // GridPane for the layout
        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.add(title, 0, 0);

    // Labels and fields for the expense details
        Label whoPaidLabel = new Label("Who paid?");
        ComboBox<String> whoPaidDropdown = new ComboBox<>();

        for (Expense expense : addEditExpenseList) {
            String name = expense.getPaidBy().getName();
            whoPaidDropdown.getItems().add(name);
        }
        layout.add(whoPaidLabel, 0, 1);
        layout.add(whoPaidDropdown, 1, 1);

        Label whatForLabel = new Label("What for?");
        TextField whatForField = new TextField();
        layout.add(whatForLabel, 0, 2);
        layout.add(whatForField, 1, 2);

        Label howMuchLabel = new Label("How much?");
        TextField howMuchField = new TextField();
        layout.add(howMuchLabel, 0, 3);
        layout.add(howMuchField, 1, 3);

        Label currencyLabel = new Label("Currency");
        ComboBox<String> currencyDropdown = new ComboBox<>();
        currencyDropdown.getItems().addAll("USD", "EUR", "GBP");
        layout.add(currencyLabel, 2, 3);
        layout.add(currencyDropdown, 3, 3);

        Label whenLabel = new Label("When?");
        DatePicker whenPicker = new DatePicker();
        layout.add(whenLabel, 0, 4);
        layout.add(whenPicker, 1, 4);

        Label howToSplitLabel = new Label("How to Split?");
        RadioButton equallyButton = new RadioButton("Equally Between Everybody");
        RadioButton somePeopleButton = new RadioButton("Only Some People");
        ToggleGroup group = new ToggleGroup();
        equallyButton.setToggleGroup(group);
        somePeopleButton.setToggleGroup(group);
        layout.add(howToSplitLabel, 0, 5);
        layout.add(equallyButton, 1, 5);
        layout.add(somePeopleButton, 1, 6);

        VBox checkboxContainer = new VBox();
        checkboxContainer.setAlignment(Pos.CENTER);
        for (Expense expense : addEditExpenseList) {
            String name = expense.getPaidBy().getName();
            CheckBox participantCheckbox = new CheckBox(name);
            participantCheckbox.setPadding(new Insets(2, 2, 2, 2));
            checkboxContainer.getChildren().add(participantCheckbox);
        }
        layout.add(checkboxContainer, 1, 7);

        Label expenseTypeLabel = new Label("Expense Type");
        TextField expenseTypeField = new TextField();
        layout.add(expenseTypeLabel, 0, 8);
        layout.add(expenseTypeField, 1, 8);

        // Buttons for abort and add
        Button abortButton = new Button("Abort");
        Button addButton = new Button("Add");
        layout.add(abortButton, 0, 9);
        layout.add(addButton, 1, 9);
        layout.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> main.getPrimaryStage().setScene(main.getMainScene()));
        layout.add(backButton, 0, 10);

        scene = new Scene(layout, 500, 600);
    }
}
