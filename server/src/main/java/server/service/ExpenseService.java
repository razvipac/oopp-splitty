package server.service;

import commons.Expense;
import commons.ExpenseId;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.api.request_bodies.ExpenseBody;
import server.database.ExpenseRepository;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;
    private ParticipantService participantService ;

    public ExpenseService(
            @Autowired ExpenseRepository expenseRepository,
            @Autowired ParticipantService participantService) {
        this.expenseRepository = expenseRepository;
        this.participantService = participantService;
    }

    public List<Expense> getAllInEvent(String eventCode){
        List<Expense> result = new LinkedList<>();
        expenseRepository.findAllExpensesInEvent(eventCode).iterator().forEachRemaining(result::add);
        return result;
    }

    public Expense getOne(String eventCode, String participantName,
                              Long id) throws NotFoundInDatabaseException {

        Optional<Expense> search = expenseRepository.findById(getExpenseId(
                eventCode,
                participantName,
                id
        ));

        if (search.isPresent()){
            return search.get();
        }
        throw new NotFoundInDatabaseException("Expense was not found in the database");
    }

    public Expense createOne(String eventCode,
                             ExpenseBody body) throws NotFoundInDatabaseException {

        Participant paidBy = participantService.getOne(eventCode, body.participantName());

        Expense newExpense = new Expense(body.price(), body.item(), paidBy);

        expenseRepository.save(newExpense);
        return newExpense;
    }

    public Expense deleteOne(String eventCode, String participantName, Long id) throws NotFoundInDatabaseException {
        Expense found = getOne(eventCode, participantName, id);
        // if not found exception will be thrown

        expenseRepository.deleteById(getExpenseId(
                eventCode,
                participantName,
                id
        ));
        return found;
    }

    public Expense updateOne(String eventCode, Long id, ExpenseBody body) throws NotFoundInDatabaseException {
        Expense found = getOne(eventCode, body.participantName(), id);
        // if not found exception will be thrown

        found.setItem(body.item());
        found.setPrice(body.price());

        expenseRepository.save(found);
        return found;

    }

    private ExpenseId getExpenseId(String eventCode, String participantName, Long id)
            throws NotFoundInDatabaseException{
        Participant paidBy = participantService.getOne(eventCode, participantName);
        return  new ExpenseId(id, paidBy);
    }
}
