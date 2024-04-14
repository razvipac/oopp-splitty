package client.scenes;

import client.LanguageManager;
import client.interfaces.DataBasedSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.DebtDTO;
import commons.dto.EventDTO;
import commons.dto.ParticipantDTO;
import javafx.application.Platform;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OpenDebtsCtrl implements DataBasedSceneController<EventDTO> {

    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    private EventDTO event;
    private final List<DebtDTO> debtList;
    private final List<DebtDTO> unsettledDebts;
    private final List<DebtDTO> settledDebts;

    @Inject
    private LanguageManager lm;

    @FXML
    private VBox debtVBox;
    @FXML
    private Label openDebts;
    @FXML
    private Button backButton;
    @FXML
    private Button regenerateButton;
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
        unsettledDebts = new ArrayList<>();
        settledDebts = new ArrayList<>();
    }

   /**
     * Initializes the scene
     * @param location passed URL location
     * @param resources passed ResourceBundle
     */
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Refreshes the scene with fresh data from the server
     * @param event event to which the scene corresponds
     */
    public void refresh(EventDTO event){
        this.event = event;

        serverUtils.registerForLongPollingDebtUpdates(event.code(), debtDTOs -> {
            Platform.runLater(() -> refresh(this.event));
        });

        List<DebtDTO> debtDTOs = serverUtils.getAllDebts(event.code());
        debtList.clear();
        debtList.addAll(debtDTOs);

        if (debtDTOs.isEmpty()) serverUtils.regenerateDebts(event.code());

        refreshDebtList();
        setLanguageForAllOpenDebtsCtrl();
    }

    /**
     * Refreshes the debt list by clearing it and fetching all open debts from the server for the
     * current event. Each debt is then added to the layout.
     */
    public void refreshDebtList(){
        unsettledDebts.clear();
        unsettledDebts.addAll(
                debtList.stream()
                        .filter(debtDTO -> !debtDTO.received())
                        .toList()
        );

        settledDebts.clear();
        settledDebts.addAll(
                debtList.stream()
                        .filter(DebtDTO::received)
                        .toList()
        );

        debtVBox.getChildren().clear();

        // Adding each debt
        for(DebtDTO d : unsettledDebts) {
            addDebtToLayout(d);
        }

        for(DebtDTO d : settledDebts){
            addDebtToLayout(d);
        }
    }

    /**
     * Back button action
     */
    @FXML
    public void goBack() {
        mainCtrl.showEventOverview(event);
    }

    /**
     * Handles global key presses within the application.
     * This method is annotated with @FXML to indicate it's a handler for JavaFX FXML-defined events.
     * It listens for key presses and if the pressed key is the Escape key, it invokes the goBack() method.
     *
     * @param keyEvent The KeyEvent representing the key press event.
     */
    @FXML
    public void onGlobalKeyPress(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ESCAPE) goBack();
    }

    /**
     * Adds the specified debt to the layout
     * @param d Debt to be added
     */
    public void addDebtToLayout(DebtDTO d) {
        // debtLine: line containing debtString and 'Mark Received' button
        HBox debtLine = new HBox(5);
        // debtItem: the entire debt item, containing the debtLine and debtInfo
        VBox debtItem = new VBox(5);

        // debtString & debtStringLabel, contains the debtor, creditor and amount
        String debtorName = d.debtorName();
        String creditorName = d.creditorName();
        double amount = d.amount();
        String debtString = debtorName + lm.get(" gives ") + amount + " Euro "+lm.get("to")+" " + creditorName;
        Text debtStringLabel = new Text(debtString);

        debtStringLabel.setStrikethrough(d.received());

        // 'Mark Received' button. Prints effect to console for testing
        String buttonText = d.received() ? lm.get("Undo") : lm.get("Mark received");
        Button receivedButton = new Button(buttonText);
        receivedButton.getStyleClass().add("primary-button");
        receivedButton.setOnAction(e -> serverUtils.toggleDebtReceivedStatus(event.code(), d));

        // extra debt info (bank information)
        VBox debtInfo = new VBox(5);
        Text bankInfo = new Text(getBankInfoText(d));
        debtInfo.setPadding(new Insets(0, 0, 10, 0));
        debtInfo.getChildren().addAll(bankInfo);
        debtInfo.setVisible(false);
        debtInfo.setManaged(false);

        // toggle button for debtInfo, can be shown or hidden
        ToggleButton moreInfo = new ToggleButton(">");
        moreInfo.getStyleClass().add("secondary-button");
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
        debtVBox.getChildren().add(debtItem);
    }

    /**
     * Generates a string containing bank information for a creditor and debt details.
     *
     * @param d The DebtDTO object representing the debt details.
     * @return A string containing debt details and bank information for the creditor.
     */
    public String getBankInfoText(DebtDTO d)
    {
        List<ParticipantDTO> participants = serverUtils.getParticipants(event.code());
        ParticipantDTO debtor = serverUtils.getParticipant(event.code(), d.debtorName());
        ParticipantDTO creditor = serverUtils.getParticipant(event.code(), d.creditorName());
        double amount = d.amount();

        String creditorBankInfo =
                lm.get("Bank Information for creditor (") + creditor.name() + "):\n" +
                        lm.get("Account Holder: ") + creditor.name() + "\n" +
                        "IBAN: " + creditor.iban() + "\n" +
                        "BIC: " + creditor.bic();

        return lm.get("Debt Details:") + "\n" +
                lm.get("Debtor: ") + debtor.name() + "\n" +
                lm.get("Creditor: ") + creditor.name() + "\n" +
                lm.get("Amount: ") + amount + " Euro\n\n" +
                creditorBankInfo;
    }

    /**
     * Sets the VBox for displaying debt-related information.
     *
     * @param debtVBox The VBox to be set for displaying debt-related information.
     */
    public void setDebtVBox(VBox debtVBox) {
        this.debtVBox = debtVBox;
    }

    @FXML
    private void regenerateDebts(){
        serverUtils.regenerateDebts(event.code());
    }

    /**
     * Sets the language for all elements in the invitations control panel.
     * This method retrieves translations for various UI elements
     * from the LanguageManager and updates the corresponding
     * text accordingly.
     * If LanguageManager is not available, no action is taken.
     */
    public void setLanguageForAllOpenDebtsCtrl() {
        if(lm == null){
            System.out.println("lm is null when setting the languages in OpenDebtsCtrl");
            return;
        }
        openDebts.setText(lm.get("Open Debts"));
        backButton.setText(lm.get("Back"));
        regenerateButton.setText(lm.get("_Regenerate Debts"));
    }
}
