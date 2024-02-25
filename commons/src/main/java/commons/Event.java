package commons;

import java.util.Objects;

public class Event {

    private String name;
    private int code;
    private Date creationDate;
    private Date lastActivity;

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
     * @param lastActivity The last activity of the respective event
     */
    public Event(String name, int code, Date creationDate, Date lastActivity) {
        this.name = name;
        this.code = code;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
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
    public int getCode() {
        return code;
    }

    /**
     * @return the creation date of an instance of type Event
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @return the last activity of an instance of type Event
     */
    public Date getLastActivity() {
        return lastActivity;
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
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Changing the value of the creation date
     *
     * @param creationDate The creation date of an event
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Changing the value of the last activity
     *
     * @param lastActivity The last activity of an event
     */
    public void setLastActivity(Date lastActivity) {
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
        return code == event.code && name.equals(event.name) && creationDate.equals(event.creationDate) && lastActivity.equals(event.lastActivity);
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
}
