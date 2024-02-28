package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.request_bodies.ParticipantBody;
import server.service.NotFoundInDatabaseException;
import server.service.ParticipantService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{eventCode}/participant")
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(@Autowired ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping
    public ResponseEntity<List<Participant>> getAllInEvent(@PathVariable("eventCode") String eventCode){
        return new ResponseEntity<>(participantService.getAll(eventCode), HttpStatus.OK);
    }

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
}
