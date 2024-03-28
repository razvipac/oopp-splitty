package commons.dto;

import commons.Event;

/**
 * Mapper class responsible for mapping between Event entities and EventDTO data transfer objects.
 */
public class EventDTOMapper {

    /**
     * Converts an Event entity to its corresponding EventDTO data transfer object.
     *
     * @param event The Event entity to be converted.
     * @return The resulting EventDTO data transfer object.
     */
    public static EventDTO toDTO(Event event) {
        return new EventDTO(
                event.getName(),
                event.getCode(),
                event.getCreationDate(),
                event.getLastActivity()
        );
    }

    /**
     * Converts an EventDTO data transfer object to its corresponding Event entity.
     *
     * @param dto The EventDTO data transfer object to be converted.
     * @return The resulting Event entity.
     */
    public static Event toEntity(EventDTO dto) {
        Event event = new Event(dto.getName(), dto.getCode(), dto.getCreationDate());
        event.setLastActivity(dto.getLastActivity());
        return event;
    }
}