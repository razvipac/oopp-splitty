package server.entities.expense;

import commons.dto.ExpenseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.entities.DTOMapper;
import server.service.ExpenseService;
import server.service.exceptions.NotFoundInDatabaseException;

@Service
public class ExpenseDTOMapper implements DTOMapper<Expense, ExpenseDTO> {

    private final ExpenseService expenseService;

    /**
     * Constructor for participantDTOMapper
     * @param expenseService ExpenseService instance to be injected
     */
    public ExpenseDTOMapper (@Autowired ExpenseService expenseService){
        this.expenseService = expenseService;
    }

    /**
     * Transforms Expense entity to corresponding ExpenseDTO
     * @param expense entity to transform
     * @return corresponding DTO
     */
    @Override
    public ExpenseDTO toDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getPrice(),
                expense.getItem(),
                expense.getPaidBy().getName(),
                expense.getDate()
        );
    }


    /**
     * Transforms ExpenseDTO to corresponding Expense entity
     * @param expenseDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the expense belongs
     * @return corresponding entity or null if not found in the database
     */
    @Override
    public Expense toEntity(ExpenseDTO expenseDTO, Object ...args) {
        try {
            return expenseService.getOne((String) args[0], expenseDTO.paidByName(), expenseDTO.id());
        } catch (NotFoundInDatabaseException e){
            return null;
        }
    }
}
