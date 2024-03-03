package server.api;

import commons.Event;
import commons.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.request_bodies.ExpenseBody;
import server.service.EventService;
import server.service.exceptions.NotFoundInDatabaseException;

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