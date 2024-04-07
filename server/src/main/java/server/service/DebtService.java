package server.service;

import commons.dto.DebtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.DebtRepository;
import server.entities.debt.Debt;
import server.entities.debt.DebtId;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.participant.Participant;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.*;

@Service
public class DebtService {
    private final DebtRepository debtRepository;
    private final ParticipantService participantService;
    private final ExpenseService expenseService;

    /**
     * Constructs a DebtService with the specified DebtRepository
     *
     * @param debtRepository The repository for accessing and managing Debt entities
     * @param participantService Todo
     */
    @Autowired
    public DebtService(
            DebtRepository debtRepository,
            ParticipantService participantService,
            ExpenseService expenseService
    ) {
        this.debtRepository = debtRepository;
        this.participantService = participantService;
        this.expenseService = expenseService;
    }

    /**
     * Retrieves a list of settled debts for a specific event
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A list of settled debts for the specified event.
     */
    public List<Debt> getAllUnsettledDebtsForEvent(String eventCode) {
        List<Debt> debts = new LinkedList<>();
        debtRepository.findAllUnsettledDebtsForEvent(eventCode)
                .iterator().
                forEachRemaining(debts::add);
        return debts;

    }

    /**
     * Gets a Debt object from the given names that form its primary key
     * @param eventCode of the object
     * @param debtorName todo
     * @param creditorName todo
     * @return the Debt object if it is found
     * @throws NotFoundInDatabaseException
     */
    public Debt getOne(String eventCode, String debtorName,
                       String creditorName) throws NotFoundInDatabaseException {

        Optional<Debt> search = debtRepository.findById(getDebtId(
                eventCode,
                debtorName,
                creditorName
        ));
        if (search.isPresent()) {
            return search.get();
        }
        throw new NotFoundInDatabaseException("Debt was not found in the database");
    }

    /**
     * Changes the 'recieved state of the object'
     * @param eventCode of the object
     * @param body of the object
     * @return the object
     * @throws NotFoundInDatabaseException
     */
    public Debt updateOne(String eventCode, DebtDTO body)
            throws NotFoundInDatabaseException {
        Debt found = getOne(eventCode, body.debtorName(), body.creditorName());
        // if not found exception will be thrown
        found.setReceived(!body.received());
        debtRepository.save(found);
        return found;

    }

    private DebtId getDebtId(String eventCode, String debtorName, String creditorName)
            throws NotFoundInDatabaseException {
        Participant debtor = participantService.getOne(eventCode, debtorName);
        Participant creditor = participantService.getOne(eventCode, creditorName);
        return new DebtId(debtor, creditor);
    }

    /**
     * Generates and populates the database with new debt entities generated from the expense instances on that event
     * (currently does not support selective expenses)
     * @param eventCode code of the event to generate debts on
     * @return list of the newly generated debts
     */
    public List<Debt> generateDebtsFromExpenses(String eventCode){
        List<Expense> expenseList = expenseService.getAllInEvent(eventCode);
        List<Participant> participantList = participantService.getAll(eventCode);
        Map<Expense, List<Participant>> inputMap = new HashMap<>();
        expenseList.forEach(e -> inputMap.put(e, new ArrayList<>(participantList)));

        Map<Participant, Map<Participant, Double>> debtsData = Event.settleDebts(inputMap);
        List<Debt> newDebts = new ArrayList<>();

        debtRepository.deleteAll();
        for (Participant debtor : debtsData.keySet()){
            Map<Participant, Double> creditors = debtsData.get(debtor);
            for (Participant creditor : creditors.keySet()){
                Debt newDebt = new Debt(debtor, creditor, creditors.get(creditor));
                debtRepository.save(newDebt);
                newDebts.add(newDebt);
            }
        }

        return newDebts;
    }

}
