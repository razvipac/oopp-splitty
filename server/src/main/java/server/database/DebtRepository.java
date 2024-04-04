package server.database;

import server.api.entities.Debt;
import server.api.entities.DebtId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DebtRepository extends CrudRepository<Debt, DebtId> {

    /**
     * Retrieves a list of settled debts for a specific event
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A list of settled debts for the specified event
     */
    @Query("SELECT d FROM Debt d " +
            "WHERE d.received = false " +
            "AND d.id.debtor.pkey.event.code = :eventCode")
    Collection<Debt> findAllUnsettledDebtsForEvent(@Param("eventCode") String eventCode);

//    @Query("SELECT d FROM Debt d WHERE e.pkey.paidBy.pkey.event.code = :eventCode")
//    Collection<Debt> findAllDebtsInEvent(@Param("eventCode") String eventCode);
}