// CHECKSTYLE:OFF

package server.entities.debt;

import jakarta.persistence.*;
import server.entities.participant.Participant;

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
     * Instantiate a new Debt using the id as a parameter
     * @param debtor The current debtor of the debt
     * @param creditor The current creditor of the debt
     * @param amount The amount to be paid
     * @param received Whether the debt has been received (true) or not (false)
     */
    public Debt(Participant debtor, Participant creditor, double amount, boolean received) {
        this.id = new DebtId(debtor, creditor);
        this.amount = amount;
        this.received = received;
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

}
