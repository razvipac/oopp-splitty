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

    // TODO: Add many to one relation with Participant

    public Expense() {
    }

    public Expense(Integer price, String item) {
        this.price = price;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public Integer getPrice() {
        return price;
    }

    public String getItem() {
        return item;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", price=" + price +
                ", item='" + item + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id) && Objects.equals(price, expense.price) && Objects.equals(item, expense.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, item);
    }
}
