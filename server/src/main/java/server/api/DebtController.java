package server.api;

import server.entities.DTOMapper;
import server.entities.debt.Debt;
import commons.dto.DebtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.service.DebtService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping("api/v1/{eventCode}/debt")
public class DebtController {

    private final DebtService debtService;
    private final DTOMapper<Debt, DebtDTO> debtDTOMapper;

    /**
     * Constructs a DebtController with the specified DebtService
     *
     * @param debtService The service for managing Debt entities
     * @param debtDTOMapper mapper for the DebtDTO
     */
    @Autowired
    public DebtController(
            @Autowired DebtService debtService,
            @Autowired DTOMapper<Debt, DebtDTO> debtDTOMapper
    ) {
        this.debtService = debtService;
        this.debtDTOMapper = debtDTOMapper;
    }

    /**
     * Retrieves all debts for a specific event.
     *
     * @param eventCode The code of the event for which all debts are to be retrieved
     * @return all debts
     */
    @GetMapping
    public ResponseEntity<List<DebtDTO>> getAll(@PathVariable(value = "eventCode") String eventCode) {
        List<Debt> debts = debtService.getAll(eventCode);
        List<DebtDTO> debtDTOs = debts
                .stream()
                .map(debtDTOMapper::toDTO)
                .toList();
        return new ResponseEntity<>(debtDTOs, HttpStatus.OK);
    }


    /**
     * Retrieves all unsettled debts for a specific event.
     *
     * @param eventCode The code of the event for which unsettled debts are to be retrieved
     * @return A DeferredResult containing the list of unsettled debts
     */
    @GetMapping("/unsettled")
    public DeferredResult<ResponseEntity<List<DebtDTO>>> getAllUnsettledDebts
    (@PathVariable String eventCode) {
        DeferredResult<ResponseEntity<List<DebtDTO>>> output = new DeferredResult<>(300000L);
        output.onTimeout(() -> output.setErrorResult(
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body("Request timed out. Please try again.")));

        ForkJoinPool.commonPool().submit(() -> {
            List<Debt> unsettledDebts = debtService.getAllUnsettledDebtsForEvent(eventCode);
            List<DebtDTO> unsettledDebtDTOs = unsettledDebts.stream()
                    .map(debtDTOMapper::toDTO)
                    .toList();
            output.setResult(new ResponseEntity<>(unsettledDebtDTOs, HttpStatus.OK));
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
    public ResponseEntity<DebtDTO> updateUnsettledDebt(
            @PathVariable("eventCode") String eventCode,
            @RequestBody DebtDTO body
    ) {
        try {
            Debt updated = debtService.updateOne(eventCode, body);
            DebtDTO debtDTO = debtDTOMapper.toDTO(updated);

            return new ResponseEntity<>(debtDTO, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
