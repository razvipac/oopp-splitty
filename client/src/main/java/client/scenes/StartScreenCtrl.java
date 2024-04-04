package client.scenes;

import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.util.*;

public class StartScreenCtrl implements VoidSceneController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField createEventTextField;
    @FXML
    private TextField joinEventTextField;
    @FXML
    private GridPane recentViewedEvents;

    private Set<EventDTO> recentlyJoinedEvents = new LinkedHashSet<>();
    private List<EventDTO> events;

    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void initialize(){
        createEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) createEvent();
        });

        joinEventTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) joinEvent();
        });

        refresh();
    }

    @FXML
    private void joinEvent() {
        String code = joinEventTextField.getText();
        Optional<EventDTO> found = getEvent(code);
        if(found.isPresent()) {
            recentlyJoinedEvents.removeIf(event -> event.getCode().equals(code));
            recentlyJoinedEvents.add(found.get());
            updateRecentEvents();
            mainCtrl.showEventOverview(found.get());
        }
        else System.out.println("Event with code: " + code + " doesn't exist");
    }

    @FXML
    private void createEvent() {
        String eventName = createEventTextField.getText();
        EventDTO event = server.createEvent(eventName);
        events = server.getAllEvents();
        System.out.println(event.toString());
        mainCtrl.showEventOverview(event);
    }

    private void updateRecentEvents() {
        recentViewedEvents.getChildren().clear();
        int amountOfEvents = 0;
        int lastIndex = recentlyJoinedEvents.size() - 1;
        for (int i = lastIndex; i >= 0 && amountOfEvents < 4; i--) {
            EventDTO event = new ArrayList<>(recentlyJoinedEvents).get(i);
            Label eventName = new Label(event.getName());
            Button overviewButton = new Button("\u2192");
            overviewButton.setOnAction(e -> mainCtrl.showEventOverview(event));
            Button removeButton = new Button("\u0078");
            removeButton.setOnAction(e -> {
                recentlyJoinedEvents.remove(event);
                refresh();
            });

            recentViewedEvents.add(eventName, 0, amountOfEvents);
            recentViewedEvents.add(overviewButton, 1, amountOfEvents);
            recentViewedEvents.add(removeButton, 2, amountOfEvents);
            amountOfEvents++;
        }
    }

    private Optional<EventDTO> getEvent(String code) {
        return events.stream()
                .filter(event -> event.getCode().equals(code))
                .findFirst();
    }

    public void refresh(){
        createEventTextField.clear();
        joinEventTextField.clear();
        events = server.getAllEvents();
        updateRecentEvents();
    }
}

