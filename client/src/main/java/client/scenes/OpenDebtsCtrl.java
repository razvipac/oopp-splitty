package client.scenes;

import client.interfaces.DataBasedSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.DebtDTO;
import commons.dto.EventDTO;
import commons.dto.ParticipantDTO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class OpenDebtsCtrl implements DataBasedSceneController<EventDTO> {

    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;

    private EventDTO event;
    private final List<DebtDTO> debtList;

    @FXML
    private VBox debtVBox;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     */
    @Inject
    public OpenDebtsCtrl(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;

        // TODO: Functionality to be tested when debts are available on the server
        debtList = new ArrayList<>();
    }

    /**
     * Generate an ui from the given object instance
     *
     * @param event Event instance to populate the UI with
     */
    public void initialize(EventDTO event) {
        this.event = event;

        refreshDebtList();
    }

    /**
     * Refreshes the debt list by clearing it and fetching all open debts from the server for the
     * current event. Each debt is then added to the layout.
     */
    public void refreshDebtList(){
        debtList.clear();
        debtList.addAll(serverUtils.getAllOpenDebts(event.getCode()));

        // Adding each debt
        for(DebtDTO d : debtList) {
            addDebtToLayout(d, debtVBox);
        }
    }


    /**
     * Back button action
     */
    @FXML
    private void goBack() {
        mainCtrl.showEventOverview(event);
    }

    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
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
}
