package commons;

import java.util.*;

public class Debt {

    private Participant debtor;      // person who owes money
    private Participant creditor;    // person who is owed money
    private double amount;      // amount of money owed, in euros
    private boolean received;   // true if money is received, false otherwise

    public Debt(Participant debtor, Participant creditor, double amount) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amount = amount;
        this.received = false;
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
        return Double.compare(amount, debt.amount) == 0 && received == debt.received && Objects.equals(debtor, debt.debtor) && Objects.equals(creditor, debt.creditor);
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
}
