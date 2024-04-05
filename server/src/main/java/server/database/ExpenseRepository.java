package server.database;

import server.entities.expense.Expense;
import server.entities.expense.ExpenseId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ExpenseRepository extends CrudRepository<Expense, ExpenseId> {

    /**
     * Fetches all Expenses in a given Event
     * @param eventCode the code of the Event from which to fetch expenses.
     * @return Collection of fetched Expenses
     */
    @Query("SELECT e FROM Expense e WHERE e.pkey.paidBy.pkey.event.code = :eventCode")
    Collection<Expense> findAllExpensesInEvent(@Param("eventCode") String eventCode);

    /**
     * Fetches all Expenses paid by a given Participant in a given Event
     * @param eventCode code of the event
     * @param paidByName name of the participant
     * @return Collection of fetched Expenses
     */
    @Query("SELECT e FROM Expense e " +
            "WHERE e.pkey.paidBy.pkey.event.code = :eventCode " +
            "AND e.pkey.paidBy.pkey.name = :paidByName")
    Collection<Expense> findAllExpensesInEventDependantOnParticipant(
            @Param("eventCode") String eventCode,
            @Param("paidByName") String paidByName);
}

