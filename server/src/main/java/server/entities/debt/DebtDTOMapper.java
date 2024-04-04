package server.entities.debt;

import commons.dto.DebtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.entities.DTOMapper;
import server.service.DebtService;
import server.service.exceptions.NotFoundInDatabaseException;

@Service
public class DebtDTOMapper implements DTOMapper<Debt, DebtDTO> {

    private final DebtService debtService;

    /**
     * Constructor for participantDTOMapper
     * @param debtService DebtService instance to be injected
     */
    public DebtDTOMapper (@Autowired DebtService debtService){
        this.debtService = debtService;
    }

    /**
     * Transforms Debt entity to corresponding DebtDTO
     * @param debt entity to transform
     * @return corresponding DTO
     */
    @Override
    public DebtDTO toDTO(Debt debt) {
        return new DebtDTO(
                debt.getDebtor().getName(),
                debt.getCreditor().getName(),
                debt.getAmount(),
                debt.isReceived()
        );
    }

    /**
     * Transforms DebtDTO to corresponding Debt entity
     * @param debtDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the debt belongs
     * @return corresponding entity or null if not found in the database
     */
    @Override
    public Debt toEntity(DebtDTO debtDTO, Object ...args) {
        try {
            return debtService.getOne((String) args[0], debtDTO.debtorName(), debtDTO.creditorName());
        } catch (NotFoundInDatabaseException e){
            return null;
        }
    }
}
