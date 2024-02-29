package server.service;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.request_bodies.json_dump.EventDump;
import server.api.request_bodies.json_dump.ExpenseDump;
import server.api.request_bodies.json_dump.ParticipantDump;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.service.exceptions.ImproperDumpFormatException;

import java.util.ArrayList;
import java.util.List;

@Service
public class JSONDumpService {
    private final EventService eventService;
    private final ParticipantService participantService;
    private final ExpenseService expenseService;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;


    public JSONDumpService(
            @Autowired EventService eventService,
            @Autowired ParticipantService participantService,
            @Autowired ExpenseService expenseService,
            @Autowired EventRepository eventRepository,
            @Autowired ParticipantRepository participantRepository,
            @Autowired ExpenseRepository expenseRepository) {
        this.eventService = eventService;
        this.participantService = participantService;
        this.expenseService = expenseService;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
    }

    /**
     * Freezes the state of the server into a JSON dump format.
     * This state can be restored from by using the restoreFromDump function.
     * @return state of the server in List<EventDump>
     */
    public List<EventDump> createDump(){

        List<EventDump> response = new ArrayList<>();

        List<Event> events = eventService.getAll();

        for (Event event : events) {
            EventDump eventDump = new EventDump(event, new ArrayList<>(), new ArrayList<>());

            List<Participant> participants = participantService.getAll(event.getCode());
            List<Expense> expenses = expenseService.getAllInEvent(event.getCode());

            for (Participant participant : participants) {
                ParticipantDump participantDump = new ParticipantDump(
                        participant.getName(),
                        participant.getEmail(),
                        participant.getIban(),
                        participant.getBic()
                );
                eventDump.participants().add(participantDump);
            }

            for (Expense expense : expenses) {
                ExpenseDump expenseDump = new ExpenseDump(
                        expense.getId(),
                        expense.getPaidBy().getName(),
                        expense.getPrice(),
                        expense.getItem()
                );
                eventDump.expenses().add(expenseDump);
            }
            response.add(eventDump);
        }
        return response;
    }

    /**
     * Restores the state of the server to that stored inside the passed List<EventDump>
     * @param jsonDump List<EventDump> containing the desired state of the server
     * @throws ImproperDumpFormatException if the passed jsonDump is formatted improperly
     */
    @Transactional
    public void restoreFromDump(List<EventDump> jsonDump) throws ImproperDumpFormatException {
        expenseRepository.deleteAll();
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        try {
            for (EventDump eventDump : jsonDump){
                Event event = eventDump.event();
                eventRepository.save(event);

                for (ParticipantDump participantDump : eventDump.participants()){
                    Participant participant = new Participant(
                            participantDump.name(),
                            event,
                            participantDump.email(),
                            participantDump.iban(),
                            participantDump.bic()
                    );
                    participantRepository.save(participant);
                }

                for (ExpenseDump expenseDump : eventDump.expenses()){
                    Expense expense = new Expense(
                            expenseDump.price(),
                            expenseDump.item(),
                            participantRepository.findParticipantByEventCodeAndName(expenseDump.paidBy(), event.getCode()).get()
                    );
                    expenseRepository.save(expense);
                }
            }
        } catch (Exception e){
            throw new ImproperDumpFormatException("Improper dump format");
        }
    }
}
