package server.api;

import commons.dto.DebtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.entities.DTOMapper;
import server.entities.debt.Debt;
import server.service.DebtService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Controller class handling HTTP requests related to debts in the system.
 * This controller provides endpoints for managing debts associated with a specific event.
 */
@RestController
@RequestMapping("api/v1/{eventCode}/debt")
public class DebtController {

    private final DebtService debtService;
    private final DTOMapper<Debt, DebtDTO> debtDTOMapper;
    private Map<Object, Consumer<List<DebtDTO>>> listeners = new HashMap<>();

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
     * Retrieves all unsettled debts for a specific event asynchronously.
     * This endpoint provides updates on unsettled debts in real-time using long polling.
     *
     * @param eventCode The code of the event for which unsettled debts are to be retrieved.
     * @return A DeferredResult containing the list of unsettled debts.
     */
    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<List<DebtDTO>>> getDebtUpdates(
            @PathVariable String eventCode
    ) {
        DeferredResult<ResponseEntity<List<DebtDTO>>> deferredResult = new DeferredResult<>(5000L);
        var key = new Object();

        listeners.put(key, passedDebtDTOs -> {
            List<DebtDTO> debtDTOs = debtService.getAllDebts(eventCode)
                    .stream()
                    .map(debtDTOMapper::toDTO)
                    .toList();

            deferredResult.setResult(ResponseEntity.ok(debtDTOs));
        });

        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out. Please try again.")
            );
        });

        deferredResult.onCompletion(() -> listeners.remove(key));

        return deferredResult;
    }

    /**
     * Retrieves all debts for a specific event.
     *
     * @param eventCode The code of the event for which debts are to be retrieved.
     * @return ResponseEntity containing the list of debts.
     */
    @GetMapping
    public ResponseEntity<List<DebtDTO>> getAllDebts(
            @PathVariable("eventCode") String eventCode
    ) {
        ResponseEntity<List<DebtDTO>> res = ResponseEntity.ok(
                debtService.getAllDebts(eventCode)
                        .stream()
                        .map(debtDTOMapper::toDTO)
                        .toList()
        );
        return res;
    }

    /**
     * Updates the status of a debt (paid/unpaid) associated with the specified event.
     *
     * @param eventCode The code of the event for which the debt belongs.
     * @param body      The DebtDTO containing the updated information about the debt.
     * @return ResponseEntity with the updated DebtDTO if successful, or NOT_FOUND if the debt is not found.
     */
    @PutMapping("")
    public ResponseEntity<DebtDTO> updateDebt(
            @PathVariable("eventCode") String eventCode,
            @RequestBody DebtDTO body
    ) {
        try {
            Debt updated = debtService.updateOne(eventCode, body);
            DebtDTO debtDTO = debtDTOMapper.toDTO(updated);

            listeners.forEach((k, v) -> v.accept(List.of(debtDTO)));

            return new ResponseEntity<>(debtDTO, HttpStatus.OK);
        } catch (NotFoundInDatabaseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Generates open debts from the expenses on the given server and populates the database with them.
     *
     * @param eventCode The code of the event for which debts are to be generated.
     * @return ResponseEntity containing the list of created debts.
     */
    @PostMapping("")
    public ResponseEntity<List<DebtDTO>> generateDebts(
            @PathVariable("eventCode") String eventCode
    ) {
        List<Debt> generatedDebts = debtService.generateDebtsFromExpenses(eventCode);

        List<DebtDTO> dtos = generatedDebts
                .stream()
                .map(debtDTOMapper::toDTO)
                .toList();

        listeners.forEach((k, v) -> v.accept(dtos));

        return new ResponseEntity<>(
                dtos,
                HttpStatus.CREATED
        );
    }
}
