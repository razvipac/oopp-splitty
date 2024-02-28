package server.api.request_bodies;

import commons.ParticipantId;

/**
 * Structure of the request body for Create/Update operations on expenses
 *
 * @param price price in euros
 * @param item item for which a person paid
 * @param participantId id of the participant who paid
 */
public record ExpenseBody(
        Integer price,
        String item,
        String participantName
) {}
