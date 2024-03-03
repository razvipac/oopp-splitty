package commons;

import java.util.*;

public class Debt {

    private Participant debtor;      // person who owes money
    private Participant creditor;    // person who is owed money
    private double amount;      // amount of money owed, in euros
    private boolean received;   // true if money is received, false otherwise

    public Debt(Participant debtor, Participant creditor, double amount) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amount = amount;
        this.received = false;
    }

    public Participant getDebtor() {
        return debtor;
    }

    public Participant getCreditor() {
        return creditor;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return Double.compare(amount, debt.amount) == 0 && received == debt.received && Objects.equals(debtor, debt.debtor) && Objects.equals(creditor, debt.creditor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(debtor, creditor, amount, received);
    }

    @Override
    public String toString() {
        return "Debt{" +
                "debtor=" + debtor +
                ", creditor=" + creditor +
                ", amount=" + amount +
                ", received=" + received +
                '}';
    }

    /**
     *
     * @param allParticipants represents the list of all the participants
     * @param event represents a specific event from which we want to settle the debts
     * @param expenses represents the list of all the expenses among that particular event
     * @return returns the list of debts for every single expense
     */
    public List<Debt> SettleDebts(List<Participant> allParticipants, Event event, List<Expense> expenses) {
        Map<Participant, Double> debtMap = new HashMap<>();

        for (Expense expense : expenses) {
            Participant paidBy = expense.getPaidBy();
            double totalExpense = expense.getPrice();

            // For this first draft, we are going to only use the basic algorithm
            List<Participant> participants = BasicGetParticipants(allParticipants, event);
            double individualShare = totalExpense / participants.size();

            for (Participant participant : participants) {
                if (!participant.equals(paidBy)) {
                    double currentDebt = debtMap.getOrDefault(participant, 0.0);
                    debtMap.put(participant, currentDebt + individualShare);
                }
            }
        }

        List<Debt> debts = new ArrayList<>();
        for (Map.Entry<Participant, Double> entry : debtMap.entrySet()) {
            Participant debtor = entry.getKey();
            double amount = entry.getValue();
            debts.add(new Debt(debtor, null, amount)); // Leave the creditor null for now
        }

        return debts;
    }

    /**
     * This function refers to the basic requirement
     * @param allParticipants holds the list of all the participants
     * @param event represent a specific event
     * @return returns only the participants that are present in that event
     */
    private List<Participant> BasicGetParticipants(List<Participant> allParticipants, Event event) {
        List<Participant> participants = new ArrayList<>();
        for(Participant participant : allParticipants)
            if(participant.getEvent().equals(event))
                participants.add(participant);
        return participants;
    }

    /**
     * This function does not take into consideration that an expense splits between all the participants
     * @param allParticipants
     * @param expense
     * @return
     */
    private List<Participant> AdvancedGetParticipants(List<Participant> allParticipants, Expense expense) {
        List<Participant> participants = new ArrayList<>();
        participants.add(expense.getPaidBy());
        // Logic to be added for participants
        return participants;
    }
}
