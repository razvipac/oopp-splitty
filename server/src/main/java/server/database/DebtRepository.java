package server.database;

import server.entities.debt.Debt;
import server.entities.debt.DebtId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DebtRepository extends CrudRepository<Debt, DebtId> {

    /**
     * Retrieves a list of unsettled debts for a specific event
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A list of unsettled debts for the specified event
     */
    @Query("SELECT d FROM Debt d " +
            "WHERE d.id.debtor.pkey.event.code = :eventCode")
    Collection<Debt> findAllDebts(@Param("eventCode") String eventCode);

    /**
     * Retrieves a list of all debts for a specific event
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A list of all debts for the specified event
     */
    @Query("SELECT d FROM Debt d WHERE d.id.debtor.pkey.event.code = :eventCode")
    Collection<Debt> findAllDebtsInEvent(@Param("eventCode") String eventCode);
}