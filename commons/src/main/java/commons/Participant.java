package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import java.util.Objects;
@Entity
@Table(name = "Participant")
public class Participant {
    @Id
    private String name;
    @ManyToOne
    @JoinColumn(name = "code", nullable = false)
    private Event event;
    private String email;
    private String iban;
    private String bic;

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
        this.name = name;
        this.event = event;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    /*
    Constructor made for testing
    To Do: delete after getting event entity information
     */
    public Participant(String name, String email, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    /**
     * @return name of the participant
     */
    public String getName() {
        return name;
    }

    /**
     * @param name changes the name of the participant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code of the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event changes the event code the participant is in
     */
    public void setEvent(Event event) {
        this.event = event;
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
                "name='" + name + '\'' +
                ", event=" + event +
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
        if (!(o instanceof Participant that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(event, that.event) && Objects.equals(email, that.email) && Objects.equals(iban, that.iban) && Objects.equals(bic, that.bic);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, event, email, iban, bic);
    }
}
