package server.api.request_bodies.json_dump;

import commons.Participant;

public record ParticipantDump (
        String name,
        String email,
        String iban,
        String bic
) {
    public static ParticipantDump build(Participant participant){
        return new ParticipantDump(
                participant.getName(),
                participant.getEmail(),
                participant.getIban(),
                participant.getBic()
        );
    }
}
