package server.entities.expense;

import jakarta.persistence.*;
import server.entities.participant.Participant;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the composite key for an Expense entity.
 */
@Embeddable
public class ExpenseId implements Serializable {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_id_sequence")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "paidBy_name", referencedColumnName = "name"),
        @JoinColumn(name = "paidBy_event_code", referencedColumnName = "event_code")
    })
    private Participant paidBy;

    /**
     * Constructs a new ExpenseId instance.
     */
    public ExpenseId() {
    }

    /**
     * Constructs a new ExpenseId instance with the specified id and paidBy participant.
     *
     * @param id     The ID of the expense.
     * @param paidBy The participant who paid for the expense.
     */
    public ExpenseId(Long id, Participant paidBy) {
        this.id = id;
        this.paidBy = paidBy;
    }

    /**
     * Retrieves the ID of the expense.
     *
     * @return The ID of the expense.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the expense.
     *
     * @param id The ID of the expense.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the participant who paid for the expense.
     *
     * @return The participant who paid for the expense.
     */
    public Participant getPaidBy() {
        return paidBy;
    }

    /**
     * Sets the participant who paid for the expense.
     *
     * @param paidBy The participant who paid for the expense.
     */
    public void setPaidBy(Participant paidBy) {
        this.paidBy = paidBy;
    }

    /**
     * Returns a string representation of the ExpenseId object.
     *
     * @return A string representation of the ExpenseId object.
     */
    @Override
    public String toString() {
        return "ExpenseId{" +
                "id=" + id +
                ", paidBy=" + paidBy +
                '}';
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The object to compare with.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseId expenseId = (ExpenseId) o;
        return Objects.equals(id, expenseId.id) && Objects.equals(paidBy, expenseId.paidBy);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, paidBy);
    }
}
