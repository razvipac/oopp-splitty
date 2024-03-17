package server.api.pojo.response_body;

import commons.Debt;
public record DebtResponseBody(
        String debtor,
        String creditor,
        double amount,
        boolean received
) {
    /**
     * Builds an DebtResponseBody from a Debt object.
     *
     * @param debt The Expense object to build the response body from.
     * @return An ExpenseResponseBody object built from the provided Expense.
     */
    public static DebtResponseBody build(Debt debt){
        return new DebtResponseBody(
                debt.getDebtor().getName(),
                debt.getCreditor().getName(),
                debt.getAmount(),
                debt.isReceived()
        );
    }
}
