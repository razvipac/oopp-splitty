package server.api;

import commons.dto.ExpenseDTO;
import commons.dto.WSAction;
import commons.dto.WSWrapperResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.entities.DTOMapper;
import server.entities.expense.Expense;
import server.service.ExpenseService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.List;

@Controller
@RestController
@RequestMapping("api/v1/{eventCode}/expense")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DTOMapper<Expense, ExpenseDTO> expenseDTOMapper;

    /**
     * Constructor for initializing the ExpenseController.
     *
     * @param expenseService        The ExpenseService instance to be injected.
     * @param simpMessagingTemplate The SimpMessagingTemplate instance to be injected.
     * @param expenseDTOMapper The ExpenseDTOMapper instance to be injected.
     */
    @Autowired
    public ExpenseController(@Autowired ExpenseService expenseService,
                             @Autowired SimpMessagingTemplate simpMessagingTemplate,
                             @Autowired DTOMapper<Expense, ExpenseDTO> expenseDTOMapper) {
        this.expenseService = expenseService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.expenseDTOMapper = expenseDTOMapper;
    }

    /**
     * GET api/v1/{eventCode}/expense?id={id}&participantName={name}
     * id and participantName are optional
     * If any of them is omitted all Expenses of event with {eventCode} will be returned
     * If they are both given an Expense belonging to a participant with name {name} of Event
     * with {eventCode} and with id {id} will be returned
     *
     * @param id               The ID of the expense (optional).
     * @param participantName  The name of the participant (optional).
     * @param eventCode        The event code.
     * @return                 A ResponseEntity containing a list of ExpenseResponseBody objects
     *                         if id and participantName are omitted
     *                         or a single ExpenseResponseBody
     *                         object if both id and participantName are provided.
     */
    @ResponseBody
    @GetMapping("")
    public ResponseEntity<List<ExpenseDTO>> getAllOrOne(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "participantName", required = false) String participantName,
            @PathVariable("eventCode") String eventCode
    ) {
        if (id == null && participantName == null) {
            List<Expense> expenses = expenseService.getAllInEvent(eventCode);
            List<ExpenseDTO> expenseDTOs = expenses.stream()
                    .map(expenseDTOMapper::toDTO)
                    .toList();
            return new ResponseEntity<>(expenseDTOs, HttpStatus.OK);
        }

        try {
            return new ResponseEntity<>(List.of(
                    expenseDTOMapper.toDTO(
                            expenseService.getOne(eventCode, participantName, id)
                    )
            ), HttpStatus.OK);

        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST api/v1/{eventCode}/expense with request body in format of ExpenseDTO
     * creates a new Expense populated with data from body under an event with {eventCode}
     * ID gets automatically generated
     * <p>
     * Sends out a WebSocket STOMP message to all listeners
     * on "/api/websocket/v1/channel/{eventCode}/expense with WSAction CREATED
     *
     * @param eventCode The event code.
     * @param expenseDTO The ExpenseDTO containing data for creating the expense.
     * @return A ResponseEntity containing the ExpenseResponseBody of the created expense
     *         if successful, or a NOT_FOUND response if the event or participant is not found.
     */
    @ResponseBody
    @PostMapping("")
    public ResponseEntity<ExpenseDTO> createOne(
            @PathVariable("eventCode") String eventCode,
            @RequestBody ExpenseDTO expenseDTO
    ) {
        try {
            Expense expense = expenseService.createOne(eventCode, expenseDTO);
            ExpenseDTO createdDTO = expenseDTOMapper.toDTO(expense);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/expense",
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
     * DELETE api/v1/{eventCode}/expense?id={id}&participantName={name}
     * deletes expense belonging to a Participant with name {name} and id {id} from
     * event with code {eventCode}
     * Sends out a WebSocket STOMP message to all listeners
     * on "/api/websocket/v1/channel/{eventCode}/expense with WSAction DELETED
     *
     * @param id              The ID of the expense.
     * @param participantName The name of the participant.
     * @param eventCode       The event code.
     * @return A ResponseEntity containing the ExpenseResponseBody of the deleted expense
     *         if successful, or a NOT_FOUND response if the expense is not found.
     */
    @ResponseBody
    @DeleteMapping("")
    public ResponseEntity<ExpenseDTO> deleteOne(
            @RequestParam("id") Long id,
            @RequestParam("participantName") String participantName,
            @PathVariable("eventCode") String eventCode
    ) {
        try {
            Expense expense = expenseService.deleteOne(eventCode, participantName, id);
            ExpenseDTO expenseDTO = expenseDTOMapper.toDTO(expense);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/expense",
                    new WSWrapperResponseBody<>(
                            WSAction.DELETED,
                            expenseDTO
                    ));

            return new ResponseEntity<>(expenseDTO, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT api/v1/{eventCode}/expense?id={id} with request body in format of ExpenseRequestBody
     * Updates the data of expense object with id {id} under event with code {eventCode}
     * Overwrites data with that in passed body, YOU CANNOT CHANGE THE NAME OF THE PARTICIPANT
     * <p>
     * Sends out a WebSocket STOMP message to all listeners
     * on "/api/websocket/v1/channel/{eventCode}/expense with WSAction MODIFIED
     *
     * @param id         The ID of the expense.
     * @param eventCode  The event code.
     * @param body       The ExpenseRequestBody containing data for updating the expense.
     * @return A ResponseEntity containing the ExpenseResponseBody of the updated expense
     *         if successful, or a NOT_FOUND response if the expense is not found.
     */
    @ResponseBody
    @PutMapping("")
    public ResponseEntity<ExpenseDTO> updateOneById(
            @RequestParam("id") Long id,
            @PathVariable("eventCode") String eventCode,
            @RequestBody ExpenseDTO body
    ) {
        try {
            Expense updated = expenseService.updateOne(eventCode, id, body);
            ExpenseDTO expenseDTO = expenseDTOMapper.toDTO(updated);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/expense",
                    new WSWrapperResponseBody<>(
                            WSAction.MODIFIED,
                            expenseDTO
                    ));

            return new ResponseEntity<>(expenseDTO, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

