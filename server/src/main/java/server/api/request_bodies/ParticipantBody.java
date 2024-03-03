package server.api.request_bodies;

/**
 * The structure of the request body for the Participant object
 *
 * @param name name of the participant
 * @param email email of the participant
 * @param iban iban number of the participant
 * @param bic bic number of the participant
 */
public record ParticipantBody(
        String name,
        String email,
        String iban,
        String bic
) {
}
