package commons.dto;

import commons.Debt;
import commons.DebtId;
import commons.Event;

public class DebtDTOMapper {

    public static DebtDTO toDTO(Debt debt) {
        return new DebtDTO(
                ParticipantDTOMapper.toDTO(debt.getDebtor()),
                ParticipantDTOMapper.toDTO(debt.getCreditor()),
                debt.getAmount(),
                debt.isReceived()
        );
    }

    public static Debt toEntity(DebtDTO dto) {
        Debt debt = new Debt(
                ParticipantDTOMapper.toEntity(dto.getDebtor()),
                ParticipantDTOMapper.toEntity(dto.getCreditor()),
                dto.getAmount()
        );
        debt.setReceived(dto.isReceived());
        return debt;
    }

}
