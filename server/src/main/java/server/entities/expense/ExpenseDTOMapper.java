package server.entities.expense;

import commons.dto.ExpenseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.entities.DTOMapper;
import server.service.ExpenseService;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;

@Service
public class ExpenseDTOMapper implements DTOMapper<Expense, ExpenseDTO> {

    private final ExpenseService expenseService;
    private final ParticipantService participantService;

    /**
     * Constructor for participantDTOMapper
     * @param expenseService ExpenseService instance to be injected
     * @param participantService ParticipantService instance to be injected
     */
    public ExpenseDTOMapper (
            @Autowired ExpenseService expenseService,
            @Autowired ParticipantService participantService){
        this.expenseService = expenseService;
        this.participantService = participantService;
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
     * Transforms ExpenseDTO to corresponding Expense entity in the database
     * @param expenseDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the expense belongs
     *             and long ID of the expense in the database
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if not present in the database
     */
    @Override
    public Expense getEntity(ExpenseDTO expenseDTO, Object... args) throws NotFoundInDatabaseException {
        return expenseService.getOne((String) args[0], expenseDTO.paidByName(), (Long) args[1]);
    }

    /**
     * Transforms ExpenseDTO to corresponding, new, Expense
     * @param expenseDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the expense belongs
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if not present in the database
     */
    @Override
    public Expense newEntity(ExpenseDTO expenseDTO, Object... args) throws NotFoundInDatabaseException {
        return new Expense(
                expenseDTO.price(),
                expenseDTO.item(),
                participantService.getOne((String) args[0], expenseDTO.paidByName()),
                expenseDTO.date()
        );
    }
}
