package server.service;

import commons.dto.DebtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.DebtRepository;
import server.entities.debt.Debt;
import server.entities.debt.DebtId;
import server.entities.participant.Participant;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class DebtService {
    private final DebtRepository debtRepository;
    private final ParticipantService participantService;

    /**
     * Constructs a DebtService with the specified DebtRepository
     *
     * @param debtRepository The repository for accessing and managing Debt entities
     * @param participantService Todo
     */
    @Autowired
    public DebtService(
            DebtRepository debtRepository,
            ParticipantService participantService
    ) {
        this.debtRepository = debtRepository;
        this.participantService = participantService;
    }

    /**
     * Retrieves a list of settled debts for a specific event
     *
     * @param eventCode The code of the event for which settled debts are to be retrieved
     * @return A list of settled debts for the specified event.
     */
    public List<Debt> getAll(String eventCode) {
        List<Debt> debts = new LinkedList<>();
        debtRepository.findAllDebtsInEvent(eventCode)
                .iterator().
                forEachRemaining(debts::add);
        return debts;
    }
    public List<Debt> getAllUnsettledDebtsForEvent(String eventCode) {
        List<Debt> debts = new LinkedList<>();
        debtRepository.findAllUnsettledDebtsForEvent(eventCode)
                .iterator().
                forEachRemaining(debts::add);
        return debts;

    }

    /**
     * Gets a Debt object from the given names that form its primary key
     * @param eventCode of the object
     * @param debtorName todo
     * @param creditorName todo
     * @return the Debt object if it is found
     * @throws NotFoundInDatabaseException
     */
    public Debt getOne(String eventCode, String debtorName,
                       String creditorName) throws NotFoundInDatabaseException {

        Optional<Debt> search = debtRepository.findById(getDebtId(
                eventCode,
                debtorName,
                creditorName
        ));
        if (search.isPresent()) {
            return search.get();
        }
        throw new NotFoundInDatabaseException("Debt was not found in the database");
    }

    /**
     * Changes the 'recieved state of the object'
     * @param eventCode of the object
     * @param body of the object
     * @return the object
     * @throws NotFoundInDatabaseException
     */
    public Debt updateOne(String eventCode, DebtDTO body)
            throws NotFoundInDatabaseException {
        Debt found = getOne(eventCode, body.debtorName(), body.creditorName());
        // if not found exception will be thrown
        found.setReceived(!body.received());
        debtRepository.save(found);
        return found;

    }
    private DebtId getDebtId(String eventCode, String debtorName, String creditorName)
            throws NotFoundInDatabaseException {
        Participant debtor = participantService.getOne(eventCode, debtorName);
        Participant creditor = participantService.getOne(eventCode, creditorName);
        return new DebtId(debtor, creditor);
    }

}
