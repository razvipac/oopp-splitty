package commons.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing an expense.
 */
public record ExpenseDTO (
        long id,
        int price,
        String item,
        String paidByName,
        LocalDate date
){ }
