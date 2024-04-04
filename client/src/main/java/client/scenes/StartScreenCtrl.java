package client.scenes;

import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.List;
import java.util.Optional;

public class StartScreenCtrl implements VoidSceneController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField createEventTextField;
    @FXML
    private TextField joinEventTextField;

    private List<EventDTO> events;

    /**
     * Constructor for the StartScreenCtrl class.
     * @param server The global serverUtils singleton.
     * @param mainCtrl The scene of the mainCtrl class.
     */
    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Initializes the controller.
     * Sets up event listeners and refreshes the scene.
     */
    public void initialize(){
        createEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) createEvent();
        });

        joinEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) joinEvent();
        });

        refresh();
    }

    /**
     * Handles the "Join Event" button action.
     * Retrieves the event with the provided code and navigates to its overview.
     */
    @FXML
    private void joinEvent() {
        String code = joinEventTextField.getText();
        Optional<EventDTO> found = getEvent(code);
        if(found.isPresent()) {
            mainCtrl.showEventOverview(found.get());
        }
        else System.out.println("Event with code: " + code + " doesn't exist");
    }

    /**
     * Handles the "Create Event" button action.
     * Creates a new event with the provided name and navigates to its overview.
     */
    @FXML
    private void createEvent() {
        String eventName = createEventTextField.getText();
        EventDTO event = server.createEvent(eventName);
        events = server.getAllEvents();
        System.out.println(event.toString());
        mainCtrl.showEventOverview(event);
    }

    /**
     * Retrieves the event corresponding to the given code.
     * @param code The code of the event.
     * @return The event with the provided code, if found.
     */
    private Optional<EventDTO> getEvent(String code) {
        return events.stream()
                .filter(event -> event.getCode().equals(code))
                .findFirst();
    }

    /**
     * Refreshes the start screen by clearing text fields and updating event data.
     */
    public void refresh(){
        createEventTextField.clear();
        joinEventTextField.clear();
        events = server.getAllEvents();
    }
}
