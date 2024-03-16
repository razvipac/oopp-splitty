package server.database;

import commons.Debt;
import commons.DebtId;
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
            "WHERE d.received = true " +
            "AND d.id.debtor.pkey.event.code = :eventCode")
    Collection<Debt> findAllSettledDebtsForEvent(@Param("eventCode") String eventCode);
}