package commons.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object (DTO) representing an event.
 */
public class EventDTO {

    private String name;
    private String code;
    private LocalDateTime creationDate;
    private LocalDateTime lastActivity;

    /**
     * Default constructor for EventDTO.
     */
    public EventDTO() {
    }

    /**
     * Constructs an EventDTO with the specified name, code, creation date, and last activity date.
     * @param name The name of the event.
     * @param code The code of the event.
     * @param creationDate The date and time when the event was created.
     * @param lastActivity The date and time of the last activity associated with the event.
     */
    public EventDTO(String name, String code, LocalDateTime creationDate,
                    LocalDateTime lastActivity) {
        this.name = name;
        this.code = code;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
    }

    /**
     * Retrieves the name of the event.
     * @return The name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event.
     * @param name The name of the event.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the code of the event.
     * @return The code of the event.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code of the event.
     * @param code The code of the event.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Retrieves the creation date and time of the event.
     * @return The creation date and time of the event.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date and time of the event.
     * @param creationDate The creation date and time of the event.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the date and time of the last activity associated with the event.
     * @return The date and time of the last activity associated with the event.
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    /**
     * Custom toString() method to format last activity for display.
     * @return Formatted string representing last activity.
     */
    public String lastActivityToString() {
        if (lastActivity == null) {
            return "Last activity: No activity recorded";
        } else {
            // Format date
            String formattedDate = lastActivity.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            // Format time
            String formattedTime = lastActivity.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            return "Last activity: " + formattedDate + "\n" + formattedTime;
        }
    }

    /**
     * Updates the last activity timestamp to the current time.
     */
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    /**
     * Sets the date and time of the last activity associated with the event.
     * @param lastActivity The date and time of the last activity associated with the event.
     */
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}