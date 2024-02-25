package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

    protected Participant() {
    }

    public Participant(String name, Event event, String email, String iban, String bic) {
        this.name = name;
        this.event = event;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

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
