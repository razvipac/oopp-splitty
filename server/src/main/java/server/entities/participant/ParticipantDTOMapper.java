package server.entities.participant;

import commons.dto.ParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.entities.DTOMapper;
import server.service.EventService;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;

@Service
public class ParticipantDTOMapper implements DTOMapper<Participant, ParticipantDTO> {

    private final ParticipantService participantService;
    private final EventService eventService;

    /**
     * Constructor for participantDTOMapper
     * @param participantService participantService instance to be injected
     * @param eventService EventService instance to be injected
     */
    public ParticipantDTOMapper (
            @Autowired ParticipantService participantService,
            @Autowired EventService eventService){
        this.participantService = participantService;
        this.eventService = eventService;
    }

    /**
     * Transforms Participant entity to corresponding ParticipantDTO
     * @param participant entity to transform
     * @return corresponding DTO
     */
    @Override
    public ParticipantDTO toDTO(Participant participant) {
        return new ParticipantDTO(
                participant.getName(),
                participant.getEmail(),
                participant.getIban(),
                participant.getBic()
        );
    }

    /**
     * Transforms ParticipantDTO to corresponding Participant entity in the database
     * @param participantDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the participant belongs
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if not present in the database
     */
    @Override
    public Participant getEntity(ParticipantDTO participantDTO, Object... args) throws NotFoundInDatabaseException {
        return participantService.getOne((String) args[0], participantDTO.name());
    }

    /**
     * Transforms ParticipantDTO to corresponding, new, Participant
     * @param participantDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the participant belongs
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if not present in the database
     */
    @Override
    public Participant newEntity(ParticipantDTO participantDTO, Object... args) throws NotFoundInDatabaseException {
        return new Participant(
                participantDTO.name(),
                eventService.getOne((String) args[0]),
                participantDTO.email(),
                participantDTO.iban(),
                participantDTO.bic()
        );

    }
}
