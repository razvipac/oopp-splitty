package server.database;

import commons.Debt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends CrudRepository<Debt, Long> {

    //@Query("SELECT d FROM Debt d WHERE d.received = true AND d.debtor.pkey.event.code = :eventCode")
    List<Debt> findAllSettledDebtsForEvent(@Param("eventCode") String eventCode);
}