package server.entities.expense;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import server.entities.participant.Participant;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an expense entity.
 */
@Entity
@Table(name = "expense")
public class Expense {

    @EmbeddedId
    private ExpenseId pkey;

    private Double price;
    private String item;
    private LocalDate date;

    @ManyToMany
    private Set<Participant> debtors = new LinkedHashSet<>();

//    @ManyToMany
//    @JoinTable(
//            name = "expense_participant",
//            joinColumns = {
//                @JoinColumn(name = "expense_id", referencedColumnName = "id"),
//                @JoinColumn(name = "paidBy_name", referencedColumnName = "paidBy_name"),
//                @JoinColumn(name = "paidBy_event_code",
//                            referencedColumnName = "paidBy_event_code")
//            },
//            inverseJoinColumns = {
//                @JoinColumn(name = "participant_name", referencedColumnName = "name"),
//                @JoinColumn(name = "participant_event_code",
//                            referencedColumnName = "event_code")
//            }
//    )
//    private List<Participant> debtors;



    /**
     * Default constructor.
     */
    public Expense() {
    }

    /**
     * Parameterized constructor to initialize an expense object.
     *
     * @param price  The price of the expense.
     * @param item   The item description of the expense.
     * @param paidBy The participant who paid for the expense.
     * @param date The date of the expense.
     */
    public Expense(Double price, String item, Participant paidBy, LocalDate date) {
        ExpenseId eid = new ExpenseId();
        eid.setPaidBy(paidBy);
        this.pkey = eid;
        this.price = price;
        this.item = item;
        this.date = date;
    }

    /**
     * Retrieves the ID of the expense.
     *
     * @return The ID of the expense.
     */
    public Long getId() {
        return pkey.getId();
    }

    /**
     * Sets the ID of the expense.
     *
     * @param id The ID of the expense to set.
     */
    public void setId(Long id) {
        pkey.setId(id);
    }

    /**
     * Retrieves the price of the expense.
     *
     * @return The price of the expense.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price of the expense.
     *
     * @param price The price of the expense to set.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Retrieves the item description of the expense.
     *
     * @return The item description of the expense.
     */
    public String getItem() {
        return item;
    }

    /**
     * Sets the item description of the expense.
     *
     * @param item The item description of the expense to set.
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * Retrieves the participant who paid for the expense.
     *
     * @return The participant who paid for the expense.
     */
    public Participant getPaidBy() {
        return pkey.getPaidBy();
    }

    /**
     * Sets the participant who paid for the expense.
     *
     * @param paidBy The participant who paid for the expense to set.
     */
    public void setPaidBy(Participant paidBy) {
        pkey.setPaidBy(paidBy);
    }

    /**
     * Retrieves the date of the expense.
     * @return The date of the expense.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of the expense.
     * @param date The date of the expense.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Getter for debtors
     * @return Set of debtors
     */
    public Set<Participant> getDebtors() {
        return debtors;
    }

    /**
     * Setter for debtors
     * @param debtors set of debtors
     */
    public void setDebtors(Set<Participant> debtors) {
        this.debtors = debtors;
    }
    /**
     * Returns a string representation of the expense.
     *
     * @return A string representation of the expense.
     */
    @Override
    public String toString() {
        return "Expense{" +
                "pkey=" + pkey +
                ", price=" + price +
                ", item='" + item + '\'' +
                '}';
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(pkey, expense.pkey)
                && Objects.equals(price, expense.price) && Objects.equals(item, expense.item);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(pkey, price, item);
    }
}
