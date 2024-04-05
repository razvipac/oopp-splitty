package client.scenes;

import client.interfaces.DataBasedSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class EventOverviewCtrl implements DataBasedSceneController<EventDTO> {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    // Event attributes
    private EventDTO event;
    private List<ParticipantDTO> participants;
    private List<ExpenseDTO> expenses;

    @FXML
    private Label eventTitleLabel;
    @FXML
    private Label eventCodeLabel;
    @FXML
    private HBox participantsHBox;
    @FXML
    private ToggleGroup expenseFilterToggleGroup;
    @FXML
    private ComboBox<String> expenseFilterComboBox;
    @FXML
    private RadioButton expenseFilterAllRadio;
    @FXML
    private RadioButton expenseFilterFromRadio;
    @FXML
    private RadioButton expenseFilterIncludingRadio;
    @FXML
    private Label totalExpensesLabel;
    @FXML
    private VBox expenseItemContainer;

    // Currently selected participant (whose expenses to view)
    private ParticipantDTO selectedParticipant;

    // Currently selected expenses view (all, from or including <selectedParticipant>)
    public enum View {
        ALL, FROM, INCLUDING
    }

    private View currentView;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     */
    @Inject
    public EventOverviewCtrl(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * Generate an ui from the given object instance
     * @param event Event instance to populate the UI with
     */
    public void initialize(EventDTO event) {
        this.event = event;

        refresh();

        // Show/Hide expense items based on currentView
        expenseFilterToggleGroup.selectedToggleProperty().addListener(
                (v, oldValue, newValue) -> refreshExpenseScroller()
        );

        // Listener for participantDropdown, sets selectedParticipant and updates radio button text
        expenseFilterComboBox.getSelectionModel().selectedIndexProperty().addListener(
                (v, oldValue, newValue) -> {
                    if (participants.isEmpty()) selectedParticipant = null;
                    else selectedParticipant = participants.get(Math.max((Integer) newValue, 0));
                    refreshFilterToggleGroupButtonLabels();
                    refreshExpenseScroller();
                }
        );
    }

    /**
     * Refreshes the page back to its default values.
     */
    public void refresh() {
        participants = serverUtils.getParticipants(event.code());
        expenses = serverUtils.getExpenses(event.code());

        // If participants isn't empty, select the first participant by default
        if(!(participants.isEmpty())) {
            selectedParticipant = participants.getFirst();
        }

        currentView = View.ALL;

        refreshEventInfoLabel();

        refreshParticipantList();

        refreshParticipantDropdown();

        refreshFilterToggleGroupButtonLabels();

        refreshExpenseScroller();
    }

    private void refreshEventInfoLabel() {
        eventTitleLabel.setText(event.name());
        eventCodeLabel.setText(event.code());
    }

    private void refreshParticipantList(){
        ArrayList<Label> participantLabels = new ArrayList<>();

        for (ParticipantDTO participant : participants){
            participantLabels.add(new Label(participant.name()));
        }

        if (participants.isEmpty()) participantLabels.add(new Label("(No participants in event)"));

        participantsHBox.getChildren().setAll(participantLabels);
    }

    /**
     * Gets participantDropDown, a ComboBox containing all the participants of the event.
     * The selected option is saved to selectedParticipant.
     */
    private void refreshParticipantDropdown() {
        expenseFilterComboBox.getItems().setAll(
                participants
                        .stream()
                        .map(ParticipantDTO::name)
                        .toList()
        );

        expenseFilterComboBox.setDisable(participants.isEmpty());

        if (selectedParticipant != null)
            expenseFilterComboBox.getSelectionModel().select(selectedParticipant.name());
    }

    /**
     * Sets the text of the radio buttons to match selectedParticipant, and disables
     * them if there are no expenses
     */
    private void refreshFilterToggleGroupButtonLabels() {
        expenseFilterAllRadio.setDisable(expenses.isEmpty());
        expenseFilterFromRadio.setDisable(expenses.isEmpty());
        expenseFilterIncludingRadio.setDisable(expenses.isEmpty());

        String name = selectedParticipant == null ? "(participant)" : selectedParticipant.name();

        expenseFilterFromRadio.setText("From " + name);
        expenseFilterIncludingRadio.setText("Including " + name);
    }

    /**
     * Gets expensesScroller, which includes a VBox container for all ExpenseItem objects.
     */
    private void refreshExpenseScroller() {
        expenseItemContainer.getChildren().clear();

        for(ExpenseDTO expense : expenses) {
            ExpenseItem item = new ExpenseItem(expense);
            expenseItemContainer.getChildren().add(item);
        }

        setExpenseItemVisibility();
    }

    /**
     * Sets the visibility of the given ExpenseItem according to currentView.*
     * If currentView == ALL, then set the ExpenseItem to be visible and managed.
     * If currentView == FROM or currentView == INCLUDING, then set node to be visible and
     * managed iff selectedParticipant equals the participant tied to the ExpenseItem.
     */
    private void setExpenseItemVisibility() {
        // set currentView to the selected radio button
        if(expenseFilterAllRadio.isSelected()) currentView = View.ALL;
        else if(expenseFilterFromRadio.isSelected()) currentView = View.FROM;
        else if(expenseFilterIncludingRadio.isSelected()) currentView = View.INCLUDING;

        // set visibility according to currentView
        for(Node item : expenseItemContainer.getChildren()) {
            // TODO: properly implement the 'INCLUDING' view (currently does the same as 'ALL')
            switch (currentView) {
                case ALL, INCLUDING -> {
                    item.setVisible(true);
                    item.setManaged(true);
                }
                case FROM -> {
                    ExpenseItem e = (ExpenseItem) item;
                    boolean isMatchingParticipant = e.paidByName.equals(selectedParticipant.name());
                    item.setVisible(isMatchingParticipant);
                    item.setManaged(isMatchingParticipant);
                }
            }
        }
    }

    /**
     * Handles the copying of the invitation code when the label is double-clicked.
     *
     * @param event The MouseEvent representing the double click event.
     */


    /**
     * Back button action
     */
    @FXML
    private void goBack() {
        mainCtrl.showStartScreen();
    }

    @FXML
    private void openInvitations(){
        mainCtrl.showInvitations(event);
    }

    @FXML
    private void openOpenDebts(){
        mainCtrl.showOpenDebts(event);
    }

    @FXML
    private void openAddEditParticipant(){
        mainCtrl.showContactDetails(event);
    }

    @FXML
    private void openAddEditExpense(){
        mainCtrl.showAddEditExpense(event);
    }

    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ESCAPE){
            goBack();
        }
    }

    /**
     * Handles the copying of the event code when the label is double-clicked.
     *
     * @param event The MouseEvent representing the double click event.
     */
    @FXML
    private void handleCopyEventCode(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            // Get the source of the event, which should be the event code label
            Label eventCodeLabel = (Label) event.getSource();
            String eventCode = eventCodeLabel.getText();

            // Create a clipboard and add the event code to its content
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(eventCode);
            clipboard.setContent(content);

            eventCodeLabel.setStyle("-fx-background-color: lightblue;");
        }
    }

    /**
     * Handles the hover-in event for a label.
     *
     * @param event The MouseEvent representing the hover-in event.
     */
    @FXML
    private void handleHoverIn(MouseEvent event) {
        Label label = (Label) event.getSource();
        label.setStyle("-fx-background-color: lightgray; -fx-cursor: hand;");
    }

    /**
     * Handles the hover-out event for a label.
     *
     * @param event The MouseEvent representing the hover-out event.
     */
    @FXML
    private void handleHoverOut(MouseEvent event) {
        Label label = (Label) event.getSource();
        label.setStyle("-fx-background-color: transparent; -fx-cursor: default;");
    }

    /**
     * Calculate the total sum of expenses.
     * @return The total sum of expenses
     */
    private int calculateTotalExpenseSum() {
        int totalSum = 0;
        for (ExpenseDTO expense : expenses) {
            totalSum += expense.price();
        }
        return totalSum;
    }


    /**
     * Nested class Expense Item.
     */
    private static class ExpenseItem extends GridPane {

        private int price;
        private String item;
        private String paidByName;

        /**
         * Creates ExpenseItem, a GridPane containing an Expense's date, participant,
         * and an 'Edit' button.
         * @param expense the expense to create an item for
         */
        public ExpenseItem(ExpenseDTO expense) {
            price = expense.price();
            item = expense.item();
            paidByName = expense.paidByName();

            createItemBox();
        }

        private void createItemBox() {
            this.setHgap(12);
            this.setVgap(2);

            // TODO: implement this for actual Expense date
            Text date = new Text("01-01-2024");
            this.add(date, 0, 0, 1, 2);

            Text expenseInfo = new Text(paidByName + " paid " + price + " Euro for " + item);
            this.add(expenseInfo, 1, 0);

            // TODO: 'paidBy includes ...' is currently hardcoded to 'all'
            Text expenseIncludes = new Text("(all)");
            this.add(expenseIncludes, 1, 1);

            Button expenseEditButton = new Button("Edit");
            this.add(expenseEditButton, 2, 0, 1, 2);
        }

    }
}
