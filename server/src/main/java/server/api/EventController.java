package server.api;

import commons.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
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

    public EventController(@Autowired EventService eventService,
                           @Autowired SimpMessagingTemplate simpMessagingTemplate) {
        this.eventService = eventService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * GET /api/v1/
     * Gets all Events
     */
    @GetMapping
    public ResponseEntity<List<Event>> getAll(){
        return new ResponseEntity<>(eventService.getAll(), HttpStatus.OK);
    }


    /**
     * POST /api/v1/?name={name}
     * Creates a new Event with name {name}
     *
     * Sends out a WebSocket STOMP message to all listeners on "/api/websocket/v1/channel/event
     * with WSAction CREATED
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestParam("name") String name){
        Event event = eventService.createOne(name);

        simpMessagingTemplate.convertAndSend("/api/websocket/v1/channel/event",
                new WSWrapperResponseBody<>(
                        WSAction.CREATED,
                        event
                ));

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    /**
     * DELETE api/v1/?code={eventCode}/
     * deletes the whole event with the Event Code {eventCode}
     */
    @DeleteMapping("")
    public ResponseEntity<Event> deleteOne(
            @RequestParam("eventCode") String eventCode
    ){
        try{
            return new ResponseEntity<>(eventService.deleteOne(eventCode), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    /**
     * PUT api/v1/{eventCode}/expense?id={id}
     */
    @PutMapping("")
    public ResponseEntity<Event> updateOneByName(
            @RequestParam("name") String name,
            @RequestParam("eventCode") String eventCode
    ){
        try{
            return new ResponseEntity<>(eventService.updateOne(eventCode, name), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}