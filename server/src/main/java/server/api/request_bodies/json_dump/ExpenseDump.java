package server.api.request_bodies.json_dump;

import commons.Expense;

public record ExpenseDump (
       Long id,
       String paidBy,
       Integer price,
       String item
) {
    public static ExpenseDump build(Expense expense){
        return new ExpenseDump(
                expense.getId(),
                expense.getPaidBy().getName(),
                expense.getPrice(),
                expense.getItem()
        );
    }
}
