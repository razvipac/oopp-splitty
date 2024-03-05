package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.request_bodies.ParticipantBody;
import server.service.exceptions.NotFoundInDatabaseException;
import server.service.ParticipantService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{eventCode}/participant")
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(@Autowired ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * GET api/v1/{eventCode}/expense?name={name}
     * name is optional
     * if it is omitted all Participants of event with {eventCode} will be returned
     * if it is given a Participant belonging to a Event with {eventCode} and given name will be returned
     */
    @GetMapping
    public ResponseEntity<List<Participant>> getAllOrOne(
            @PathVariable(value = "eventCode") String eventCode,
            @RequestParam(value = "name", required = false) String name){
        if (name == null)
            return new ResponseEntity<>(participantService.getAll(eventCode), HttpStatus.OK);

        try {
            return new ResponseEntity<>(List.of(participantService.getOne(eventCode, name)), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST /api/v1/{eventCode}/participant with body in ParticipantBody format
     * Creates a new Participant populated with the data in body
     */
    @PostMapping
    public ResponseEntity<Participant> createOne(
            @PathVariable("eventCode") String eventCode,
            @RequestBody ParticipantBody body
    ){
        try{
            return new ResponseEntity<>(participantService.createOne(eventCode, body), HttpStatus.CREATED);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Participant> deleteOne(
            @RequestParam("participantName") String participantName,
            @PathVariable("eventCode") String eventCode
    ){
        try{
            return new ResponseEntity<>(participantService.deleteOne(eventCode, participantName), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("")
    public ResponseEntity<Participant> updateOneById(
            @RequestParam("name") String name,
            @PathVariable("eventCode") String eventCode,
            @RequestBody ParticipantBody body
    ){
        try{
            return new ResponseEntity<>(participantService.updateOne(eventCode, name, body), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
