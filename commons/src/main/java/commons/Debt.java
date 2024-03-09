// CHECKSTYLE:OFF

package commons;

import jakarta.persistence.*;

import java.util.*;


@Entity
@Table(name = "debt")
public class Debt {

    @EmbeddedId
    private DebtId id;
    private double amount;
    private boolean received;

    /**
     * Instantiate an empty Debt
     */
    public Debt() {
    }

    /**
     * Instantiate a new Debt
     * @param debtor The current debtor of the debt
     * @param creditor The current creditor of the debt
     * @param amount The amount to be paid
     */
    public Debt(Participant debtor, Participant creditor, double amount)
    {
        this.id = new DebtId(debtor, creditor);
        this.amount = amount;
        this.received = false;
    }

    /**
     * Instantiate a new Debt using the id as a parameter
     * @param id The id of the debt
     * @param amount The amount to be paid
     */
    public Debt(DebtId id, double amount) {
        this.id = id;
        this.amount = amount;
        this.received = false;
    }

    /**
     *
     * @return Returns the current id of a Debt object
     */
    public DebtId getId() {
        return id;
    }

    /**
     *
     * @return Returns the debtor of a debt
     */
    public Participant getDebtor()
    {
        return id.getDebtor();
    }

    /**
     *
     * @return Returns the creditor of a debt
     */
    public Participant getCreditor()
    {
        return id.getCreditor();
    }

    /**
     * Setting a new id for a specific debt
     * @param id The id to be changed with
     */
    public void setId(DebtId id) {
        this.id = id;
    }

    /**
     *
     * @return Returns the amount of a specific debt
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount to be paid
     * @param amount The amount to be paid
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     *
     * @return Returns whether the payment has been received
     */
    public boolean isReceived() {
        return received;
    }

    /**
     *
     * @param received Setting the status of whether the debt has been paid
     */
    public void setReceived(boolean received) {
        this.received = received;
    }

    /**
     *
     * @param o Another object to be compared with
     * @return Returns whether these two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return Double.compare(debt.amount, amount) == 0
                && received == debt.received && id.equals(debt.id);
    }

    /**
     *
     * @return Returns the hash code corresponding to a Debt object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, amount, received);
    }

    /**
     *
     * @return Returns a simple string format for the Debt object
     */
    @Override
    public String toString() {
        return "Debt{" +
                "debtor=" + id.getDebtor() +
                ", creditor=" + id.getCreditor() +
                ", amount=" + amount +
                ", received=" + received +
                '}';
    }

    /**
     *
     * @param allParticipants Represents all the participants from the server
     * @param event The event to be taken into consideration
     * @param expenses The list of expenses within an event
     * @return Returns a list of debts to be settled
     */
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

    /**
     *
     * @param allParticipants Represents all the participants from the entire server
     * @param event The event to be taken into consideration
     * @return Returns the list of participants who are present within one specific event
     */
    private List<Participant> basicGetParticipants(List<Participant> allParticipants, Event event) {
        List<Participant> participants = new ArrayList<>();
        for(Participant participant : allParticipants)
            if(participant.getEvent().equals(event))
                participants.add(participant);
        return participants;
    }

    /**
     *
     * @param expense The expense to be taken into consideration
     * @return Returns the list of people who participated in this event
     */
    private List<Participant> advancedGetParticipants(Expense expense) {
        List<Participant> participants = new ArrayList<>();
        participants.add(expense.getPaidBy());
        // Logic to be added for participants
        return participants;
    }
}
