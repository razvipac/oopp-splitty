package server.api;

import commons.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.request_bodies.ExpenseBody;
import server.service.ExpenseService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.List;

@RestController
@RequestMapping("api/v1/{eventCode}/expense")
public class ExpenseController {
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

//    @GetMapping("")
//    public ResponseEntity<List<Expense>> getAllInEvent(
//            @PathVariable("eventCode") String eventCode
//    ){
//        return new ResponseEntity<>(expenseService.getAllInEvent(eventCode), HttpStatus.OK);
//    }

    /**
     * GET api/v1/{eventCode}/expense?id={id}&participantName={name}
     * id and participantName are optional
     * if any of them is omitted all Expenses of event with {eventCode} will be returned
     * if they are both given a Expense belonging to a participant with name {name} of Event with {eventCode}
     * and with id {id} will be returned
     */
    @GetMapping("")
    public ResponseEntity<List<Expense>> getAllOrOne(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "participantName", required = false) String participantName,
            @PathVariable("eventCode") String eventCode
    ) {
        if (id == null && participantName == null){
            return new ResponseEntity<>(expenseService.getAllInEvent(eventCode), HttpStatus.OK);
        }

        try{
            return new ResponseEntity<>(List.of(expenseService.getOne(eventCode, participantName, id)), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST api/v1/{eventCode}/expense with request body in format of ExpenseBody
     * creates a new Expense populated with data from body under an event with {eventCode}
     */
    @PostMapping("")
    public ResponseEntity<Expense> createOne(
            @PathVariable("eventCode") String eventCode,
            @RequestBody ExpenseBody body
    ){
        try{
            return new ResponseEntity<>(expenseService.createOne(eventCode, body), HttpStatus.CREATED);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE api/v1/{eventCode}/expense?id={id}&participantName={name}
     * deletes expense belonging to a Participant with name {name} and id {id} from
     * event with code {eventCode}
     */
    @DeleteMapping("")
    public ResponseEntity<Expense> deleteOne(
            @RequestParam("id") Long id,
            @RequestParam("participantName") String participantName,
            @PathVariable("eventCode") String eventCode
    ){
        try{
            return new ResponseEntity<>(expenseService.deleteOne(eventCode, participantName, id), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT api/v1/{eventCode}/expense?id={id} with request body in format of ExpenseBody
     * Updates the data of expense object with id {id} under event with code {eventCode}
     * Overwrites data with that in passed body, YOU CANNOT CHANGE THE NAME OF THE PARTICIPANT
     */
    @PutMapping("")
    public ResponseEntity<Expense> updateOneById(
            @RequestParam("id") Long id,
            @PathVariable("eventCode") String eventCode,
            @RequestBody ExpenseBody body
    ){
        try{
            return new ResponseEntity<>(expenseService.updateOne(eventCode, id, body), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}


