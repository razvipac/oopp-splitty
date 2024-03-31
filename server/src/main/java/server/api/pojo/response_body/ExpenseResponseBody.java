package server.api.pojo.response_body;

import commons.Expense;

import java.time.LocalDate;

public record ExpenseResponseBody(
       Long id,
       String paidBy,
       Integer price,
       String item,
       LocalDate date
) {

    /**
     * Builds an ExpenseResponseBody from an Expense object.
     *
     * @param expense The Expense object to build the response body from.
     * @return An ExpenseResponseBody object built from the provided Expense.
     */
    public static ExpenseResponseBody build(Expense expense){
        return new ExpenseResponseBody(
                expense.getId(),
                expense.getPaidBy().getName(),
                expense.getPrice(),
                expense.getItem(),
                expense.getDate()
        );
    }
}
