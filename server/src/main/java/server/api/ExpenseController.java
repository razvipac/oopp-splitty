package server.api;

import commons.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ExpenseRepository;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<Expense>> getAll(@PathVariable("eventCode") String eventCode){
        throw new RuntimeException("To be implemented");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getOneById(
            @PathVariable("id") Long id
    ){
        throw new RuntimeException("To be implemented");
    }

    @GetMapping("/{eventCode}")
    public ResponseEntity<Expense> getAllInEvent(
            @PathVariable("eventCode") String eventCode
    ){
        throw new RuntimeException("To be implemented");
    }

    @GetMapping("/{eventCode}/{id}")
    public ResponseEntity<Expense> getOneFromEventById(
            @PathVariable("eventCode") String eventCode,
            @PathVariable("id") Long id
    ){
        throw new RuntimeException("To be implemented");
    }

    @PostMapping("")
    public ResponseEntity<Expense> createExpense(@RequestBody ExpenseBody body){
        throw new RuntimeException("To be implemented");
    }

    @PostMapping("")
    public ResponseEntity<List<Expense>> createExpenses(@RequestBody List<ExpenseBody> body){
        throw new RuntimeException("To be implemented");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> deleteOneById(@PathVariable("id") Long id){
        throw new RuntimeException("To be implemented");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateOneById(@PathVariable("id") Long id){
        throw new RuntimeException("To be implemented");
    }
}


