package server.service;

import commons.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.DebtRepository;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.entities.DTOMapper;
import server.entities.debt.Debt;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.participant.Participant;
import server.service.exceptions.ImproperDumpFormatException;

import java.util.ArrayList;
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
     * @param eventDTOMapper        The EventDTOMapper instance.
     * @param participantDTOMapper  The ParticipantDTOMapper instance.
     * @param expenseDTOMapper      The ExpenseDTOMapper instance.
     * @param debtDTOMapper         The DebtDTOMapper instance.
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
            List<Debt> debts = debtService.getAllDebts(event.getCode());

            for (Participant participant : participants) {
                ParticipantDTO participantDTO = participantDTOMapper.toDTO(participant);
                jsonDumpEventDTO.participantDTOs().add(participantDTO);
            }

            for (Expense expense : expenses) {
                ExpenseDTO expenseDTO = expenseDTOMapper.toDTO(expense);
                jsonDumpEventDTO.expenseDTOs().add(expenseDTO);
            }

            for (Debt debt : debts) {
                DebtDTO debtDTO = debtDTOMapper.toDTO(debt);
                jsonDumpEventDTO.debtDTOs().add(debtDTO);
            }

            response.add(jsonDumpEventDTO);
        }

        return response;
    }
//
//    public JSONDumpEventDTO createDumpForEvent(EventDTO eventDTO){
//        Event eventToDump = eventService.getOne(eventDTO.code());
//    }

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
            for (JSONDumpEventDTO jsonDumpEventDTO : jsonDump) {

                EventDTO eventDTO = jsonDumpEventDTO.eventDTO();
                eventRepository.save(eventDTOMapper.newEntity(eventDTO));

                for (ParticipantDTO participantDTO : jsonDumpEventDTO.participantDTOs()) {
                    participantRepository.save(
                            participantDTOMapper.newEntity(
                                    participantDTO,
                                    eventDTO.code()
                            )
                    );
                }

                for (ExpenseDTO expenseDTO : jsonDumpEventDTO.expenseDTOs()) {
                    expenseRepository.save(
                            expenseDTOMapper.newEntity(
                                   expenseDTO,
                                   eventDTO.code()
                            )
                    );
                }

                for (DebtDTO debtDTO : jsonDumpEventDTO.debtDTOs()) {
                    debtRepository.save(
                            debtDTOMapper.newEntity(
                                    debtDTO,
                                    eventDTO.code()
                            )
                    );
                }
            }
        } catch (Exception e) {
            throw new ImproperDumpFormatException("Improper dump format");
        }
    }

    /**
     * Restores the state of an event to that stored inside the passed DTO
     * @param jsonDumpEventDTO EventDump containing the desired event
     * @throws ImproperDumpFormatException if the passed jsonDump is formatted improperly
     */
    @Transactional
    public void restoreEventFromDump(JSONDumpEventDTO jsonDumpEventDTO)
            throws ImproperDumpFormatException {
        try {

            EventDTO eventDTO = jsonDumpEventDTO.eventDTO();
            if(eventDTO.code() == null || eventDTO.code().isEmpty())
                throw new ImproperDumpFormatException("Event code is invalid");
            eventRepository.save(eventDTOMapper.newEntity(eventDTO));

            for (ParticipantDTO participantDTO : jsonDumpEventDTO.participantDTOs()) {
                participantRepository.save(
                        participantDTOMapper.newEntity(
                                participantDTO,
                                eventDTO.code()
                        )
                );
            }

            for (ExpenseDTO expenseDTO : jsonDumpEventDTO.expenseDTOs()) {
                expenseRepository.save(
                        expenseDTOMapper.newEntity(
                                expenseDTO,
                                eventDTO.code()
                        )
                );
            }

            for (DebtDTO debtDTO : jsonDumpEventDTO.debtDTOs()) {
                debtRepository.save(
                    debtDTOMapper.newEntity(
                            debtDTO,
                            eventDTO.code()
                    )
                );
            }

        } catch (Exception e) {
            throw new ImproperDumpFormatException("Improper dump format");
        }
    }
}