package server.service;

import commons.dto.ExpenseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.expense.ExpenseId;
import server.entities.participant.Participant;
import server.service.exceptions.NotFoundInDatabaseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Handles input and output of saved Expense objects
 */
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EventRepository eventRepository;


    /**
     * Constructs an ExpenseService instance with
     * the specified ExpenseRepository and ParticipantRepository.
     *
     * @param expenseRepository     The ExpenseRepository to be injected into the service.
     * @param participantRepository The ParticipantRepository to be injected into the service.
     * @param simpMessagingTemplate The SimpMessagingTemplate to be injected into the service.
     * @param eventRepository       The EventRepository to be injected into the service.
     */
    public ExpenseService(
            @Autowired ExpenseRepository expenseRepository,
            @Autowired ParticipantRepository participantRepository,
            @Autowired SimpMessagingTemplate simpMessagingTemplate,
            @Autowired EventRepository eventRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.participantRepository = participantRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.eventRepository = eventRepository;
    }

    /**
     * Fetches all Expenses in a given Event
     *
     * @param eventCode code of the Event to which the Expenses belong
     * @return a LinkedList containing all Expense objects in a given Event
     */
    public List<Expense> getAllInEvent(String eventCode) {
        List<Expense> result = new LinkedList<>();
        expenseRepository.findAllExpensesInEvent(eventCode)
                .iterator()
                .forEachRemaining(result::add);
        return result;
    }

    /**
     * Fetches all Expenses in a given Event and paid by given participant
     *
     * @param eventCode code of the Event to which the Expenses belong
     * @param paidBy Participant who paid the seeked Expenses
     * @return a LinkedList containing all Expense objects in a given Event
     */
    public List<Expense> getAllInEventAndPaidByParticipant(String eventCode, Participant paidBy) {
        List<Expense> result = new LinkedList<>();
        expenseRepository.findAllExpensesInEventDependantOnParticipant(eventCode, paidBy.getName())
                .iterator()
                .forEachRemaining(result::add);
        return result;
    }

    /**
     * Fetches one Expense object
     *
     * @param eventCode       code of the Event to which the Expense belongs
     * @param participantName name of the owner of the Expense
     * @param id              id of the Expense
     * @return                a given Expense object
     * @throws NotFoundInDatabaseException if an owner of the Expense
     *                                     or the specified Event does not exist
     */
    public Expense getOne(String eventCode, String participantName,
                          Long id) throws NotFoundInDatabaseException {

        Optional<Expense> search = expenseRepository.findById(getExpenseId(
                eventCode,
                participantName,
                id
        ));

        if (search.isPresent()) {
            return search.get();
        }
        throw new NotFoundInDatabaseException("Expense was not found in the database");
    }

    /**
     * Creates and saves a new Expense object
     *
     * @param eventCode code of the Event to which the Expense belongs
     * @param body      data to populate the new Expense object with
     * @return the newly created Expense object
     * @throws NotFoundInDatabaseException if an owner of the Expense
     *                                     or the specified Event does not exist
     */
    public Expense createOne(String eventCode,
                             ExpenseDTO body) throws NotFoundInDatabaseException {

        Participant paidBy = getOneParticipant(eventCode, body.paidByName());

        Expense newExpense = new Expense(body.price(), body.item(), paidBy, body.date());

        expenseRepository.save(newExpense);
        updateDate(eventCode);
        return newExpense;
    }

    /**
     * Deletes a specified Expense from the database
     *
     * @param eventCode       code of the Event to which the Expense belongs
     * @param participantName name of the owner of the Expense
     * @param id              id of the Expense
     * @return the deleted Expense object
     * @throws NotFoundInDatabaseException if an owner of the Expense
     *                                     or the specified Event does not exist
     */
    public Expense deleteOne(String eventCode, String participantName, Long id)
            throws NotFoundInDatabaseException {
        Expense found = getOne(eventCode, participantName, id);
        // if not found exception will be thrown

        expenseRepository.deleteById(getExpenseId(
                eventCode,
                participantName,
                id
        ));

        updateDate(eventCode);
        return found;
    }

    /**
     * Updates the data of a given Expense
     *
     * @param eventCode code of the Event to which the Expense belongs
     * @param id        id of the Expense
     * @param body      data to be set as the new value of the given expense.
     *                  **NAME CANNOT BE CHANGED!**
     * @return the updated expense object
     * @throws NotFoundInDatabaseException if an owner of the Expense
     *                                     or the specified Event does not exist
     */
    public Expense updateOne(String eventCode, Long id, ExpenseDTO body)
            throws NotFoundInDatabaseException {
        Expense found = getOne(eventCode, body.paidByName(), id);
        // if not found exception will be thrown

        if(!(body.item().isEmpty())) found.setItem(body.item());
        if(body.price() != -1) found.setPrice(body.price());
        if(body.date() != null) found.setDate(body.date());

        expenseRepository.save(found);

        updateDate(eventCode);
        return found;
    }

    /**
     * Creates a ExpenseId object given arguments
     *
     * @param eventCode       eventCode of the Event to which the Expense belongs
     * @param participantName name of the owner of the expense
     * @param id              id of the Expense
     * @return ExpenseId object created from the given data
     * @throws NotFoundInDatabaseException if a Participant of Event with code eventCode
     *                                     and name participantName does not exist in the database
     */
    private ExpenseId getExpenseId(String eventCode, String participantName, Long id)
            throws NotFoundInDatabaseException {
        Participant paidBy = getOneParticipant(eventCode, participantName);
        return new ExpenseId(id, paidBy);
    }

    /**
     * Fetches a specific Participant object
     *
     * @param eventCode eventCode of the event to which the Participant belongs
     * @param name      name of the participant
     * @return the fetched Participant object
     * @throws NotFoundInDatabaseException if such a Participant is not present in the database
     */
    public Participant getOneParticipant(String eventCode, String name)
            throws NotFoundInDatabaseException {
        Optional<Participant> searchResult = participantRepository
                .findParticipantByEventCodeAndName(name, eventCode);

        if (searchResult.isEmpty()) throw new NotFoundInDatabaseException(
                "A Participant of event " + eventCode + " with name " + name + "cannot be found!"
        );

        return searchResult.get();
    }
    /**
     * Fetches an Event object with given code
     *
     * @param eventCode code of the fetched Event object
     * @return a fetched Event object
     * @throws NotFoundInDatabaseException if an object with given code
     *                                     is not present in the database
     */
    public Event getOneEvent(String eventCode) throws NotFoundInDatabaseException {
        Optional<Event> searchResult = eventRepository.findById(eventCode);

        if (searchResult.isEmpty()) throw new NotFoundInDatabaseException(
                "Event with code: " + eventCode + " is not present in the database!");

        return searchResult.get();
    }

    /**
     * Updates the Last Activity date on the Event
     *
     * @param eventCode code of the fetched Event object
     * @throws NotFoundInDatabaseException if an object with given code
     *                                     is not present in the database
     */
    public void updateDate(String eventCode) throws NotFoundInDatabaseException {
        Event found = getOneEvent(eventCode);
        // if not found exception will be thrown
        LocalDateTime l = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        found.setLastActivity(l);
        eventRepository.save(found);
    }
}
