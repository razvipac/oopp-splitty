package server.api;

import commons.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.entities.DTOMapper;
import server.entities.debt.Debt;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.participant.Participant;
import server.service.JSONDumpService;
import server.service.exceptions.ImproperDumpFormatException;

import java.util.List;


@RestController
@RequestMapping("api/v1/admin/jsondump")
public class JSONDumpController {
    private final JSONDumpService jsonDumpService;
    private final DTOMapper<Event, EventDTO> eventDTOMapper;
    private final DTOMapper<Participant, ParticipantDTO> participantDTOMapper;
    private final DTOMapper<Expense, ExpenseDTO> expenseDTOMapper;
    private final DTOMapper<Debt, DebtDTO> debtDTOMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructs a JSONDumpController with the specified JSONDumpService.
     *
     * @param jsonDumpService The JSONDumpService instance to be injected.
     * @param eventDTOMapper The EventDTOMapper instance to be injected.
     * @param participantDTOMapper The ParticipantDTOMapper instance to be injected.
     * @param expenseDTOMapper The ExpenseDTOMapper instance to be injected.
     * @param debtDTOMapper The DebtDTOMapper instance to be injected.
     * @param simpMessagingTemplate The SimpMessaging template to be injected.
     */
    public JSONDumpController(
            @Autowired JSONDumpService jsonDumpService,
            @Autowired DTOMapper<Event, EventDTO> eventDTOMapper,
            @Autowired DTOMapper<Participant, ParticipantDTO> participantDTOMapper,
            @Autowired DTOMapper<Expense, ExpenseDTO> expenseDTOMapper,
            @Autowired DTOMapper<Debt, DebtDTO> debtDTOMapper,
            @Autowired SimpMessagingTemplate simpMessagingTemplate
    ) {
        this.jsonDumpService = jsonDumpService;
        this.eventDTOMapper = eventDTOMapper;
        this.participantDTOMapper = participantDTOMapper;
        this.expenseDTOMapper = expenseDTOMapper;
        this.debtDTOMapper = debtDTOMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * GET api/v1/admin/jsondump
     * Returns a JSON object corresponding to the current state of the server
     *
     * @return A ResponseEntity containing a list of EventResponseBody objects if successful.
     *         Returns HttpStatus.OK if successful.
     */
    @GetMapping("")
    public ResponseEntity<List<JSONDumpEventDTO>> getJSONDump(){
        return new ResponseEntity<>(jsonDumpService.createDump(), HttpStatus.OK);
    }

    /**
     * POST api/v1/admin/jsondump with a request body in List<JSONDumpEventDTO> format
     * Restores the server to the state in accordance with the passed JSON object
     *
     * @param body The request body in List<JSONDumpEventDTO> format.
     * @return A ResponseEntity with a status message.
     *         Returns "Restored Successfully" if the restoration is successful (HttpStatus.OK).
     *         Returns "Improper JSON dump format!" if the JSON dump format
     *         is improper (HttpStatus.NOT_MODIFIED).
     *         Returns "Unknown Error!" if an unknown error occurs
     *         (HttpStatus.INTERNAL_SERVER_ERROR).
     */
    @PostMapping("/server")
    public ResponseEntity<String> restoreFromJSONDump(
            @RequestBody List<JSONDumpEventDTO> body
    ){
        try {
            jsonDumpService.restoreFromDump(body);
            return new ResponseEntity<>("Restored Successfully", HttpStatus.OK);
        } catch (ImproperDumpFormatException e){
            return new ResponseEntity<>("Improper JSON dump format!", HttpStatus.NOT_MODIFIED);
        } catch (Exception e){
            return new ResponseEntity<>("Unknown Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param body of the event
     * @return an event created from the body
     */
    @PostMapping("")
    public ResponseEntity<String> restoreEventFromJSONDump(
            @RequestBody JSONDumpEventDTO body
    ){
        try {
            jsonDumpService.restoreEventFromDump(body);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/event",
                    new WSWrapperResponseBody<>(
                            WSAction.CREATED,
                            body
                    )
            );

            return new ResponseEntity<>("Restored Successfully", HttpStatus.OK);
        } catch (ImproperDumpFormatException e){
            return new ResponseEntity<>("Improper JSON dump format!", HttpStatus.NOT_MODIFIED);
        } catch (Exception e){
            return new ResponseEntity<>("Unknown Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
