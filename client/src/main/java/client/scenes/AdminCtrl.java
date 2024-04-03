package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.dto.EventDTO;

import java.util.List;

public class AdminCtrl {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private List<EventDTO> events;

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

    public void initalize() {
        events = serverUtils.getAllEvents();
    }

}
