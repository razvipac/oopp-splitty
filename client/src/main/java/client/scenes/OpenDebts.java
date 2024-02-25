package client.scenes;

import javafx.geometry.Insets;
//import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
// import javafx.stage.Stage;

import java.util.ArrayList;

import commons.Debt;
import commons.Person;

public class OpenDebts {

    private Scene scene;
    private ArrayList<Debt> debtList;

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Constructor for Open Debts page
     */
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

        createScene();
    }

    /**
     * Creates the scene
     */
    public void createScene() {
        // Layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));

        // Header
        Text header = new Text("Open Debts");
        header.setFont(Font.font("Arial", FontWeight.BOLD , 20));
        layout.getChildren().add(header);

        // Adding each debt
        for(Debt d : debtList) {
            addDebtToLayout(d, layout);
        }

        // Scene
        scene = new Scene(layout, 400, 400);
    }

    /**
     * Adds the specified debt to the layout
     * @param d Debt to be added
     * @param layout Layout to add it to
     */
    private static void addDebtToLayout(Debt d, VBox layout) {
        // debtLine: line containing debtString and 'Mark Received' button
        HBox debtLine = new HBox(5);
        // debtItem: the entire debt item, containing the debtLine and debtInfo
        VBox debtItem = new VBox(5);

        // debtString & debtStringLabel, contains the debtor, creditor and amount
        String debtorName = d.getDebtor().firstName;
        String creditorName = d.getCreditor().firstName;
        double amount = d.getAmount();
        String debtString = debtorName + " gives " + amount + " Euro to " + creditorName;
        Label debtStringLabel = new Label(debtString);

        // 'Mark Received' button. Prints effect to console for testing
        Button receivedButton = new Button("Mark received");
        receivedButton.setOnAction(event -> {
            d.setReceived(!d.isReceived());
            if(d.isReceived()) {
                receivedButton.setText("Undo");
                System.out.println("The debt (" + debtString + ") is marked as received");
            }
            else {
                receivedButton.setText("Mark received");
                System.out.println("The debt (" + debtString + ") is marked as not received");
            }
        });

        // extra debt info (bank information)
        VBox debtInfo = new VBox(5);
        Text bankInfo = new Text("""
                Bank information available, transfer money to:
                Account Holder: John Doe
                IBAN: NL12 3456 7890 1234 56
                BIC: ABCDEFGH""");
        debtInfo.setPadding(new Insets(0, 0, 10, 0));
        debtInfo.getChildren().addAll(bankInfo);
        debtInfo.setVisible(false);
        debtInfo.setManaged(false);

        // toggle button for debtInfo, can be shown or hidden
        ToggleButton moreInfo = new ToggleButton(">");
        moreInfo.setOnAction(event -> {
            if(moreInfo.isSelected()) {
                moreInfo.setText("v");
                debtInfo.setVisible(true);
                debtInfo.setManaged(true);
            }
            else {
                moreInfo.setText(">");
                debtInfo.setVisible(false);
                debtInfo.setManaged(false);
            }
        });

        debtLine.getChildren().addAll(moreInfo, debtStringLabel, receivedButton);
        debtItem.getChildren().addAll(debtLine, debtInfo);
        layout.getChildren().add(debtItem);
    }

}
