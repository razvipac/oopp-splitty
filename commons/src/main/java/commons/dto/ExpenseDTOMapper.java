package commons.dto;

import commons.Expense;

public class ExpenseDTOMapper {

    public static ExpenseDTO toDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getPrice(),
                expense.getItem(),
                ParticipantDTOMapper.toDTO(expense.getPaidBy())
        );
    }

    public static Expense toEntity(ExpenseDTO dto) {
        return new Expense(
                dto.getPrice(),
                dto.getItem(),
                ParticipantDTOMapper.toEntity(dto.getPaidBy())
        );
    }

}
