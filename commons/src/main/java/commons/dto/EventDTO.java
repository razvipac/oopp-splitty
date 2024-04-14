package commons.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object (DTO) representing an event.
 */
public record EventDTO(
        String name,
        String code,
        LocalDateTime creationDate,
        LocalDateTime lastActivity
){

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
            return formattedDate + "\n" + formattedTime;
        }
    }

    /**
     * Setter for last activity.
     * @param lastActivity The new last activity value.
     * @return A new EventDTO object with the updated last activity.
     */
    public EventDTO withLastActivity(LocalDateTime lastActivity) {
        return new EventDTO(name(), code(), creationDate(), lastActivity);
    }
}