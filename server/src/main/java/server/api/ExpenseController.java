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

    @GetMapping("")
    public ResponseEntity<List<Expense>> getAllOrOne(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "participantName", required = false) String participantName,
            @PathVariable("eventCode") String eventCode
            ){
        if (id == null && participantName == null){
            return new ResponseEntity<>(expenseService.getAllInEvent(eventCode), HttpStatus.OK);
        }

        try{
            return new ResponseEntity<>(List.of(expenseService.getOne(eventCode, participantName, id)), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

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


