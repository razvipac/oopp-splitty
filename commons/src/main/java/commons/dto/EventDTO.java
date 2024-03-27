package commons.dto;

import java.time.LocalDateTime;

public class EventDTO {

    private String name;
    private String code;
    private LocalDateTime creationDate;
    private LocalDateTime lastActivity;

    public EventDTO() {
    }

    public EventDTO(String name, String code, LocalDateTime creationDate, LocalDateTime lastActivity) {
        this.name = name;
        this.code = code;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

}
