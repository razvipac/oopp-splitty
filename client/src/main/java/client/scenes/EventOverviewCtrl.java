package client.scenes;

import client.interfaces.DataBasedSceneController;
import client.utils.ControllerUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import commons.dto.ExpenseDTO;
import commons.dto.ParticipantDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventOverviewCtrl implements DataBasedSceneController<EventDTO> {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @Inject
    private ControllerUtils controllerUtils;

    // Event attributes
    private EventDTO event;
    private List<ParticipantDTO> participants;
    private List<ExpenseDTO> expenses;

    @FXML
    private TextField eventTitleTextField;
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
    @FXML
    private Label lastActivityLabel;

    // Currently selected participant (whose expenses to view)
    private ParticipantDTO selectedParticipant;
    private boolean firstTimeOpened = true;

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

        participants = serverUtils.getParticipants(event.code());
        expenses = serverUtils.getExpenses(event.code());

        // If participants isn't empty, select the first participant by default
        if(!(participants.isEmpty())) {
            selectedParticipant = participants.getFirst();
        }

        currentView = View.ALL;

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

        eventTitleTextField.textProperty().addListener((ov, prevText, currText) -> {
            Platform.runLater(() -> {
                resizeEventTitleTextField(currText);
                eventTitleTextField.positionCaret(eventTitleTextField.getCaretPosition());
            });
        });

        eventTitleTextField.focusedProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                revertEventTitle();
            }
        });

        serverUtils.registerForWebSocketUpdatesForTheWholeEvent(
                event.code(), q-> Platform.runLater(this::refresh));

        // The last activity should be updated only the first time the event is ever opened
        // From then on, whenever somebody visits it, it does not count as the last activity has been changed
        if(firstTimeOpened)
        {
            updateAndPrintLastActivity();
            firstTimeOpened = false;
        }
    }

    /**
     * Refreshes the page to reflect the current state of the server
     */
    public void refresh() {
        EventDTO syncedEvent = serverUtils.getEvent(event.code());
        if (syncedEvent == null) {
            Alert alert = controllerUtils.createAlert(
                    Alert.AlertType.WARNING,
                    "This event was deleted!",
                    "This event got deleted from the server!",
                    "This may be an error. Try to connect later or contact our customer service desk!");
            alert.showAndWait();
            mainCtrl.showStartScreen();
        } else {
            event = syncedEvent;
            participants = serverUtils.getParticipants(event.code());
            expenses = serverUtils.getExpenses(event.code());

            refreshEventInfoLabel();

            // resizes event title text field to the size of the current text
            Platform.runLater(() -> resizeEventTitleTextField(eventTitleTextField.getText()));

            refreshParticipantList();

            refreshParticipantDropdown();

            refreshFilterToggleGroupButtonLabels();

            refreshExpenseScroller();
        }
    }

    /**
     * Refreshes Event's name and code
     */
    private void refreshEventInfoLabel() {
        eventCodeLabel.setText(event.code());
        eventTitleTextField.setText(event.name());
    }

    /**
     * Refreshes the participant list
     */
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

        // Update total expenses label
        totalExpensesLabel.setText("Total sum of expenses: " + calculateTotalExpenseSum());

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
                    boolean isMatchingParticipant = e.expenseDTO.paidByName().equals(selectedParticipant.name());
                    item.setVisible(isMatchingParticipant);
                    item.setManaged(isMatchingParticipant);
                }
            }
        }
    }

    /**
     * Back button action.
     */
    @FXML
    private void goBack() {
        mainCtrl.showStartScreen();
    }

    /**
     * Opens the invitations screen and updates the last activity.
     */
    @FXML
    private void openInvitations() {
        mainCtrl.showInvitations(event);
    }

    /**
     * Opens the open debts screen and updates the last activity.
     */
    @FXML
    private void openOpenDebts() {
        mainCtrl.showOpenDebts(event);
        updateAndPrintLastActivity();
    }

    /**
     * Opens the add/edit participant screen and updates the last activity.
     */
    @FXML
    private void openAddEditParticipant() {
        mainCtrl.showContactDetails(event);
        updateAndPrintLastActivity();
    }

    /**
     * Opens the add/edit expense screen and updates the last activity.
     */
    @FXML
    private void openAddExpense() {
        mainCtrl.showAddExpense(event);
        updateAndPrintLastActivity();
    }

    /**
     * Opens the edit expense screen and updates the last activity.
     */
    @FXML
    private void openEditExpense(ExpenseDTO expense) {
        mainCtrl.showEditExpense(event, expense);
        updateAndPrintLastActivity();
    }

    /**
     * Handles the global key press event, specifically ESCAPE key to go back.
     *
     * @param keyEvent The KeyEvent representing the key press event.
     */
    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            if (eventTitleTextField.isFocused()) {
                handleEventTitleKeyboardEvent(keyEvent);
            }
            else{
                goBack();
            }
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
     * Updates and prints the custom toString method for the last activity
     */
    public void updateAndPrintLastActivity() {
        LocalDateTime updatedLastActivity = LocalDateTime.now();

        // Create a new EventDTO object with the updated last activity
        EventDTO updatedEventDTO = event.withLastActivity(updatedLastActivity);

        // Update the last activity label in the UI
        lastActivityLabel.setText(updatedEventDTO.lastActivityToString());
    }

    private void resizeEventTitleTextField(String currText) {
        Text text = new Text(currText);
        text.setFont(eventTitleTextField.getFont());

        double width = text.getLayoutBounds().getWidth()
                + eventTitleTextField.getPadding().getLeft() + eventTitleTextField.getPadding().getRight();
        eventTitleTextField.setMinWidth(width);
    }

    @FXML
    private void handleEventTitleKeyboardEvent(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String curText = eventTitleTextField.getText();
            boolean confirmed = controllerUtils.createConfirmationAlert("Changing the name of the event",
                    "Are you sure you want to change the name of the event to \""
                            + curText
                            + "\"?");
            if (confirmed) {
                serverUtils.updateEvent(event, curText);
            } else {
                revertEventTitle();
            }
        }
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            revertEventTitle();
        }
    }

    private void revertEventTitle() {
        resizeEventTitleTextField(event.name());
        eventTitleTextField.setText(event.name());
        Robot robot = new Robot();
        robot.keyType(KeyCode.TAB);
    }

    /**
     * Nested class Expense Item.
     */
    private class ExpenseItem extends GridPane {

        private final ExpenseDTO expenseDTO;
        private Text dateText;
        private Text expenseInfoText;
        private Text includesText;
        private Button editButton;
        private Button deleteButton;

        /**
         * Creates ExpenseItem, a GridPane containing an Expense's date, participant,
         * and an 'Edit' button.
         * @param expense the expense to create an item for
         */
        public ExpenseItem(ExpenseDTO expense) {
            expenseDTO = expense;
            createGridPaneBasis();
            addExpenseInformation();
        }

        /**
         * Creates basis for this GridPane
         */
        private void createGridPaneBasis() {
            this.setAlignment(Pos.CENTER_LEFT);
            this.setPrefSize(557, 42);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHalignment(HPos.CENTER);
            col1.setMaxWidth(102.5);
            col1.setPrefWidth(84.5);

            ColumnConstraints col2 = new ColumnConstraints();
            col2.setMaxWidth(378.5);
            col2.setPrefWidth(364.0);

            ColumnConstraints col3 = new ColumnConstraints();
            col3.setMaxWidth(202.5);
            col3.setPrefWidth(57.0);

            ColumnConstraints col4 = new ColumnConstraints();
            col4.setMaxWidth(147.0);
            col4.setPrefWidth(51.5);

            RowConstraints row1 = new RowConstraints();
            row1.setMaxHeight(21.0);
            row1.setPrefHeight(21.0);

            RowConstraints row2 = new RowConstraints();
            row2.setMaxHeight(21.0);
            row2.setPrefHeight(21.0);

            this.getColumnConstraints().addAll(col1, col2, col3, col4);
            this.getRowConstraints().addAll(row1, row2);

            dateText = new Text("(no date)");
            dateText.setFill(Color.web("#6f6f6f"));
            GridPane.setHalignment(dateText, HPos.LEFT);
            GridPane.setMargin(dateText, new Insets(0, 0, 0, 10));
            this.add(dateText, 0, 0, 1, 2);

            expenseInfoText = new Text();
            this.add(expenseInfoText, 1, 0);

            includesText = new Text("(everyone)");
            includesText.setFill(Color.web("#6f6f6f"));
            this.add(includesText, 1, 1);

            editButton = new Button("Edit");
            editButton.setPrefSize(45, 14);
            editButton.setFont(Font.font(10));
            GridPane.setHalignment(editButton, HPos.RIGHT);
            this.add(editButton, 2, 0, 1, 2);

            deleteButton = new Button("Delete");
            deleteButton.setPrefSize(45, 14);
            deleteButton.setFont(Font.font(10));
            GridPane.setHalignment(deleteButton, HPos.RIGHT);
            this.add(deleteButton, 3, 0, 1, 2);
        }

        /**
         * Adds the information of the expense to this GridPane
         */
        private void addExpenseInformation() {
            if(expenseDTO.date() != null ) dateText.setText(expenseDTO.date().toString());

            expenseInfoText.setText(expenseDTO.paidByName() + " paid \u20AC"
                    + expenseDTO.price() + " for " + expenseDTO.item());

            // TODO: includesText is currently hardcoded to '(everyone)'

            editButton.setOnAction(eventHandler -> openEditExpense(expenseDTO));

            deleteButton.setOnAction(eventHandler -> {
                boolean confirmed = controllerUtils.createConfirmationAlert(
                        "Confirm Delete",
                        "Are you sure you want to delete this expense?");

                if (confirmed) {
                    serverUtils.deleteExpense(expenseDTO, event.code());
                }
            });
        }
    }
}
