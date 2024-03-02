package commons;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExpenseId implements Serializable {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_id_sequence")
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "paidBy_name", referencedColumnName = "name"),
            @JoinColumn(name = "paidBy_event_code", referencedColumnName = "event_code")
    })
    private Participant paidBy;

    public ExpenseId() {
    }

    public ExpenseId(Long id, Participant paidBy) {
        this.id = id;
        this.paidBy = paidBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Participant getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(Participant paidBy) {
        this.paidBy = paidBy;
    }

    @Override
    public String toString() {
        return "ExpenseId{" +
                "id=" + id +
                ", paidBy=" + paidBy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseId expenseId = (ExpenseId) o;
        return Objects.equals(id, expenseId.id) && Objects.equals(paidBy, expenseId.paidBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paidBy);
    }
}


