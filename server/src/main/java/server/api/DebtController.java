package server.api;

import commons.Debt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.api.pojo.request_body.DebtRequestBody;
import server.api.pojo.response_body.DebtResponseBody;
import server.api.pojo.response_body.WSAction;
import server.api.pojo.response_body.WSWrapperResponseBody;
import server.service.DebtService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.List;

@RestController
@RequestMapping("api/v1/events/{eventCode}/debts")
public class DebtController {

    private final DebtService debtService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructs a DebtController with the specified DebtService and SimpMessagingTemplate
     *
     * @param debtService           The service for managing Debt entities
     * @param simpMessagingTemplate The template for sending WebSocket messages
     */
    @Autowired
    public DebtController(DebtService debtService, SimpMessagingTemplate simpMessagingTemplate) {
        this.debtService = debtService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Retrieves all settled debts for a specific event.
     * Sends out a WebSocket STOMP message to all listeners on "/api/websocket/v1/{eventCode}/debts"
     * with WSAction RESPONDED.
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A ResponseEntity containing the list of settled debts and HTTP status OK
     */
    @GetMapping
    public ResponseEntity<List<Debt>> getAllUnsettledDebts(@PathVariable String eventCode) {
        List<Debt> unsettledDebts = debtService.getAllUnsettledDebtsForEvent(eventCode);

        simpMessagingTemplate.convertAndSend("/api/websocket/v1/{eventCode}/debts",
                new WSWrapperResponseBody<>(
                        WSAction.RESPONDED,
                        unsettledDebts
                ));

        return new ResponseEntity<>(unsettledDebts, HttpStatus.OK);
    }

    /**
     * PUT api/v1/{eventCode}/debt?id={id} with request body in format of ExpenseRequestBody
     * Updates the data of debt object with code {eventCode}
     * Changes if the debt is paid or not: from true to false, and from false to true
     * Sends out a WebSocket STOMP message to all listeners
     * on "/api/websocket/v1/channel/{eventCode}/debt with WSAction MODIFIED
     * @param eventCode todo
     * @param body todo
     * @return ResponseEntity with changed received status if it is found
     */
    @PutMapping("")
    public ResponseEntity<DebtResponseBody> updateUnsettledDebt(
            @PathVariable("eventCode") String eventCode,
            @RequestBody DebtRequestBody body
    ) {
        try {
            Debt updated = debtService.updateOne(eventCode, body);
            DebtResponseBody response = DebtResponseBody.build(updated);

            simpMessagingTemplate.convertAndSend(
                    "/api/websocket/v1/channel/" + eventCode + "/debt",
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