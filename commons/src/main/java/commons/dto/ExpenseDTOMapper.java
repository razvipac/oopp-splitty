package commons.dto;

import commons.Expense;

/**
 * Mapper class responsible for mapping between Expense entities and ExpenseDTO
 * data transfer objects.
 */
public class ExpenseDTOMapper {

    /**
     * Converts an Expense entity to its corresponding ExpenseDTO data transfer object.
     *
     * @param expense The Expense entity to be converted.
     * @return The resulting ExpenseDTO data transfer object.
     */
    public static ExpenseDTO toDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getPrice(),
                expense.getItem(),
                ParticipantDTOMapper.toDTO(expense.getPaidBy())
        );
    }

    /**
     * Converts an ExpenseDTO data transfer object to its corresponding Expense entity.
     *
     * @param dto The ExpenseDTO data transfer object to be converted.
     * @return The resulting Expense entity.
     */
    public static Expense toEntity(ExpenseDTO dto) {
        return new Expense(
                dto.getPrice(),
                dto.getItem(),
                ParticipantDTOMapper.toEntity(dto.getPaidBy())
        );
    }

}