package server.entities.participant;

import commons.dto.ParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.entities.DTOMapper;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;

@Service
public class ParticipantDTOMapper implements DTOMapper<Participant, ParticipantDTO> {

    private final ParticipantService participantService;

    /**
     * Constructor for participantDTOMapper
     * @param participantService participantService instance to be injected
     */
    public ParticipantDTOMapper (@Autowired ParticipantService participantService){
        this.participantService = participantService;
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
     * Transforms ParticipantDTO to corresponding Participant entity
     * @param participantDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the participant belongs
     * @return corresponding entity or null if not found in the database
     */
    @Override
    public Participant toEntity(ParticipantDTO participantDTO, Object ...args) {
        try {
            return participantService.getOne((String) args[0], participantDTO.name());
        } catch (NotFoundInDatabaseException e){
            return null;
        }
    }
}
