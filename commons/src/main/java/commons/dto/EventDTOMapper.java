package commons.dto;

import commons.Event;

public class EventDTOMapper {

    public static EventDTO toDTO(Event event) {
        return new EventDTO(
                event.getName(),
                event.getCode(),
                event.getCreationDate(),
                event.getLastActivity()
        );
    }

    public static Event toEntity(EventDTO dto) {
        return new Event(
                dto.getName(),
                dto.getCode(),
                dto.getCreationDate()
        );
    }

}
