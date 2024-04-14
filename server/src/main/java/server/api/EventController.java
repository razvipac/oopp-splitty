package server.api;

import commons.dto.EventDTO;
import commons.dto.WSAction;
import commons.dto.WSWrapperResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.entities.DTOMapper;
import server.entities.event.Event;
import server.entities.participant.Participant;
import server.service.EventService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class EventController {
    private final EventService eventService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DTOMapper<Event, EventDTO> eventDTOMapper;
    private final ParticipantController participantController;

    /**
     * Constructs an EventController with the specified EventService and SimpMessagingTemplate.
     *
     * @param eventService          The EventService to be injected into the controller.
     * @param simpMessagingTemplate The SimpMessagingTemplate to be injected into the controller.
     * @param eventDTOMapper The EventDTOMapper instance to be injected into the controller.
     * @param participantController The ParticipantController instance to be injected into the controller.
     */
    public EventController(@Autowired EventService eventService,
                           @Autowired SimpMessagingTemplate simpMessagingTemplate,
                           @Autowired DTOMapper<Event, EventDTO> eventDTOMapper,
                           @Autowired ParticipantController participantController
    ) {
        this.eventService = eventService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.eventDTOMapper = eventDTOMapper;
        this.participantController = participantController;
    }

    /**
     * Retrieves all events through a GET request to /api/v1/.
     *
     * @return A ResponseEntity containing a list of all events fetched from the database.
     * Returns HttpStatus.OK if successful.
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll() {
        List<Event> events = eventService.getAll();
        List<EventDTO> eventDTOs = events
                .stream()
                .map(eventDTOMapper::toDTO)
                .toList();
        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }


    /**
     * Creates a new Event with the specified name through a POST request to /api/v1/?name={name}.
     *
     * Sends out a WebSocket STOMP message to all listeners on "/api/websocket/v1/channel/event"
     * with WSAction CREATED.
     *
     * @param name The name of the new event.
     * @return A ResponseEntity containing the newly created event.
     * Returns HttpStatus.CREATED if successful.
     */
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestParam("name") String name) {
        Event event = eventService.createOne(name);

        simpMessagingTemplate.convertAndSend("/api/websocket/v1/channel/event",
                new WSWrapperResponseBody<>(
                        WSAction.CREATED,
                        event
                ));

        return new ResponseEntity<>(eventDTOMapper.toDTO(event), HttpStatus.CREATED);
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
    public ResponseEntity<EventDTO> deleteOne(
            @RequestParam("eventCode") String eventCode
    ) {
        try{
            Event event = eventService.getOne(eventCode);
            EventDTO eventDTO = eventDTOMapper.toDTO(event);

            deleteDependants(event);
            eventService.deleteOne(eventCode);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode,
                    new WSWrapperResponseBody<>(
                            WSAction.DELETED,
                            eventDTO
                    )
            );

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/event",
                    new WSWrapperResponseBody<>(
                            WSAction.DELETED,
                            eventDTO
                    )
            );

            return new ResponseEntity<>(eventDTO, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates the event with the specified Event Code by changing its name through a PUT request to
     * api/v1/{eventCode}?name={name}
     *
     * @param name The new name for the event.
     * @param eventCode The Event Code of the event to be updated.
     * @return A ResponseEntity containing the updated event if successful.
     *         Returns HttpStatus.OK if successful.
     *         Returns HttpStatus.NOT_FOUND if the event is not found in the database.
     */

    @PutMapping("/{eventCode}")
    public ResponseEntity<EventDTO> updateOneByName(
            @RequestParam("name") String name,
            @PathVariable("eventCode") String eventCode
    ) {
        try {
            Event event = eventService.updateOne(eventCode, name);
            EventDTO eventDTO = eventDTOMapper.toDTO(event);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode,
                    new WSWrapperResponseBody<>(
                            WSAction.MODIFIED,
                            eventDTO
                    )
            );

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/event",
                    new WSWrapperResponseBody<>(
                            WSAction.MODIFIED,
                            eventDTO
                    )
            );

            return new ResponseEntity<>(eventDTO, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes all of the dependats of an event
     * @param event that is deleted
     */
    public void deleteDependants(Event event){
        if(event.getParticipants()==null) return;
        List<Participant> toBeDeleted = new ArrayList<>(event.getParticipants());
        for (Participant participant : toBeDeleted){
            participantController.deleteOne(
                    participant.getName(),
                    event.getCode()
            );
        }
    }
}