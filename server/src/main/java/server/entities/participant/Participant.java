package server.entities.participant;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import server.entities.expense.Expense;
import server.entities.debt.Debt;
import server.entities.event.Event;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "participant")
public class Participant {

    @EmbeddedId
    private ParticipantId pkey;
    private String email;
    private String iban;
    private String bic;

    @OneToMany(
            mappedBy = "pkey.paidBy",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Expense> paidForExpenses;

    @OneToMany(mappedBy = "id.debtor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Debt> debtsWhereDebtor = new LinkedHashSet<>();

    @OneToMany(mappedBy = "id.creditor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Debt> debtsWhereCreditor = new LinkedHashSet<>();

    /**
     * Default constructor needed for the JPA
     */
    public Participant() {
    }

    /**
     *
     * @param name is the primary key of this entity as it has to be unique
     *             because this is the only information available for participants
     * @param event contains the code of the event that the participant is in
     * @param email for payment information purposes
     * @param iban for payment information purposes
     * @param bic for payment information purposes
     */
    public Participant(String name, Event event, String email, String iban, String bic) {
        this.pkey = new ParticipantId(name, event);
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    /**
     * @return name of the participant
     */
    public String getName() {
        return pkey.getName();
    }

    /**
     * @param name changes the name of the participant
     */
    public void setName(String name) {
        this.pkey.setName(name);
    }

    /**
     * @return the code of the event
     */
    public Event getEvent() {
        return pkey.getEvent();
    }

    /**
     * @param event changes the event code the participant is in
     */
    public void setEvent(Event event) {
        pkey.setEvent(event);
    }

    /**
     * @return the mail of the participant
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email sets a different email for the participant
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the payment information
     */
    public String getIban() {
        return iban;
    }

    /**
     * @param iban changes payment information
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * @return the payment information
     */
    public String getBic() {
        return bic;
    }
    /**
     * @param bic changes payment information
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * Getter for paidForExpenses
     * @return reference to paidForExpenses
     */
    public List<Expense> getPaidForExpenses(){
        return paidForExpenses;
    }


    /**
     * @return legible information of the participant
     */
    @Override
    public String toString() {
        return "Participant{" +
                "name='" + pkey.getName() + '\'' +
                ", event=" + pkey.getEvent() +
                ", email='" + email + '\'' +
                ", iban='" + iban + '\'' +
                ", bic='" + bic + '\'' +
                '}';
    }

    /**
     * @param o takes an obkect to compare
     * @return true if the objects are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(pkey, that.pkey) && Objects.equals(email, that.email)
                && Objects.equals(iban, that.iban) && Objects.equals(bic, that.bic);
    }

    /**
     * Returns a hash code value for the participant.
     *
     * @return A hash code value based on the fields of the participant.
     */
    @Override
    public int hashCode() {
        return Objects.hash(pkey, email, iban, bic);
    }
}
