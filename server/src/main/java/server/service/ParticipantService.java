package server.service;

import commons.dto.ParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.entities.event.Event;
import server.entities.participant.Participant;
import server.entities.participant.ParticipantId;
import server.service.exceptions.NotFoundInDatabaseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Handles input and output for saved Participant objects
 */
@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final ExpenseService expenseService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor for ParticipantService
     *
     * @param participantRepository The ParticipantRepository instance to interact with the database
     * @param eventRepository The EventRepository instance to interact with the database
     * @param expenseService The ExpenseService instance to interact with the expenses
     * @param simpMessagingTemplate The SimpMessagingService instance to send STOMP messages
     */
    public ParticipantService(@Autowired ParticipantRepository participantRepository,
                              @Autowired EventRepository eventRepository,
                              @Autowired ExpenseService expenseService,
                              @Autowired SimpMessagingTemplate simpMessagingTemplate
    ) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.expenseService = expenseService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Fetches a specific Participant object
     *
     * @param eventCode eventCode of the event to which the Participant belongs
     * @param name      name of the participant
     * @return the fetched Participant object
     * @throws NotFoundInDatabaseException if such a Participant is not present in the database
     */
    public Participant getOne(String eventCode, String name) throws NotFoundInDatabaseException {
        Optional<Participant> searchResult = participantRepository
                .findParticipantByEventCodeAndName(name, eventCode);
        if (searchResult.isEmpty()) throw new NotFoundInDatabaseException(
                "A Participant of event " + eventCode + " with name " + name + "cannot be found!"
        );

        return searchResult.get();
    }

    /**
     * Fetches all participants in given event
     *
     * @param eventCode a code of the event from which to fetch participants
     * @return a LinkedList of Participant objects
     */
    public List<Participant> getAll(String eventCode) {
        List<Participant> result = new LinkedList<>();
        participantRepository.findAllParticipantsInEvent(eventCode)
                .iterator()
                .forEachRemaining(result::add);
        return result;
    }

    /**
     * Creates and saves a new Participant entity
     *
     * @param eventCode code of the event for which the participant should be created
     * @param body      data to be used when creating the participant object
     * @return the newly created Participant entity
     * @throws NotFoundInDatabaseException if an event with the given eventCode
     *                                     is not present in the database
     */
    public Participant createOne(String eventCode, ParticipantDTO body)
            throws NotFoundInDatabaseException {
        Event event = getOneEvent(eventCode);

        Participant newParticipant = new Participant(
                body.name(),
                event,
                body.email(),
                body.iban(),
                body.bic()
        );

        participantRepository.save(newParticipant);
        updateDate(eventCode);
        return newParticipant;
    }

    /**
     * Deletes a participant from the database.
     *
     * @param eventCode       The code of the event.
     * @param participantName The name of the participant.
     * @return The deleted participant.
     * @throws NotFoundInDatabaseException If the participant is not found in the database.
     */
    public Participant deleteOne(String eventCode, String participantName)
            throws NotFoundInDatabaseException {
        Participant found = getOne(eventCode, participantName);
        // if not found, exception will be thrown

        participantRepository.deleteById(getParticipantId(
                eventCode,
                participantName
        ));


        updateDate(eventCode);
        return found;
    }

    /**
     * Updates a participant in the database.
     *
     * @param eventCode The code of the event.
     * @param name      The name of the participant.
     * @param body      The request body containing updated participant information.
     * @return The updated participant.
     * @throws NotFoundInDatabaseException If the participant is not found in the database.
     */
    public Participant updateOne(String eventCode, String name, ParticipantDTO body)
            throws NotFoundInDatabaseException {

        Participant found = getOne(eventCode, name);
        // if not found exception will be thrown

        // Check if the corresponding fields in ParticipantDTO are not empty before updating
        if (!body.email().isEmpty()) found.setEmail(body.email());
        if (!body.iban().isEmpty()) found.setIban(body.iban());
        if (!body.bic().isEmpty()) found.setBic(body.bic());

        participantRepository.save(found);
        updateDate(eventCode);
        return found;
    }

    /**
     * Retrieves the participant ID based on the event code and participant name.
     *
     * @param eventCode       The code of the event.
     * @param participantName The name of the participant.
     * @return The participant ID.
     * @throws NotFoundInDatabaseException If the event corresponding to the event code
     *                                     is not found in the database.
     */
    private ParticipantId getParticipantId(String eventCode, String participantName)
            throws NotFoundInDatabaseException {
        Event event = getOneEvent(eventCode);
        return new ParticipantId(participantName, event);
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
     * @return the instance of the event on which the lastActivity date was updated
     * @throws NotFoundInDatabaseException if an object with given code
     *                                     is not present in the database
     */
    public Event updateDate(String eventCode) throws NotFoundInDatabaseException {
        Event found = getOneEvent(eventCode);
        // if not found exception will be thrown
        LocalDateTime l = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        found.setLastActivity(l);
        eventRepository.save(found);
        return found;
    }
}
