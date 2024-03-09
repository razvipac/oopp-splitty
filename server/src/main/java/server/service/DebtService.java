package server.service;

import commons.Debt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.DebtRepository;

import java.util.List;

@Service
public class DebtService {
    private final DebtRepository debtRepository;

    @Autowired
    public DebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    public List<Debt> getAllSettledDebtsForEvent(String eventCode) {
        return debtRepository.findAllSettledDebtsForEvent(eventCode);
    }
}