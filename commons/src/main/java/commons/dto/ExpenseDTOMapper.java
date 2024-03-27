package commons.dto;

import commons.Expense;
import commons.Participant;

public class ExpenseDTOMapper {

    public static ExpenseDTO toDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getPrice(),
                expense.getItem(),
                expense.getPaidBy().getName()
        );
    }

    public static Expense toEntity(ExpenseDTO dto, Participant participant) {
        return new Expense(
                dto.getPrice(),
                dto.getItem(),
                participant
        );
    }

}
