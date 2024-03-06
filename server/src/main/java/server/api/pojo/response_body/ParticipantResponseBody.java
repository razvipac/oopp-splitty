package server.api.pojo.response_body;

import commons.Participant;

public record ParticipantResponseBody(
        String name,
        String email,
        String iban,
        String bic
) {
    public static ParticipantResponseBody build(Participant participant){
        return new ParticipantResponseBody(
                participant.getName(),
                participant.getEmail(),
                participant.getIban(),
                participant.getBic()
        );
    }
}
