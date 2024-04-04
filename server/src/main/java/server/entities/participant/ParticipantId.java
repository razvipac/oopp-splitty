package server.entities.participant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import server.entities.event.Event;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ParticipantId implements Serializable {
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_code")
    private Event event;

    /**
     * Default constructor
     */
    public ParticipantId() {
    }

    /**
     * Parameterized constructor
     *
     * @param name  The name of the participant
     * @param event The associated event
     */
    public ParticipantId(String name, Event event) {
        this.name = name;
        this.event = event;
    }

    /**
     * Getter for event
     *
     * @return The associated event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Setter for event
     *
     * @param event The associated event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Getter for name
     *
     * @return The name of the participant
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     *
     * @param name The name of the participant to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * toString method
     *
     * @return String representation of the ParticipantId
     */
    @Override
    public String toString() {
        return "ParticipantId{" +
                "name='" + name + '\'' +
                ", event=" + event +
                '}';
    }

    /**
     * Equals method
     *
     * @param o The object to compare
     * @return True if the objects are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantId that = (ParticipantId) o;
        return Objects.equals(name, that.name) && Objects.equals(event, that.event);
    }

    /**
     * HashCode method
     *
     * @return HashCode of the ParticipantId
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, event);
    }
}
