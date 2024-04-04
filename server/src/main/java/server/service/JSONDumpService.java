package server.service;

import commons.dto.*;
import server.entities.DTOMapper;
import server.entities.debt.Debt;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.participant.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.pojo.response_body.DebtResponseBody;
import server.api.pojo.response_body.JSONDumpEventDTO;
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
    private final DTOMapper<Event, EventDTO> eventDTOMapper;
    private final DTOMapper<Participant, ParticipantDTO> participantDTOMapper;
    private final DTOMapper<Expense, ExpenseDTO> expenseDTOMapper;
    private final DTOMapper<Debt, DebtDTO> debtDTOMapper;

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
            @Autowired DebtRepository debtRepository,
            @Autowired DTOMapper<Event, EventDTO> eventDTOMapper,
            @Autowired DTOMapper<Participant, ParticipantDTO> participantDTOMapper,
            @Autowired DTOMapper<Expense, ExpenseDTO> expenseDTOMapper,
            @Autowired DTOMapper<Debt, DebtDTO> debtDTOMapper
    ) {
        this.eventService = eventService;
        this.participantService = participantService;
        this.expenseService = expenseService;
        this.debtService = debtService;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
        this.debtRepository = debtRepository;
        this.eventDTOMapper = eventDTOMapper;
        this.participantDTOMapper = participantDTOMapper;
        this.expenseDTOMapper = expenseDTOMapper;
        this.debtDTOMapper = debtDTOMapper;
    }

    /**
     * Freezes the state of the server into a JSON dump format.
     * This state can be restored from by using the restoreFromDump function.
     *
     * @return state of the server in List<EventDump>
     */
    public List<JSONDumpEventDTO> createDump() {

        List<JSONDumpEventDTO> response = new ArrayList<>();

        List<Event> events = eventService.getAll();

        for (Event event : events) {
            JSONDumpEventDTO jsonDumpEventDTO =
                    new JSONDumpEventDTO(eventDTOMapper.toDTO(event), new ArrayList<>(),
                            new ArrayList<>(), new ArrayList<>());

            List<Participant> participants = participantService.getAll(event.getCode());
            List<Expense> expenses = expenseService.getAllInEvent(event.getCode());
            List<Debt> debts = debtService.getAllUnsettledDebtsForEvent(event.getCode());

            for (Participant participant : participants) {
                ParticipantDTO participantDTO = participantDTOMapper.toDTO(participant);
                jsonDumpEventDTO.participantDTOs().add(participantDTO);
            }

            for (Expense expense : expenses) {
                ExpenseDTO expenseDTO = expenseDTOMapper.toDTO(expense);
                jsonDumpEventDTO.expenseDTOs().add(expenseDTO);
            }

            for (Debt debt : debts) {
                DebtDTO debtDTO = debtDTOMapper.toDTO(debt)
                jsonDumpEventDTO.debtDTOs().add(debtDTO);
            }

            response.add(jsonDumpEventDTO);
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
    public void restoreFromDump(List<JSONDumpEventDTO> jsonDump)
            throws ImproperDumpFormatException {
        debtRepository.deleteAll();
        expenseRepository.deleteAll();
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        try {
            for (JSONDumpEventDTO JSONDumpEventDTO : jsonDump) {
                EventDTO eventDTO = JSONDumpEventDTO.eventDTO();
                eventRepository.save(eventDTOMapper.toEntity(eventDTO));

                for (DebtDTO debtDTO : JSONDumpEventDTO.debtDTOs()) {
                    debtRepository.save(debtDTOMapper.toEntity(debtDTO, eventDTO.code()));
                }

                for (ParticipantDTO participantDTO :
                        JSONDumpEventDTO.participantDTOs()) {
                    Participant participant = new Participant(
                            participantDTO.name(),
                            event,
                            participantDTO.email(),
                            participantDTO.iban(),
                            participantDTO.bic()
                    );
                    participantRepository.save(participant);
                }

                for (ExpenseResponseBody expenseResponseBody : JSONDumpEventDTO.expenses()) {
                    Expense expense = new Expense(
                            expenseResponseBody.price(),
                            expenseResponseBody.item(),
                            participantRepository.findParticipantByEventCodeAndName
                                    (expenseResponseBody.paidBy(),
                                            event.getCode()).get(), expenseResponseBody.date());
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
     * @param JSONDumpEventDTO contains the information of 1 event
     * @throws ImproperDumpFormatException if the passed jsonDump is formatted improperly
     */
    @Transactional
    public void restoreFromDump(JSONDumpEventDTO JSONDumpEventDTO)
            throws ImproperDumpFormatException {
        try {
            Event event = JSONDumpEventDTO.event();
            LocalDateTime l = new Date().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            event.setLastActivity(l);
            eventRepository.save(event);

            for (DebtResponseBody debtResponseBody : JSONDumpEventDTO.debts()) {
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
                    JSONDumpEventDTO.participants()) {
                Participant participant = new Participant(
                        participantResponseBody.name(),
                        event,
                        participantResponseBody.email(),
                        participantResponseBody.iban(),
                        participantResponseBody.bic()
                );
                participantRepository.save(participant);
            }

            for (ExpenseResponseBody expenseResponseBody : JSONDumpEventDTO.expenses()) {
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
