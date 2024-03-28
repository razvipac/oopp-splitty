package commons.dto;

import commons.Participant;

public class ParticipantDTOMapper {

    public static ParticipantDTO toDTO(Participant participant) {
        return new ParticipantDTO(
                participant.getName(),
                EventDTOMapper.toDTO(participant.getEvent()),
                participant.getEmail(),
                participant.getIban(),
                participant.getBic()
        );
    }

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
