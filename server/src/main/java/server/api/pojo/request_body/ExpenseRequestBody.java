package server.api.pojo.request_body;

/**
 * Structure of the request body for Create/Update operations on expenses
 *
 * @param price price in euros
 * @param item item for which a person paid
 * @param participantName name of the participant who paid
 */
public record ExpenseRequestBody(
        Integer price,
        String item,
        String participantName
) {}
