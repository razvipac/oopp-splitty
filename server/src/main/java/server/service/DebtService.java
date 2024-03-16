package server.service;

import commons.Debt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.DebtRepository;

import java.util.LinkedList;
import java.util.List;

@Service
public class DebtService {
    private final DebtRepository debtRepository;

    /**
     * Constructs a DebtService with the specified DebtRepository
     *
     * @param debtRepository The repository for accessing and managing Debt entities
     */
    @Autowired
    public DebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    /**
     * Retrieves a list of settled debts for a specific event
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A list of settled debts for the specified event.
     */
    public List<Debt> getAllSettledDebtsForEvent(String eventCode) {
        List<Debt> debts = new LinkedList<>();
        debtRepository.findAllSettledDebtsForEvent(eventCode)
                      .iterator()
                      .forEachRemaining(debts::add);
        return debts;
    }
}
