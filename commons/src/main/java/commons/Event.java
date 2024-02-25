package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Comparator;
import java.util.stream.Collectors;

@Entity
public class Event {

    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String code;
    private LocalDate creationDate;
    private LocalDate lastActivity = LocalDate.now();

    /**
     * Initializing an empty Event
     */
    public Event() {
    }

    /**
     * Initializing an Event with proper attributes
     *
     * @param name         The name of the respective event
     * @param code         The code of the respective event
     * @param creationDate The creation date of the respective event
     */
    public Event(String name, String code, LocalDate creationDate) {
        this.name = name;
        this.code = code;
        this.creationDate = creationDate;
    }

    /**
     * @return the name of an instance of type Event
     */
    public String getName() {
        return name;
    }

    /**
     * @return the code of an instance of type Event
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the creation date of an instance of type Event
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * @return the last activity of an instance of type Event
     */
    public String getLastActivity() {
        return lastActivity.toString();
    }

    /**
     * Changing the value of the name
     *
     * @param name The name of an event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Changing the value of the code
     *
     * @param code The code of an event
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Changing the value of the creation date
     *
     * @param creationDate The creation date of an event
     */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Changing the value of the last activity
     *
     * @param lastActivity The last activity of an event
     */
    public void setLastActivity(LocalDate lastActivity) {
        this.lastActivity = lastActivity;
    }

    /**
     * A proper equals method for the class Event
     *
     * @param o another object with which we compare
     * @return whether the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Event event = (Event) o;
        return code.equals(event.code) && name.equals(event.name) && creationDate.equals(event.creationDate) && lastActivity.equals(event.lastActivity);
    }

    /**
     * A proper hashCode for the class Event
     *
     * @return the hashCode of an instance of this class
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, code, creationDate, lastActivity);
    }

    /**
     * @return the format in which the events shall appear
     */
    @Override
    public String toString() {
        return "Event " + name + ":" +
                "\t- code = " + code +
                "\t- creationDate = " + creationDate.toString() +
                "\t- lastActivity = " + lastActivity.toString();
    }

    /**
     *
     * @param events Represents our list of present events
     * @return Returns the list of present events ordered alphabetically
     */
    public static List<Event> orderByTitle(List<Event> events)
    {
        return events.stream()
                .sorted(Comparator.comparing(Event::getName))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param events Represents our list of present events
     * @return Returns the list of present events but ordered by means of date of creation
     */
    public static List<Event> orderByCreationDate(List<Event> events) {
        return events.stream()
                .sorted(Comparator.comparing(Event::getCreationDate))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param events Represents our list of present events
     * @return Returns the list of present events but ordered by means of last activity
     */
    public static List<Event> orderByLastActivity(List<Event> events) {
        return events.stream()
                .sorted(Comparator.comparing(Event::getLastActivity))
                .collect(Collectors.toList());
    }

}
