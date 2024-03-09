package server.api;

import commons.Debt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.api.pojo.response_body.WSAction;
import server.api.pojo.response_body.WSWrapperResponseBody;
import server.service.DebtService;

import java.util.List;

@RestController
@RequestMapping("api/v1/events/{eventCode}/debts")
public class DebtController {

    private final DebtService debtService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public DebtController(DebtService debtService, SimpMessagingTemplate simpMessagingTemplate) {
        this.debtService = debtService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * READ /api/v1/events/{eventCode}/debts
     * Retrieves all settled debts for a specific event
     * Sends out a WebSocket STOMP message to all listeners on "/api/websocket/v1/{eventCode}/debts
     * with WSAction RESPONDED
     */
    @GetMapping
    public ResponseEntity<List<Debt>> getAllSettledDebts(@PathVariable String eventCode) {
        List<Debt> settledDebts = debtService.getAllSettledDebtsForEvent(eventCode);

        simpMessagingTemplate.convertAndSend("/api/websocket/v1/{eventCode}/debts",
                new WSWrapperResponseBody<>(
                        WSAction.RESPONDED,
                        settledDebts
                ));

        return new ResponseEntity<>(settledDebts, HttpStatus.OK);
    }
}