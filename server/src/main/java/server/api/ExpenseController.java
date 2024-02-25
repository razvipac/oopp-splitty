package server.api;

import commons.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.ExpenseService;
import server.service.NotFoundInDatabaseException;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("")
    public ResponseEntity<List<Expense>> getAll(){
        return new ResponseEntity<>(expenseService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getOneById(
            @PathVariable("id") Long id
    ){
        try{
            return new ResponseEntity<>(expenseService.getOneById(id), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{eventCode}/{id}")
    public ResponseEntity<Expense> getOneFromEventById(
            @PathVariable("eventCode") String eventCode,
            @PathVariable("id") Long id
    ){
        throw new RuntimeException("To be implemented");
    }

    @PostMapping("")
    public ResponseEntity<Expense> createExpense(
            @RequestBody ExpenseBody body
    ){
        return new ResponseEntity<>(expenseService.create(body), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> deleteOneById(
            @PathVariable("id") Long id
    ){
        try{
            return new ResponseEntity<>(expenseService.deleteById(id), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateOneById(
            @PathVariable("id") Long id,
            @RequestBody ExpenseBody body
    ){
        try{
            return new ResponseEntity<>(expenseService.updateById(id, body), HttpStatus.OK);
        } catch (NotFoundInDatabaseException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}


