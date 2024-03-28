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
        Event event =  new Event(dto.getName(), dto.getCode(), dto.getCreationDate());
        event.setLastActivity(dto.getLastActivity());
        return event;
    }

}
