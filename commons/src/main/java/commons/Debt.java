// CHECKSTYLE:OFF

package commons;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "debt")
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "debtor_name", referencedColumnName = "name"),
            @JoinColumn(name = "debtor_event_code", referencedColumnName = "event_code")
    })
    private Participant debtor;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "creditor_name", referencedColumnName = "name"),
            @JoinColumn(name = "creditor_event_code", referencedColumnName = "event_code")
    })
    private Participant creditor;

    private double amount;          // amount of money owed, in euros

    private boolean received;       // true if money is received, false otherwise

    public Debt(Participant debtor, Participant creditor, double amount) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amount = amount;
        this.received = false;
    }

    public Debt() {

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
        return Double.compare(amount, debt.amount) == 0
                && received == debt.received && Objects.equals(debtor, debt.debtor)
                && Objects.equals(creditor, debt.creditor);
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

    public List<Debt> settleDebts(List<Participant> allParticipants,
                                  Event event, List<Expense> expenses) {
        Map<Participant, Double> debtMap = new HashMap<>();

        for (Expense expense : expenses) {
            Participant paidBy = expense.getPaidBy();
            double totalExpense = expense.getPrice();

            // For this first draft, we are going to
            List<Participant> participants = basicGetParticipants(allParticipants, event);
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

    private List<Participant> basicGetParticipants(List<Participant> allParticipants, Event event) {
        List<Participant> participants = new ArrayList<>();
        for(Participant participant : allParticipants)
            if(participant.getEvent().equals(event))
                participants.add(participant);
        return participants;
    }

    private List<Participant> advancedGetParticipants(Expense expense) {
        List<Participant> participants = new ArrayList<>();
        participants.add(expense.getPaidBy());
        // Logic to be added for participants
        return participants;
    }
}
