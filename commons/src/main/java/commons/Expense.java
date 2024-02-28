package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "expense")
public class Expense {

    @EmbeddedId
    private ExpenseId pkey;

    private Integer price;
    private String item;

    public Expense() {
    }

    public Expense(Long id, Integer price, String item, Participant paidBy) {
        this.pkey = new ExpenseId(id, paidBy);
        this.price = price;
        this.item = item;
    }

    public Long getId() {
        return pkey.getId();
    }

    public void setId(Long id) {
        pkey.setId(id);
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
        return pkey.getPaidBy();
    }

    public void setPaidBy(Participant paidBy) {
        pkey.setPaidBy(paidBy);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "pkey=" + pkey +
                ", price=" + price +
                ", item='" + item + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(pkey, expense.pkey) && Objects.equals(price, expense.price) && Objects.equals(item, expense.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkey, price, item);
    }
}