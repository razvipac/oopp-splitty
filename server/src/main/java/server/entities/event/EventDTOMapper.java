package server.entities.event;

import commons.dto.EventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.entities.DTOMapper;
import server.service.EventService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.time.LocalDateTime;

@Service
public class EventDTOMapper implements DTOMapper<Event, EventDTO> {

    private final EventService eventService;

    /**
     * Constructor for EventDTOMapper
     * @param eventService EventService instance to be injected
     */
    public EventDTOMapper(@Autowired EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Transforms Event entity to corresponding EventDTO
     * @param event entity to transform
     * @return corresponding DTO
     */
    @Override
    public EventDTO toDTO(Event event) {
        return new EventDTO(
                event.getName(),
                event.getCode(),
                event.getCreationDate(),
                event.getLastActivity()
        );
    }

    /**
     * Transforms EventDTO to corresponding Event entity in the database
     * @param eventDTO DTO to transform
     * @param args additional arguments, here not used
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if the corresponding event is not present in the database
     */
    @Override
    public Event getEntity(EventDTO eventDTO, Object... args) throws NotFoundInDatabaseException {
        return eventService.getOne(eventDTO.code());
    }

    /**
     * Transforms eventDTO to corresponding, new, event
     * @param eventDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the event belongs
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if not present in the database
     */
    @Override
    public Event newEntity(EventDTO eventDTO, Object... args) throws NotFoundInDatabaseException {
        return new Event(eventDTO.name(), eventDTO.code(), LocalDateTime.now());
    }
}
