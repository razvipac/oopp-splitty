package commons;

public class Debt {

    private Person debtor;      // person who owes money
    private Person creditor;    // person who is owed money
    private double amount;      // amount of money owed, in euros
    private boolean received;   // true if money is received, false otherwise

    public Debt(Person debtor, Person creditor, double amount) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amount = amount;
        this.received = false;
    }

    public Person getDebtor() {
        return debtor;
    }

    public Person getCreditor() {
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

    // To do: equals method
}
