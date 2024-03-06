package server.api.pojo.response_body;

import commons.Expense;

public record ExpenseResponseBody(
       Long id,
       String paidBy,
       Integer price,
       String item
) {
    public static ExpenseResponseBody build(Expense expense){
        return new ExpenseResponseBody(
                expense.getId(),
                expense.getPaidBy().getName(),
                expense.getPrice(),
                expense.getItem()
        );
    }
}
