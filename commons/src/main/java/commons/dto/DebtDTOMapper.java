package commons.dto;

import commons.Debt;

/**
 * Mapper class responsible for mapping between Debt entities and DebtDTO data transfer objects.
 */
public class DebtDTOMapper {

    /**
     * Converts a Debt entity to its corresponding DebtDTO data transfer object.
     * @param debt The Debt entity to be converted.
     * @return The resulting DebtDTO data transfer object.
     */
    public static DebtDTO toDTO(Debt debt) {
        return new DebtDTO(
                ParticipantDTOMapper.toDTO(debt.getDebtor()),
                ParticipantDTOMapper.toDTO(debt.getCreditor()),
                debt.getAmount(),
                debt.isReceived()
        );
    }

    /**
     * Converts a DebtDTO data transfer object to its corresponding Debt entity.
     * @param dto The DebtDTO data transfer object to be converted.
     * @return The resulting Debt entity.
     */
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