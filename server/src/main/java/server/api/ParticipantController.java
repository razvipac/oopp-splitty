package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    /**
     * Constructor for ParticipantController
     *
     * @param participantService    The ParticipantService instance
     *                              to handle participant-related operations
     * @param simpMessagingTemplate The SimpMessagingTemplate instance to send WebSocket messages
     */
    public ParticipantController(@Autowired ParticipantService participantService,
                                 @Autowired SimpMessagingTemplate simpMessagingTemplate) {
        this.participantService = participantService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * GET api/v1/{eventCode}/expense?name={name}
     * name is optional
     * if it is omitted all Participants of event with {eventCode} will be returned
     * if it is given a Participant belonging to an Event
     * with {eventCode} and given name will be returned
     *
     * @param eventCode The event code
     * @param name      The participant's name (optional)
     * @return ResponseEntity with a list of ParticipantResponseBody
     * or HttpStatus.NOT_FOUND if not found
     */
    @GetMapping
    public ResponseEntity<List<ParticipantResponseBody>> getAllOrOne(
            @PathVariable(value = "eventCode") String eventCode,
            @RequestParam(value = "name", required = false) String name) {
        if (name == null) {
            List<Participant> participants = participantService.getAll(eventCode);
            List<ParticipantResponseBody> participantResponseBodies = participants.stream()
                                                    .map(ParticipantResponseBody::build).toList();
            return new ResponseEntity<>(participantResponseBodies, HttpStatus.OK);
        }

        try {
            return new ResponseEntity<>(List.of(
                    ParticipantResponseBody.build(
                            participantService.getOne(eventCode, name)
                    )), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST /api/v1/{eventCode}/participant with body in ParticipantRequestBody format
     * Creates a new Participant populated with the data in body
     * <p>
     * Sends out a WebSocket STOMP message to all listeners
     * on "/api/websocket/v1/channel/{eventCode}/participant
     * with WSAction CREATED
     *
     * @param eventCode The event code
     * @param body      The ParticipantRequestBody instance
     * @return ResponseEntity with ParticipantResponseBody or HttpStatus.NOT_FOUND if not found
     */
    @PostMapping
    public ResponseEntity<ParticipantResponseBody> createOne(
            @PathVariable("eventCode") String eventCode,
            @RequestBody ParticipantRequestBody body) {
        try {
            Participant participant = participantService.createOne(eventCode, body);
            ParticipantResponseBody responseBody = ParticipantResponseBody.build(participant);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/participant",
                    new WSWrapperResponseBody<>(
                            WSAction.CREATED,
                            responseBody
                    ));

            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE /api/v1/{eventCode}/participant with parameter participantName
     * Deletes a Participant specified by the participantName and eventCode
     *
     * @param participantName The participant's name
     * @param eventCode       The event code
     * @return ResponseEntity with ParticipantResponseBody or HttpStatus.NOT_FOUND if not found
     */
    @DeleteMapping("")
    public ResponseEntity<ParticipantResponseBody> deleteOne(
            @RequestParam("participantName") String participantName,
            @PathVariable("eventCode") String eventCode) {
        try {
            return new ResponseEntity<>(ParticipantResponseBody.build(
                    participantService.deleteOne(eventCode, participantName)
            ), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /api/v1/{eventCode}/participant with parameter name and body
     * in ParticipantRequestBody format
     * Updates a Participant specified by the eventCode and name with data in the body
     *
     * @param name      The participant's name
     * @param eventCode The event code
     * @param body      The ParticipantRequestBody instance
     * @return ResponseEntity with ParticipantResponseBody or HttpStatus.NOT_FOUND if not found
     */
    @PutMapping("")
    public ResponseEntity<ParticipantResponseBody> updateOneById(
            @RequestParam("name") String name,
            @PathVariable("eventCode") String eventCode,
            @RequestBody ParticipantRequestBody body) {
        try {
            return new ResponseEntity<>(ParticipantResponseBody.build(
                    participantService.updateOne(eventCode, name, body)
            ), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
