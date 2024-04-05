package commons.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing an expense.
 */
public record ExpenseDTO (
        Long id,
        int price,
        String item,
        String paidByName,
        LocalDate date
){ }
