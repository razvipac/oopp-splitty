package commons.dto;

import java.util.List;

public record JSONDumpEventDTO(
        EventDTO eventDTO,
        List<ParticipantDTO> participantDTOs,
        List<ExpenseDTO> expenseDTOs,
        List<DebtDTO> debtDTOs
) {
}
