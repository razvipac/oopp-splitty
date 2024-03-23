package server.api;

import commons.Debt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.api.pojo.request_body.DebtRequestBody;
import server.api.pojo.response_body.DebtResponseBody;
import server.service.DebtService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.List;
import java.util.concurrent.ForkJoinPool;




@RestController
@RequestMapping("api/v1/events/{eventCode}/debts")
public class DebtController {

    private final DebtService debtService;

    /**
     * Constructs a DebtController with the specified DebtService
     *
     * @param debtService The service for managing Debt entities
     */
    @Autowired
    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    /**
     * Retrieves all unsettled debts for a specific event.
     *
     * @param eventCode The code of the event for which unsettled debts are to be retrieved
     * @return A DeferredResult containing the list of unsettled debts
     */
    @GetMapping
    public DeferredResult<ResponseEntity<List<Debt>>> getAllUnsettledDebts
    (@PathVariable String eventCode) {
        DeferredResult<ResponseEntity<List<Debt>>> output = new DeferredResult<>(300000L);
        output.onTimeout(() -> output.setErrorResult(
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body("Request timed out. Please try again.")));

        ForkJoinPool.commonPool().submit(() -> {
            List<Debt> unsettledDebts = debtService.getAllUnsettledDebtsForEvent(eventCode);
            output.setResult(new ResponseEntity<>(unsettledDebts, HttpStatus.OK));
        });

        return output;
    }

    /**
     * PUT api/v1/{eventCode}/debt?id={id} with request body in format of ExpenseRequestBody
     * Updates the data of debt object with code {eventCode}
     * Changes if the debt is paid or not: from true to false, and from false to true
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
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
