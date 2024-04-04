package commons.dto;

/**
 * Data Transfer Object (DTO) representing a participant.
 */
public record ParticipantDTO (
        String name,
        String email,
        String iban,
        String bic
){ }