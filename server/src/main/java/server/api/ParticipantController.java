package server.api;

import commons.dto.ParticipantDTO;
import commons.dto.WSAction;
import commons.dto.WSWrapperResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.DebtRepository;
import server.entities.DTOMapper;
import server.entities.debt.DebtDTOMapper;
import server.entities.expense.Expense;
import server.entities.participant.Participant;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/{eventCode}/participant")
public class ParticipantController {
    private final ParticipantService participantService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DTOMapper<Participant, ParticipantDTO> participantDTOMapper;
    private final ExpenseController expenseController;
    private final DebtController debtController;
    private final DebtRepository debtRepository;
    private final DebtDTOMapper debtDTOMapper;

    /**
     * Constructs a new instance of ParticipantController.
     *
     * @param participantService    The ParticipantService instance to handle participant-related operations
     * @param simpMessagingTemplate The SimpMessagingTemplate instance to send WebSocket messages
     * @param participantDTOMapper  The ParticipantDTOMapper instance to be injected
     * @param expenseController     The ExpenseController instance to be injected
     * @param debtController        The DebtController instance to be injected
     * @param debtRepository        The DebtRepository instance to be injected
     * @param debtDTOMapper         The DebtDTOMapper instance to be injected
     */
    public ParticipantController(@Autowired ParticipantService participantService,
                                 @Autowired SimpMessagingTemplate simpMessagingTemplate,
                                 @Autowired DTOMapper<Participant, ParticipantDTO> participantDTOMapper,
                                 @Autowired ExpenseController expenseController,
                                 @Autowired DebtController debtController,
                                 @Autowired DebtRepository debtRepository,
                                 @Autowired DebtDTOMapper debtDTOMapper) {
        this.participantService = participantService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.participantDTOMapper = participantDTOMapper;
        this.expenseController = expenseController;
        this.debtController = debtController;
        this.debtRepository = debtRepository;
        this.debtDTOMapper = debtDTOMapper;
    }

    /**
     * GET api/v1/{eventCode}/participant?name={name}
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
    public ResponseEntity<List<ParticipantDTO>> getAllOrOne(
            @PathVariable(value = "eventCode") String eventCode,
            @RequestParam(value = "name", required = false) String name) {
        if (name == null) {
            List<Participant> participants = participantService.getAll(eventCode);
            List<ParticipantDTO> participantDTOs = participants.stream()
                    .map(participantDTOMapper::toDTO)
                    .toList();
            return new ResponseEntity<>(participantDTOs, HttpStatus.OK);
        }

        try {
            return new ResponseEntity<>(List.of(
                    participantDTOMapper.toDTO(
                            participantService.getOne(eventCode, name)
                    )), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST /api/v1/{eventCode}/participant with body in ParticipantDTO format
     * Creates a new Participant populated with the data in body
     * <p>
     * Sends out a WebSocket STOMP message to all listeners
     * on "/api/websocket/v1/channel/{eventCode}/participant
     * with WSAction CREATED
     *
     * @param eventCode      The event code
     * @param participantDTO The ParticipantDTO instance
     * @return ResponseEntity with ParticipantDTO or HttpStatus.NOT_FOUND if not found
     */
    @PostMapping
    public ResponseEntity<ParticipantDTO> createOne(
            @PathVariable("eventCode") String eventCode,
            @RequestBody ParticipantDTO participantDTO) {
        try {
            Participant participant = participantService.createOne(eventCode, participantDTO);
            ParticipantDTO createdDTO = participantDTOMapper.toDTO(participant);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/participant",
                    new WSWrapperResponseBody<>(
                            WSAction.CREATED,
                            createdDTO
                    ));

            return new ResponseEntity<>(createdDTO, HttpStatus.CREATED);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE /api/v1/{eventCode}/participant?name={participant name}
     * Deletes a Participant specified by the name and eventCode
     *
     * @param participantName The participant's name
     * @param eventCode       The event code
     * @return ResponseEntity with ParticipantDTO or HttpStatus.NOT_FOUND if not found
     */
    @DeleteMapping("")
    public ResponseEntity<ParticipantDTO> deleteOne(
            @RequestParam("name") String participantName,
            @PathVariable("eventCode") String eventCode
    ) {
        try {
            Participant participant = participantService.getOne(eventCode, participantName);
            ParticipantDTO participantDTO = participantDTOMapper.toDTO(participant);

            deleteDependants(participant);

            participantService.deleteOne(eventCode, participantName);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/participant",
                    new WSWrapperResponseBody<>(
                            WSAction.DELETED,
                            participantDTO
                    ));

            return new ResponseEntity<>(participantDTO, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /api/v1/{eventCode}/participant?name={name} with parameter name and body
     * in ParticipantDTO format
     * Updates a Participant specified by the eventCode and name with data in the body
     *
     * @param name      The participant's name
     * @param eventCode The event code
     * @param body      The ParticipantDTO that includes the changes that are being made
     * @return ResponseEntity with ParticipantDTO or HttpStatus.NOT_FOUND if not found
     */
    @PutMapping("")
    public ResponseEntity<ParticipantDTO> updateOneById(
            @RequestParam("name") String name,
            @PathVariable("eventCode") String eventCode,
            @RequestBody ParticipantDTO body) {
        try {
            Participant updated = participantService.updateOne(eventCode, name, body);
            ParticipantDTO participantDTO = participantDTOMapper.toDTO(updated);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/participant",
                    new WSWrapperResponseBody<>(
                            WSAction.MODIFIED,
                            participantDTO
                    ));

            return new ResponseEntity<>(participantDTO, HttpStatus.OK);

        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private void deleteDependants(Participant participant) {
        if (participant != null) {
            List<Expense> toBeDeleted = new ArrayList<>(participant.getPaidForExpenses());
            for (Expense expense : toBeDeleted) {
                expenseController.deleteOne(
                        expense.getId(),
                        expense.getPaidBy().getName(),
                        participant.getEvent().getCode()
                );
            }
            debtController.generateDebts(participant.getEvent().getCode());
        }
    }
}
