package commons.request_body;

import commons.Participant;

/**
 * The structure of the request body for the Participant object
 *
 * @param name name of the participant
 * @param email email of the participant
 * @param iban iban number of the participant
 * @param bic bic number of the participant
 */
public record ParticipantRequestBody(
        String name,
        String email,
        String iban,
        String bic
) {
}
