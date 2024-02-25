package commons;

import java.util.Objects;

public class Event {

    private String name;
    private int code;
    private Date creationDate;
    private Date lastActivity;


    public Event(String name, int code, Date creationDate, Date lastActivity) {
        this.name = name;
        this.code = code;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Event event = (Event) o;
        return code == event.code && name.equals(event.name) && creationDate.equals(event.creationDate) && lastActivity.equals(event.lastActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code, creationDate, lastActivity);
    }

    @Override
    public String toString() {
        return "Event" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", creationDate=" + creationDate +
                ", lastActivity=" + lastActivity +
                '}';
    }
}
