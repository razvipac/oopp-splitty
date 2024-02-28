package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.util.Objects;
@Entity
@Table(name = "participant")
public class Participant {
    @EmbeddedId
    private ParticipantId pkey;
    private String email;
    private String iban;
    private String bic;

//    Basic endpoints and fully functional relationships between the 3 main entities
//
//    Added fully functional relationships between classes Event, Participant and Expense.
//    Added very basic endpoints for testing.
//    JSON dumping is on the way now that this works.

    /**
     * Default constructor needed for the JPA
     */
    protected Participant() {
    }

    /**
     *
     * @param name is the primary key of this entity as it has to be unique because this is the only information available for participants
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
        return Objects.equals(pkey, that.pkey) && Objects.equals(email, that.email) && Objects.equals(iban, that.iban) && Objects.equals(bic, that.bic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkey, email, iban, bic);
    }
}
