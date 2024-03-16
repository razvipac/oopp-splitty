package server.api;

import commons.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.api.pojo.request_body.ExpenseRequestBody;
import server.api.pojo.response_body.ExpenseResponseBody;
import server.api.pojo.response_body.WSAction;
import server.api.pojo.response_body.WSWrapperResponseBody;
import server.service.ExpenseService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.List;

@RestController
@RequestMapping("api/v1/{eventCode}/expense")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor for initializing the ExpenseController.
     *
     * @param expenseService        The ExpenseService instance to be used.
     * @param simpMessagingTemplate The SimpMessagingTemplate instance to be used.
     */
    @Autowired
    public ExpenseController(ExpenseService expenseService,
                             SimpMessagingTemplate simpMessagingTemplate) {
        this.expenseService = expenseService;
        this.simpMessagingTemplate = simpMessagingTemplate;
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
    @GetMapping("")
    public ResponseEntity<List<ExpenseResponseBody>> getAllOrOne(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "participantName", required = false) String participantName,
            @PathVariable("eventCode") String eventCode
    ) {
        if (id == null && participantName == null) {
            List<Expense> expenses = expenseService.getAllInEvent(eventCode);
            List<ExpenseResponseBody> responseBodies = expenses.stream()
                                                               .map(ExpenseResponseBody::build)
                                                               .toList();
            return new ResponseEntity<>(responseBodies, HttpStatus.OK);
        }

        try {
            return new ResponseEntity<>(List.of(
                    ExpenseResponseBody.build(expenseService.getOne(eventCode, participantName, id))
            ), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
//
//    @MessageMapping("v1/{eventCode}/expense")
//    @SendToUser("/api/websocket/v1/channel/{eventCode}/expense")
//    public WSWrapperResponseBody<List<ExpenseResponseBody>> getAll(
//            @DestinationVariable("eventCode") String eventCode
//    ){
//        List<Expense> expenses = expenseService.getAllInEvent(eventCode);
//        List<ExpenseResponseBody> responseBodies = expenses.stream()
//        .map(ExpenseResponseBody::build).toList();
//        return new WSWrapperResponseBody<>(WSAction.RESPONDED, responseBodies);
//    }

    /**
     * POST api/v1/{eventCode}/expense with request body in format of ExpenseRequestBody
     * creates a new Expense populated with data from body under an event with {eventCode}
     * <p>
     * Sends out a WebSocket STOMP message to all listeners
     * on "/api/websocket/v1/channel/{eventCode}/expense with WSAction CREATED
     *
     * @param eventCode The event code.
     * @param body      The ExpenseRequestBody containing data for creating the expense.
     * @return A ResponseEntity containing the ExpenseResponseBody of the created expense
     *         if successful, or a NOT_FOUND response if the event is not found.
     */
    @PostMapping("")
    public ResponseEntity<ExpenseResponseBody> createOne(
            @PathVariable("eventCode") String eventCode,
            @RequestBody ExpenseRequestBody body
    ) {
        try {
            Expense expense = expenseService.createOne(eventCode, body);
            ExpenseResponseBody responseBody = ExpenseResponseBody.build(expense);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/expense",
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
     * DELETE api/v1/{eventCode}/expense?id={id}&participantName={name}
     * deletes expense belonging to a Participant with name {name} and id {id} from
     * event with code {eventCode}
     * <p>
     * Sends out a WebSocket STOMP message to all listeners
     * on "/api/websocket/v1/channel/{eventCode}/expense with WSAction DELETED
     *
     * @param id              The ID of the expense.
     * @param participantName The name of the participant.
     * @param eventCode       The event code.
     * @return A ResponseEntity containing the ExpenseResponseBody of the deleted expense
     *         if successful, or a NOT_FOUND response if the expense is not found.
     */
    @DeleteMapping("")
    public ResponseEntity<ExpenseResponseBody> deleteOne(
            @RequestParam("id") Long id,
            @RequestParam("participantName") String participantName,
            @PathVariable("eventCode") String eventCode
    ) {
        try {
            Expense expense = expenseService.deleteOne(eventCode, participantName, id);
            ExpenseResponseBody responseBody = ExpenseResponseBody.build(expense);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/expense",
                    new WSWrapperResponseBody<>(
                            WSAction.DELETED,
                            responseBody
                    ));

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
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
    @PutMapping("")
    public ResponseEntity<ExpenseResponseBody> updateOneById(
            @RequestParam("id") Long id,
            @PathVariable("eventCode") String eventCode,
            @RequestBody ExpenseRequestBody body
    ) {
        try {
            Expense updated = expenseService.updateOne(eventCode, id, body);
            ExpenseResponseBody response = ExpenseResponseBody.build(updated);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/expense",
                    new WSWrapperResponseBody<>(
                            WSAction.MODIFIED,
                            response
                    ));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}

