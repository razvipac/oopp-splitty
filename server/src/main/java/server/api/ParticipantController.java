package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;
import server.api.pojo.request_body.ParticipantRequestBody;
import server.api.pojo.response_body.ParticipantResponseBody;
import server.api.pojo.response_body.WSAction;
import server.api.pojo.response_body.WSWrapperResponseBody;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{eventCode}/participant")
public class ParticipantController {
    private final ParticipantService participantService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ParticipantController(@Autowired ParticipantService participantService,
                                 @Autowired SimpMessagingTemplate simpMessagingTemplate) {
        this.participantService = participantService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * GET api/v1/{eventCode}/expense?name={name}
     * name is optional
     * if it is omitted all Participants of event with {eventCode} will be returned
     * if it is given a Participant belonging to a Event with {eventCode} and given name will be returned
     */
    @GetMapping
    public ResponseEntity<List<ParticipantResponseBody>> getAllOrOne(
            @PathVariable(value = "eventCode") String eventCode,
            @RequestParam(value = "name", required = false) String name){
        if (name == null){
            List<Participant> participants = participantService.getAll(eventCode);
            List<ParticipantResponseBody> participantResponseBodies = participants.stream().map(ParticipantResponseBody::build).toList();
            return new ResponseEntity<>(participantResponseBodies, HttpStatus.OK);
        }


        try {
            return new ResponseEntity<>(List.of(
                    ParticipantResponseBody.build(
                            participantService.getOne(eventCode, name)
                    )), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @MessageMapping("v1/{eventCode}/participant")
//    @SendToUser("/api/websocket/v1/channel/{eventCode}/participant")
//    public WSWrapperResponseBody<List<ParticipantResponseBody>> getAll(
//            @DestinationVariable("eventCode") String eventCode
//    ){
//        List<Participant> participants = participantService.getAll(eventCode);
//        List<ParticipantResponseBody> responseBodies = participants.stream().map(ParticipantResponseBody::build).toList();
//        return new WSWrapperResponseBody<>(WSAction.RESPONDED, responseBodies);
//    }

    /**
     * POST /api/v1/{eventCode}/participant with body in ParticipantRequestBody format
     * Creates a new Participant populated with the data in body
     */
    @PostMapping
    public ResponseEntity<ParticipantResponseBody> createOne(
            @PathVariable("eventCode") String eventCode,
            @RequestBody ParticipantRequestBody body
    ){
        try{
            Participant participant = participantService.createOne(eventCode, body);
            ParticipantResponseBody responseBody = ParticipantResponseBody.build(participant);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/participant",
                    new WSWrapperResponseBody<>(
                            WSAction.CREATED,
                            responseBody
                    ));

            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<ParticipantResponseBody> deleteOne(
            @RequestParam("participantName") String participantName,
            @PathVariable("eventCode") String eventCode
    ){
        try{
            return new ResponseEntity<>(ParticipantResponseBody.build(
                    participantService.deleteOne(eventCode, participantName)
            ), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("")
    public ResponseEntity<ParticipantResponseBody> updateOneById(
            @RequestParam("name") String name,
            @PathVariable("eventCode") String eventCode,
            @RequestBody ParticipantRequestBody body
    ){
        try{
            return new ResponseEntity<>(ParticipantResponseBody.build(
                    participantService.updateOne(eventCode, name, body)
            ), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
