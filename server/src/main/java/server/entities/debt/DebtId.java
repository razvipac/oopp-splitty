package server.entities.debt;

import jakarta.persistence.*;
import server.entities.participant.Participant;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DebtId implements Serializable {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "debtor_name", referencedColumnName = "name"),
        @JoinColumn(name = "debtor_event_code", referencedColumnName = "event_code")
    })
    private Participant debtor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "creditor_name", referencedColumnName = "name"),
        @JoinColumn(name = "creditor_event_code", referencedColumnName = "event_code")
    })
    private Participant creditor;

    /**
     * Instantiate an empty primary key
     */
    public DebtId() {
    }

    /**
     * Instantiate a new primary key for a Debt object
     *
     * @param debtor   The debtor of the debt
     * @param creditor The creditor of the debt
     */
    public DebtId(Participant debtor, Participant creditor) {
        this.debtor = debtor;
        this.creditor = creditor;
    }

    /**
     * @return Returns the current debitor
     */
    public Participant getDebtor() {
        return debtor;
    }

    /**
     * Sets the current debitor to be another
     *
     * @param debtor Represents another participant
     */
    public void setDebtor(Participant debtor) {
        this.debtor = debtor;
    }

    /**
     * @return Returns the current creditor
     */
    public Participant getCreditor() {
        return creditor;
    }

    /**
     * Sets the current creditor to be another
     *
     * @param creditor Represents another participant
     */
    public void setCreditor(Participant creditor) {
        this.creditor = creditor;
    }

    /**
     * @return Returns a simple string format for the primary key of Debt
     */
    @Override
    public String toString() {
        return "DebtId{" +
                "debtor=" + debtor +
                ", creditor=" + creditor +
                '}';
    }

    /**
     * @param o Another object to be compared with
     * @return Returns whether two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebtId debtId = (DebtId) o;
        return Objects.equals(debtor, debtId.debtor) && Objects.equals(creditor, debtId.creditor);
    }

    /**
     * @return Returns a hash code for a specific primary key
     */
    @Override
    public int hashCode() {
        return Objects.hash(debtor, creditor);
    }
}