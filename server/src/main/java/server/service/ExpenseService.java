package server.service;

import commons.Expense;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.api.request_bodies.ExpenseBody;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;
    private ParticipantRepository participantRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ParticipantRepository participantRepository) {
        this.expenseRepository = expenseRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * Fetches a list of all expense objects in the datasource
     * @return list of all expense objects in the datasource
     */
    public List<Expense> getAll(){
        List<Expense> result = new LinkedList<>();
        expenseRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    /**
     * Fetches one expense object from the datasource by it's ID
     *
     * @param id id of the object to be fetched
     * @return the fetched expense object
     * @throws NotFoundInDatabaseException
     * if the object with the passed ID does not exist in the datasource
     */
    public Expense getOneById(Long id) throws NotFoundInDatabaseException {
        Optional<Expense> search = expenseRepository.findById(id);

        if (search.isPresent()){
            return search.get();
        }
        throw new NotFoundInDatabaseException("Expense was not found in the database");
    }

    /**
     * Adds an expense object with data specified in the passed ExpenseBody to
     * the datasource
     *
     * @param body data to populate the new Expense instance with
     * @return the Expense object that got created
     */
    public Expense create(ExpenseBody body){
        Optional<Participant> searchResult = participantRepository.findById(body.participantId());

        if (searchResult.isEmpty()) return new Expense();

        Expense newExpense = new Expense();
        newExpense.setPrice(body.price());
        newExpense.setItem(body.item());
        newExpense.setPaidBy(searchResult.get());

        expenseRepository.save(newExpense);
        return newExpense;
    }

    /**
     * Deletes an Expense object with the passed ID from the datasource
     *
     * @param id ID of the object to be deleted
     * @return the Expense object that got deleted
     * @throws NotFoundInDatabaseException
     * if a object with the passed ID does not exist in the datasource
     */
    public Expense deleteById(Long id) throws NotFoundInDatabaseException {
        Expense found = getOneById(id);
        // if not found exception will be thrown
        expenseRepository.deleteById(id);
        return found;
    }

    /**
     * Modifies the data of an object with a passed ID inside the datasource
     *
     * @param id ID of the object to be modified
     * @param body ExpenseBody object specifying the new state of the modified object
     * @return the modified Expense object
     * @throws NotFoundInDatabaseException
     * if an object with passed ID does not exist in the datasource
     */
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
