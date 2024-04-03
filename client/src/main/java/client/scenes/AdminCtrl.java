package client.scenes;

import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.List;

public class AdminCtrl implements VoidSceneController {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private List<EventDTO> events;

    @FXML
    private GridPane eventGrid;

    /**
     * Constructor for AdminCtrl.
     * @param mainCtrl MainCtrl class
     * @param serverUtils Global ServerUtils singleton
     */
    @Inject
    public AdminCtrl(MainCtrl mainCtrl, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    public void initialize() {
        refresh();
    }

    public void refresh() {
        events = serverUtils.getAllEvents();
        addEventsToEventGrid();
    }

    public void addEventsToEventGrid() {
        for (int i = 0; i < events.size(); i++) {
            EventDTO e = events.get(i);
            Button eventNameButton = new Button(e.getName());
            Button deleteButton = new Button("Delete");
            Button downloadButton = new Button("Download");

            eventGrid.add(eventNameButton, 0, i);
            eventGrid.add(deleteButton, 1, i);
            eventGrid.add(downloadButton, 2, i);
        }
    }

}
