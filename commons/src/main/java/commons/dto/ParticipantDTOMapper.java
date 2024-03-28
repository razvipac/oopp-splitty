package commons.dto;

import commons.Participant;

/**
 * Mapper class responsible for mapping between Participant entities and ParticipantDTO
 * data transfer objects.
 */
public class ParticipantDTOMapper {

    /**
     * Converts a Participant entity to its corresponding ParticipantDTO data transfer object.
     *
     * @param participant The Participant entity to be converted.
     * @return The resulting ParticipantDTO data transfer object.
     */
    public static ParticipantDTO toDTO(Participant participant) {
        return new ParticipantDTO(
                participant.getName(),
                EventDTOMapper.toDTO(participant.getEvent()),
                participant.getEmail(),
                participant.getIban(),
                participant.getBic()
        );
    }

    /**
     * Converts a ParticipantDTO data transfer object to its corresponding Participant entity.
     *
     * @param dto The ParticipantDTO data transfer object to be converted.
     * @return The resulting Participant entity.
     */
    public static Participant toEntity(ParticipantDTO dto) {
        return new Participant(
                dto.getName(),
                EventDTOMapper.toEntity(dto.getEvent()),
                dto.getEmail(),
                dto.getIban(),
                dto.getBic()
        );
    }

}