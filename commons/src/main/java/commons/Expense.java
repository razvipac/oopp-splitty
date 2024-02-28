package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Entity")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_id_sequence")
    private Long id;

    private Integer price;
    private String item;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_name")
    private Participant paidBy;

    public Expense() {
    }

    public Expense(Long id, Integer price, String item, Participant paidBy) {
        this.id = id;
        this.price = price;
        this.item = item;
        this.paidBy = paidBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Participant getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(Participant paidBy) {
        this.paidBy = paidBy;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", price=" + price +
                ", item='" + item + '\'' +
                ", paidBy=" + paidBy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id) && Objects.equals(price, expense.price) && Objects.equals(item, expense.item) && Objects.equals(paidBy, expense.paidBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, item, paidBy);
    }
}