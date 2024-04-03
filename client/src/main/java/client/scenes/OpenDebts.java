package client.scenes;

import client.interfaces.DataBasedPopupController;
import client.utils.ServerUtils;
import commons.dto.DebtDTO;
import commons.dto.EventDTO;
import commons.dto.ParticipantDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class OpenDebts {

    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;

    private Scene scene;
    private final Stage window;
    private boolean isOpen;

    private EventDTO event;
    private List<DebtDTO> debtList;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     * @param event event entity corresponding to this window
     */
    public OpenDebts(MainCtrl mainCtrl, ServerUtils serverUtils, EventDTO event) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;

        window = new Stage();
        window.setTitle("Add Participant");
        window.initModality(Modality.APPLICATION_MODAL);
        isOpen = false;

        initialize(event);
    }

    /**
     * Generate an ui from the given object instance
     * @param event Event instance to populate the UI with
     * @return the newly generated scene
     */
    public Scene initialize(EventDTO event) {
        this.event = event;

        if (event == null) createSceneNoEvent();
        else {
            debtList = serverUtils.getAllOpenDebts(event.getCode());
            createScene();
        }

        return scene;
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
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        layout.getChildren().addAll(header);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());
        layout.getChildren().add(backButton);

        // Adding each debt
        for(DebtDTO d : debtList) {
            addDebtToLayout(d, layout);
        }
        // Scene
        scene = new Scene(layout, 400, 500);
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
     * Adds the specified debt to the layout
     * @param d Debt to be added
     * @param layout Layout to add it to
     */
    private static void addDebtToLayout(DebtDTO d, VBox layout) {
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

    private static String getBankInfoText(DebtDTO d)
    {
        ParticipantDTO debtor = d.getDebtor();
        ParticipantDTO creditor = d.getCreditor();
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
     * Getter for the scene
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Getter for isOpen, true - window is open - false otherwise
     * @return value for isOpen
     */
    public boolean isOpen(){
        return isOpen;
    }

}
