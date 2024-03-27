package commons.dto;

import commons.Event;
import commons.Participant;

public class ParticipantDTOMapper {

    public static ParticipantDTO toDTO(Participant participant) {
        return new ParticipantDTO(
                participant.getName(),
                participant.getEmail(),
                participant.getIban(),
                participant.getBic()
        );
    }

    public static Participant toEntity(ParticipantDTO dto, Event event) {
        return new Participant(
                dto.getName(),
                event,
                dto.getEmail(),
                dto.getIban(),
                dto.getBic()
        );
    }

}
