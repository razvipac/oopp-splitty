package server.api.pojo.request_body;

/**
 * The structure of the request body for the Debt object
 *
 * @param debtor_name name of the debtor
 * @param creditor_name name of the creditor
 * @param amount owed
 * @param received is it received
 */

public record DebtRequestBody(
    String debtor_name,
    String creditor_name,
    double amount,
    boolean received
){
}
