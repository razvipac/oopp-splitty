package server.service;

import commons.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.api.ExpenseBody;
import server.database.ExpenseRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getAll(){
        List<Expense> result = new LinkedList<>();
        expenseRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    public Expense getOneById(Long id) throws NotFoundInDatabaseException {
        Optional<Expense> search = expenseRepository.findById(id);

        if (search.isPresent()){
            return search.get();
        }
        throw new NotFoundInDatabaseException("Expense was not found in the database");
    }

    public Expense create(ExpenseBody body){
        // TODO: fetch participant

        Expense newExpense = new Expense(
                body.price(),
                body.item()
        );

        expenseRepository.save(newExpense);
        return newExpense;
    }

    public Expense deleteById(Long id) throws NotFoundInDatabaseException {
        Expense found = getOneById(id);
        // if not found exception will be thrown
        expenseRepository.deleteById(id);
        return found;
    }

    public Expense updateById(Long id, ExpenseBody body) throws NotFoundInDatabaseException {
        Expense found = getOneById(id);
        // if not found exception will be thrown
        found.setItem(body.item());
        found.setPrice(body.price());
        // TODO: set participant, fetch and add

        expenseRepository.save(found);
        return found;
    }
}
