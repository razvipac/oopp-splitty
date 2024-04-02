package server.service;

import commons.Debt;
import commons.Event;
import commons.Expense;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.pojo.response_body.DebtResponseBody;
import server.api.pojo.response_body.EventResponseBody;
import server.api.pojo.response_body.ExpenseResponseBody;
import server.api.pojo.response_body.ParticipantResponseBody;
import server.database.DebtRepository;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.service.exceptions.ImproperDumpFormatException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JSONDumpService {
    private final EventService eventService;
    private final ParticipantService participantService;
    private final ExpenseService expenseService;
    private final DebtService debtService;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;
    private final DebtRepository debtRepository;

    /**
     * Constructs a JSONDumpService with the specified dependencies.
     *
     * @param eventService          The EventService instance.
     * @param participantService    The ParticipantService instance.
     * @param expenseService        The ExpenseService instance.
     * @param debtService           The DebtService instance.
     * @param eventRepository       The EventRepository instance.
     * @param participantRepository The ParticipantRepository instance.
     * @param expenseRepository     The ExpenseRepository instance.
     * @param debtRepository        The DebtRepository instance.
     */
    public JSONDumpService(
            @Autowired EventService eventService,
            @Autowired ParticipantService participantService,
            @Autowired ExpenseService expenseService,
            @Autowired DebtService debtService,
            @Autowired EventRepository eventRepository,
            @Autowired ParticipantRepository participantRepository,
            @Autowired ExpenseRepository expenseRepository,
            @Autowired DebtRepository debtRepository) {
        this.eventService = eventService;
        this.participantService = participantService;
        this.expenseService = expenseService;
        this.debtService = debtService;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
        this.debtRepository = debtRepository;
    }

    /**
     * Freezes the state of the server into a JSON dump format.
     * This state can be restored from by using the restoreFromDump function.
     *
     * @return state of the server in List<EventDump>
     */
    public List<EventResponseBody> createDump() {

        List<EventResponseBody> response = new ArrayList<>();

        List<Event> events = eventService.getAll();

        for (Event event : events) {
            EventResponseBody eventResponseBody =
                    new EventResponseBody(event, new ArrayList<>(),
                            new ArrayList<>(), new ArrayList<>());

            List<Participant> participants = participantService.getAll(event.getCode());
            List<Expense> expenses = expenseService.getAllInEvent(event.getCode());
            List<Debt> debts = debtService.getAllUnsettledDebtsForEvent(event.getCode());

            for (Participant participant : participants) {
                ParticipantResponseBody participantResponseBody = new ParticipantResponseBody(
                        participant.getName(),
                        participant.getEmail(),
                        participant.getIban(),
                        participant.getBic()
                );
                eventResponseBody.participants().add(participantResponseBody);
            }

            for (Expense expense : expenses) {
                ExpenseResponseBody expenseResponseBody = new ExpenseResponseBody(
                        expense.getId(),
                        expense.getPaidBy().getName(),
                        expense.getPrice(),
                        expense.getItem(),
                        expense.getDate()
                );
                eventResponseBody.expenses().add(expenseResponseBody);
            }

            for (Debt debt : debts) {
                DebtResponseBody debtResponseBody = new DebtResponseBody(
                        debt.getDebtor().getName(),
                        debt.getCreditor().getName(),
                        debt.getAmount(),
                        debt.isReceived()
                );
                eventResponseBody.debts().add(debtResponseBody);
            }
            response.add(eventResponseBody);
        }

        return response;
    }

    /**
     * Restores the state of the server to that stored inside the passed List<EventDump>
     *
     * @param jsonDump List<EventDump> containing the desired state of the server
     * @throws ImproperDumpFormatException if the passed jsonDump is formatted improperly
     */
    @Transactional
    public void restoreFromDump(List<EventResponseBody> jsonDump)
            throws ImproperDumpFormatException {
        debtRepository.deleteAll();
        expenseRepository.deleteAll();
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        try {
            for (EventResponseBody eventResponseBody : jsonDump) {
                Event event = eventResponseBody.event();
                eventRepository.save(event);

                for (DebtResponseBody debtResponseBody : eventResponseBody.debts()) {
                    Debt debt = new Debt(
                            participantRepository.findParticipantByEventCodeAndName
                                    (debtResponseBody.debtor(), event.getCode()).get(),
                            participantRepository.findParticipantByEventCodeAndName(
                                    debtResponseBody.creditor(), event.getCode()).get(),
                            debtResponseBody.amount()
                    );
                    debtRepository.save(debt);
                }

                for (ParticipantResponseBody participantResponseBody :
                        eventResponseBody.participants()) {
                    Participant participant = new Participant(
                            participantResponseBody.name(),
                            event,
                            participantResponseBody.email(),
                            participantResponseBody.iban(),
                            participantResponseBody.bic()
                    );
                    participantRepository.save(participant);
                }

                for (ExpenseResponseBody expenseResponseBody : eventResponseBody.expenses()) {
                    Expense expense = new Expense(
                            expenseResponseBody.price(),
                            expenseResponseBody.item(),
                            participantRepository.
                                    findParticipantByEventCodeAndName
                                            (expenseResponseBody.paidBy(), event.getCode()).get(), expenseResponseBody.date()
                    );
                    expenseRepository.save(expense);
                }
            }
        } catch (Exception e) {
            throw new ImproperDumpFormatException("Improper dump format");
        }
    }
    /**
     * Restores the state of an event to that stored inside the passed EventDump
     *
     * @param eventResponseBody contains the information of 1 event
     * @throws ImproperDumpFormatException if the passed jsonDump is formatted improperly
     */
    @Transactional
    public void restoreFromDump(EventResponseBody eventResponseBody)
            throws ImproperDumpFormatException {
        try {
            Event event = eventResponseBody.event();
            LocalDateTime l = new Date().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            event.setLastActivity(l);
            eventRepository.save(event);

            for (DebtResponseBody debtResponseBody : eventResponseBody.debts()) {
                Debt debt = new Debt(
                        participantRepository.findParticipantByEventCodeAndName
                                (debtResponseBody.debtor(), event.getCode()).get(),
                        participantRepository.findParticipantByEventCodeAndName(
                                debtResponseBody.creditor(), event.getCode()).get(),
                        debtResponseBody.amount()
                );
                debtRepository.save(debt);
            }

            for (ParticipantResponseBody participantResponseBody :
                    eventResponseBody.participants()) {
                Participant participant = new Participant(
                    participantResponseBody.name(),
                    event,
                    participantResponseBody.email(),
                    participantResponseBody.iban(),
                    participantResponseBody.bic()
                );
                participantRepository.save(participant);
            }

            for (ExpenseResponseBody expenseResponseBody : eventResponseBody.expenses()) {
                Expense expense = new Expense(
                    expenseResponseBody.price(),
                    expenseResponseBody.item(),
                    participantRepository.
                            findParticipantByEventCodeAndName
                                    (expenseResponseBody.paidBy(), event.getCode()).get(),
                    expenseResponseBody.date()
                );
                expenseRepository.save(expense);
            }
        } catch (Exception e) {
            throw new ImproperDumpFormatException("Improper dump format");
        }
    }
}
