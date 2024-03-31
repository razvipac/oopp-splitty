package commons.response_body;

import commons.Participant;

public record ParticipantResponseBody(
        String name,
        String email,
        String iban,
        String bic
) {

    /**
     * Builds a ParticipantResponseBody from a Participant object.
     *
     * @param participant The participant object.
     * @return The participant response body.
     */
    public static ParticipantResponseBody build(Participant participant){
        return new ParticipantResponseBody(
                participant.getName(),
                participant.getEmail(),
                participant.getIban(),
                participant.getBic()
        );
    }
}
