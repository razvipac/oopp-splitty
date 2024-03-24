package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import client.Main;
import javafx.geometry.Insets;
//import javafx.geometry.Pos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.*;
// import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import commons.Debt;
import commons.Participant;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OpenDebts {

    private Scene scene;
    private List<Debt> debtList;
    private Main main;
    private final ServerUtils server = ServerUtils.getServerUtils();

    /**
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Constructor for Open Debts page
     * @param main to the main class
     */
    public OpenDebts(Main main, Event event) {
        this.main = main;
        if(event == null)
            createSceneNoEvent();
        else
        {
            debtList = server.getDebtUtils().getAllOpenDebts(event.getCode());
            createScene();
            /*
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
            debtList = new ArrayList<>();
            debtList.add(new Debt(john, david, 123));
            debtList.add(new Debt(chris, david, 34));
            debtList.add(new Debt(anna, david, 5.60));
            */
        }
    }

    private void createSceneNoEvent() {
        VBox layout = new VBox(5);
        Text noEventText = new Text("You have to join an event before checking the debts.");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(noEventText, backButton);
        layout.setPadding(new Insets(20));

        scene = new Scene(layout, 350, 300);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        });
    }

    /**
     * Creates the scene for the Open Debts page
     */
    public void createScene() {
        // Layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));

        // Header
        Text header = new Text("Open Debts");
        header.setFont(Font.font("Arial", FontWeight.BOLD , 20));
        layout.getChildren().addAll(header);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());
        layout.getChildren().add(backButton);

        // Adding each debt
        for(Debt d : debtList) {
            addDebtToLayout(d, layout);
        }
        // Scene
        scene = new Scene(layout, 400, 500);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
        });

    }

    private void goBack() {
        main.getPrimaryStage().setScene(main.getMainScene());
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
        String debtorName = d.getDebtor().getName();
        String creditorName = d.getCreditor().getName();
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
        Text bankInfo = new Text(getBankInfoText(d));
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

    private static String getBankInfoText(Debt d)
    {
        Participant debtor = d.getDebtor();
        Participant creditor = d.getCreditor();
        double amount = d.getAmount();

        String creditorBankInfo =
                "Bank Information for creditor (" + creditor.getName() + "):\n" +
                "Account Holder: " + creditor.getName() + "\n" +
                "IBAN: " + creditor.getIban() + "\n" +
                "BIC: " + creditor.getBic();

        return "Debt Details:\n" +
                "Debtor: " + debtor.getName() + "\n" +
                "Creditor: " + creditor.getName() + "\n" +
                "Amount: " + amount + " Euro\n\n" +
                creditorBankInfo;
    }

    /**
     * Displays a modal alert box for viewing Open Debts.
     */
    public void displayAlertBox() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Participant");
        window.setScene(scene);
        window.showAndWait();
    }

}
