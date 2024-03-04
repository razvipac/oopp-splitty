package commons;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ParticipantId implements Serializable {
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_code")
    private Event event;

    public ParticipantId() {
    }

    public ParticipantId(String name, Event event) {
        this.name = name;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ParticipantId{" +
                "name='" + name + '\'' +
                ", event=" + event +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantId that = (ParticipantId) o;
        return Objects.equals(name, that.name) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, event);
    }
}
