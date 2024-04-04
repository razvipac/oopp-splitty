package commons.dto;

/**
 * Data Transfer Object (DTO) representing a debt between participants.
 */
public record DebtDTO (
        String debtorName,
        String creditorName,
        double amount,
        boolean received
) { }