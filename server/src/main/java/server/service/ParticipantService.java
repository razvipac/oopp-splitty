package server.service;

import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.api.pojo.request_body.ParticipantRequestBody;
import server.database.ParticipantRepository;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Handles input and output for saved Participant objects
 */
@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final EventService eventService;

    /**
     * Constructor for ParticipantService
     *
     * @param participantRepository The ParticipantRepository instance to interact with the database
     * @param eventService          The EventService instance to handle event-related operations
     */
    public ParticipantService(@Autowired ParticipantRepository participantRepository,
                              @Autowired EventService eventService) {
        this.participantRepository = participantRepository;
        this.eventService = eventService;
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
    public Participant createOne(String eventCode, ParticipantRequestBody body)
            throws NotFoundInDatabaseException {
        Event event = eventService.getOne(eventCode);

        Participant newParticipant = new Participant(
                body.name(),
                event,
                body.email(),
                body.iban(),
                body.bic()
        );

        participantRepository.save(newParticipant);
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
        // if not found exception will be thrown

        participantRepository.deleteById(getParticipantId(
                eventCode,
                participantName
        ));
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
    public Participant updateOne(String eventCode, String name, ParticipantRequestBody body)
            throws NotFoundInDatabaseException {
        Participant found = getOne(eventCode, body.name());
        // if not found exception will be thrown
        found.setEmail(body.email());
        found.setIban(body.iban());
        found.setBic(body.bic());

        participantRepository.save(found);
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
        Event event = eventService.getOne(eventCode);
        return new ParticipantId(participantName, event);
    }
}
