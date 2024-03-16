package server.service;

import commons.Expense;
import commons.ExpenseId;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.api.pojo.request_body.ExpenseRequestBody;
import server.database.ExpenseRepository;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Handles input and output of saved Expense objects
 */
@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;
    private ParticipantService participantService;

    /**
     * Constructs an ExpenseService instance with
     * the specified ExpenseRepository and ParticipantService.
     *
     * @param expenseRepository  The ExpenseRepository to be injected into the service.
     * @param participantService The ParticipantService to be injected into the service.
     */
    public ExpenseService(
            @Autowired ExpenseRepository expenseRepository,
            @Autowired ParticipantService participantService) {
        this.expenseRepository = expenseRepository;
        this.participantService = participantService;
    }

    /**
     * Fetches all Expenses in a given Event
     *
     * @param eventCode code of the Event to which the Expenses belong
     * @return a LinkedList containing all Expense objects in a given Event
     */
    public List<Expense> getAllInEvent(String eventCode) {
        List<Expense> result = new LinkedList<>();
        expenseRepository.findAllExpensesInEvent(eventCode)
                .iterator()
                .forEachRemaining(result::add);
        return result;
    }

    /**
     * Fetches one Expense object
     *
     * @param eventCode       code of the Event to which the Expense belongs
     * @param participantName name of the owner of the Expense
     * @param id              id of the Expense
     * @return a given Expense object
     * @throws NotFoundInDatabaseException if an owner of the Expense
     *                                     or the specified Event does not exist
     */
    public Expense getOne(String eventCode, String participantName,
                          Long id) throws NotFoundInDatabaseException {

        Optional<Expense> search = expenseRepository.findById(getExpenseId(
                eventCode,
                participantName,
                id
        ));

        if (search.isPresent()) {
            return search.get();
        }
        throw new NotFoundInDatabaseException("Expense was not found in the database");
    }

    /**
     * Creates and saves a new Expense object
     *
     * @param eventCode code of the Event to which the Expense belongs
     * @param body      data to populate the new Expense object with
     * @return the newly created Expense object
     * @throws NotFoundInDatabaseException if an owner of the Expense
     *                                     or the specified Event does not exist
     */
    public Expense createOne(String eventCode,
                             ExpenseRequestBody body) throws NotFoundInDatabaseException {

        Participant paidBy = participantService.getOne(eventCode, body.participantName());

        Expense newExpense = new Expense(body.price(), body.item(), paidBy);

        expenseRepository.save(newExpense);
        return newExpense;
    }

    /**
     * Deletes a specified Expense from the database
     *
     * @param eventCode       code of the Event to which the Expense belongs
     * @param participantName name of the owner of the Expense
     * @param id              id of the Expense
     * @return the deleted Expense object
     * @throws NotFoundInDatabaseException if an owner of the Expense
     *                                     or the specified Event does not exist
     */
    public Expense deleteOne(String eventCode, String participantName, Long id)
            throws NotFoundInDatabaseException {
        Expense found = getOne(eventCode, participantName, id);
        // if not found exception will be thrown

        expenseRepository.deleteById(getExpenseId(
                eventCode,
                participantName,
                id
        ));
        return found;
    }

    /**
     * Updates the data of a given Expense
     *
     * @param eventCode code of the Event to which the Expense belongs
     * @param id        id of the Expense
     * @param body      data to be set as the new value of the given expense.
     *                  **NAME CANNOT BE CHANGED!**
     * @return the updated expense object
     * @throws NotFoundInDatabaseException if an owner of the Expense
     *                                     or the specified Event does not exist
     */
    public Expense updateOne(String eventCode, Long id, ExpenseRequestBody body)
            throws NotFoundInDatabaseException {
        Expense found = getOne(eventCode, body.participantName(), id);
        // if not found exception will be thrown

        found.setItem(body.item());
        found.setPrice(body.price());

        expenseRepository.save(found);
        return found;

    }

    /**
     * Creates a ExpenseId object given arguments
     *
     * @param eventCode       eventCode of the Event to which the Expense belongs
     * @param participantName name of the owner of the expense
     * @param id              id of the Expense
     * @return ExpenseId object created from the given data
     * @throws NotFoundInDatabaseException if a Participant of Event with code eventCode
     *                                     and name participantName does not exist in the database
     */
    private ExpenseId getExpenseId(String eventCode, String participantName, Long id)
            throws NotFoundInDatabaseException {
        Participant paidBy = participantService.getOne(eventCode, participantName);
        return new ExpenseId(id, paidBy);
    }
}
