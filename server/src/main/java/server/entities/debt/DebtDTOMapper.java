package server.entities.debt;

import commons.dto.DebtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.entities.DTOMapper;
import server.service.DebtService;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;

@Service
public class DebtDTOMapper implements DTOMapper<Debt, DebtDTO> {

    private final DebtService debtService;
    private final ParticipantService participantService;

    /**
     * Constructor for participantDTOMapper
     * @param debtService DebtService instance to be injected
     * @param participantService participantService instance to be injected
     */
    public DebtDTOMapper (
            @Autowired DebtService debtService,
            @Autowired ParticipantService participantService
    ){
        this.debtService = debtService;
        this.participantService = participantService;
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
     * Transforms DebtDTO to corresponding Debt entity in the database
     * @param debtDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the debt belongs
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if not present in the database
     */
    @Override
    public Debt getEntity(DebtDTO debtDTO, Object... args) throws NotFoundInDatabaseException {
        return debtService.getOne((String) args[0], debtDTO.debtorName(), debtDTO.creditorName());
    }

    /**
     * Transforms DebtDTO to corresponding, new, Debt
     * @param debtDTO DTO to transform
     * @param args additional arguments, here eventCode of the event to which the debt belongs
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if not present in the database
     */
    @Override
    public Debt newEntity(DebtDTO debtDTO, Object... args) throws NotFoundInDatabaseException {
        return new Debt(
                participantService.getOne((String) args[0], debtDTO.debtorName()),
                participantService.getOne((String) args[0], debtDTO.creditorName()),
                debtDTO.amount(),
                debtDTO.received()
        );
    }
}
