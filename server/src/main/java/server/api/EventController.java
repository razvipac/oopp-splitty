package server.api;

import commons.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.EventService;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class EventController {
    private final EventService eventService;

    public EventController(@Autowired EventService eventService) {
        this.eventService = eventService;
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
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestParam("name") String name){
        return new ResponseEntity<>(eventService.createOne(name), HttpStatus.CREATED);
    }
}