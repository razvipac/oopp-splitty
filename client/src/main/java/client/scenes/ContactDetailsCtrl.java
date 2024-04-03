package client.scenes;

import client.interfaces.DataBasedSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ContactDetailsCtrl implements DataBasedSceneController<EventDTO> {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private EventDTO event;

    /**
     * Constructor for the AddEditExpense that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     */
    @Inject
    public ContactDetailsCtrl(MainCtrl mainCtrl, ServerUtils serverUtils){
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    @Override
    public void initialize(EventDTO event) {
        this.event = event;
    }

}
