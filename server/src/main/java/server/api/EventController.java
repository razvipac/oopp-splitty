package server.api;

import commons.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.api.pojo.response_body.WSAction;
import server.api.pojo.response_body.WSWrapperResponseBody;
import server.service.EventService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class EventController {
    private final EventService eventService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructs an EventController with the specified EventService and SimpMessagingTemplate.
     *
     * @param eventService          The EventService to be injected into the controller.
     * @param simpMessagingTemplate The SimpMessagingTemplate to be injected into the controller.
     */
    public EventController(@Autowired EventService eventService,
                           @Autowired SimpMessagingTemplate simpMessagingTemplate) {
        this.eventService = eventService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Retrieves all events through a GET request to /api/v1/.
     *
     * @return A ResponseEntity containing a list of all events fetched from the database.
     * Returns HttpStatus.OK if successful.
     */
    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        return new ResponseEntity<>(eventService.getAll(), HttpStatus.OK);
    }


    /**
     * Creates a new Event with the specified name through a POST request to /api/v1/?name={name}.
     * <p>
     * Sends out a WebSocket STOMP message to all listeners on "/api/websocket/v1/channel/event"
     * with WSAction CREATED.
     *
     * @param name The name of the new event.
     * @return A ResponseEntity containing the newly created event.
     * Returns HttpStatus.CREATED if successful.
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestParam("name") String name) {
        Event event = eventService.createOne(name);

        simpMessagingTemplate.convertAndSend("/api/websocket/v1/channel/event",
                new WSWrapperResponseBody<>(
                        WSAction.CREATED,
                        event
                ));

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }


    /**
     * Deletes the event with the specified Event Code
     * through a DELETE request to api/v1/?eventCode={eventCode}.
     *
     * @param eventCode The Event Code of the event to be deleted.
     * @return A ResponseEntity containing the deleted event if successful.
     *         Returns HttpStatus.OK if successful.
     *         Returns HttpStatus.NOT_FOUND if the event is not found in the database.
     */

    @DeleteMapping("")
    public ResponseEntity<Event> deleteOne(
            @RequestParam("eventCode") String eventCode
    ) {
        try{
            Event event = eventService.deleteOne(eventCode);
            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode,
                    new WSWrapperResponseBody<>(
                            WSAction.DELETED,
                            event
                    ));

            return new ResponseEntity<>(event, HttpStatus.OK);

        } catch (NotFoundInDatabaseException e){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates the event with the specified Event Code by changing its name through a PUT request to
     * api/v1/{eventCode}/expense?id={id}.
     *
     * @param name The new name for the event.
     * @param eventCode The Event Code of the event to be updated.
     * @return A ResponseEntity containing the updated event if successful.
     *         Returns HttpStatus.OK if successful.
     *         Returns HttpStatus.NOT_FOUND if the event is not found in the database.
     */

    @PutMapping("")
    public ResponseEntity<Event> updateOneByName(
            @RequestParam("name") String name,
            @RequestParam("eventCode") String eventCode
    ) {
        try {
            Event event = eventService.updateOne(eventCode, name);
            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode,
                    new WSWrapperResponseBody<>(
                            WSAction.MODIFIED,
                            event
                    ));

            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}