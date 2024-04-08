package server.service;

import commons.dto.DebtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.DebtRepository;
import server.entities.debt.Debt;
import server.entities.debt.DebtId;
import server.entities.expense.Expense;
import server.entities.participant.Participant;
import server.service.exceptions.NotFoundInDatabaseException;

import java.text.DecimalFormat;
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
     * @param participantService ParticipantService instance
     * @param expenseService ExpenseService instance
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
     * Retrieves a list of all debts for a specific event
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A list of all debts for the specified event.
     */
    public List<Debt> getAll(String eventCode) {
        List<Debt> debts = new LinkedList<>();
        debtRepository.findAllDebtsInEvent(eventCode)
                .iterator().
                forEachRemaining(debts::add);
        return debts;
    }
    /**
     * Retrieves a list of unsettled debts for a specific event
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A list of unsettled debts for the specified event.
     */
    public List<Debt> getAllDebts(String eventCode) {
        List<Debt> debts = new LinkedList<>();
        debtRepository.findAllDebts(eventCode)
                .iterator()
                .forEachRemaining(debts::add);
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

        return calculateDebts(expenseList, participantList);
    }

//    private List<Debt> calculateDebts(List<Participant> participants, List<Expense> expenses){
//        Map<Participant, Double> balances = new HashMap<>();
//        participants.forEach(p -> balances.put(p, 0.0));
//
//        for (Expense expense : expenses){
//            Participant creditor = expense.getPaidBy();
//            Double amount = expense.getPrice();
//            balances.put(creditor, balances.get(creditor) - amount);
//
//            List<Participant> debtors = new ArrayList<>(expense.getDebtors());
//            List<Double> splits = splitWithoutLoss(amount, debtors.size());
//            Iterator<Double> splitsIterator = splits.iterator();
//
//            for (Participant debtor : debtors){
//                balances.put(debtor, balances.get(debtor) + splitsIterator.next());
//            }
//        }
//
//
//
//    }

    private List<Debt> calculateDebts(List<Expense> expenses, List<Participant> participants){
        Map<Expense, List<Participant>> inputMap = new HashMap<>();
        expenses.forEach(e -> inputMap.put(e, new ArrayList<>(participants)));

        Map<Participant, Map<Participant, Double>> debts = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.##");

        // Now we are analyzing each expense and for each, its participants in that specific expense
        for (Map.Entry<Expense, List<Participant>> entry : inputMap.entrySet()) {
            Expense expense = entry.getKey();
            Participant paidBy = expense.getPaidBy();
            // People who are participating in that expense
            List<Participant> participantList = entry.getValue();

            // Calculate the share per participant (it is split equally among every participant)
            double share = (double) expense.getPrice() / participantList.size();
            share = Double.parseDouble(df.format(share));

            for (Participant debtor : participantList) {
                if (!debtor.equals(paidBy)) {
                    // Update the debt between participants

                    double amount = debts.getOrDefault(debtor, new HashMap<>()).getOrDefault(paidBy, 0.0);
                    amount += share;
                    Map<Participant, Double> debtorDebts = debts.computeIfAbsent(debtor, k -> new HashMap<>());
                    debtorDebts.put(paidBy, amount);
                }
            }
        }

        List<Debt> newDebts = new ArrayList<>();

        debtRepository.deleteAll();
        for (Participant debtor : debts.keySet()){
            Map<Participant, Double> creditors = debts.get(debtor);
            for (Participant creditor : creditors.keySet()){
                Debt newDebt = new Debt(debtor, creditor, creditors.get(creditor));
                debtRepository.save(newDebt);
                newDebts.add(newDebt);
            }
        }

        return newDebts;
    }

//    public static List<Double> splitWithoutLoss(Double numerator, Integer denominator){
//        List<Double> splits = new ArrayList<>();
//        while (denominator > 0){
//            Double split = (double) Math.round((numerator / denominator) * 100) / 100;
//            numerator -= split;
//            denominator --;
//            splits.add(split);
//        }
//        return splits;
//    }
}
