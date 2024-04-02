package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.dto.EventDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Optional;

public class StartScreenCtrl{

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField createEventTextField;
    @FXML
    private TextField joinEventTextField;

    private List<EventDTO> events;

//    private List<EventDTO> events;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param server global serverUtils singleton
     */
    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    private void goBack() {
//        mainCtrl.showDevScreen();
    }

    public void joinEvent(ActionEvent e) {
        System.out.println("joinEvent()");
        String code = joinEventTextField.getText();
        Optional<EventDTO> found = getEvent(code);
        if(found.isPresent()) {
//            mainCtrl.showEventOverview(found.get());
        }
        else System.out.println("Event with code: " + code + " doesn't exist");
    }

    public void createEvent(ActionEvent e) {
        System.out.println("createEvent()");
        String eventName = createEventTextField.getText();
        EventDTO event = server.createEvent(eventName);
        events = server.getAllEvents();
        System.out.println(event.toString());
//        mainCtrl.showEventOverview(event);
    }

    /**
     * Get event corresponding to the given code
     * @param code Code of the event
     * @return The event
     */
    public Optional<EventDTO> getEvent(String code) {
        return events.stream()
                .filter(event -> event.getCode().equals(code))
                .findFirst();
    }

    public void refresh(){
        System.out.println("StartScreenCtrl refresh()");
        // TODO: refresh the list of recent events
    }

}
