package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.entities.event.Event;
import server.service.exceptions.NotFoundInDatabaseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Handles input and output for saved Event objects
 */
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ParticipantService participantService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructs an EventService instance with the specified EventRepository.
     *
     * @param eventRepository The EventRepository to be injected into the service.
     * @param participantService The ParticipantService to be injected into the service.
     * @param simpMessagingTemplate The simpMessagingTemplate to be injected into the service.
     */
    public EventService(@Autowired EventRepository eventRepository,
                        @Autowired ParticipantService participantService,
                        @Autowired SimpMessagingTemplate simpMessagingTemplate) {
        this.eventRepository = eventRepository;
        this.participantService = participantService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Fetches an Event object with given code
     *
     * @param code code of the fetched Event object
     * @return a fetched Event object
     * @throws NotFoundInDatabaseException if an object with given code
     *                                     is not present in the database
     */
    public Event getOne(String code) throws NotFoundInDatabaseException {
        Optional<Event> searchResult = eventRepository.findById(code);

        if (searchResult.isEmpty()) throw new NotFoundInDatabaseException(
                "Event with code: " + code + " is not present in the database!");


        return searchResult.get();
    }


    /**
     * Fetches all Events in the database
     *
     * @return a linked list with all stored Event instances
     */
    public List<Event> getAll() {
        List<Event> events = new LinkedList<>();
        eventRepository.findAll().iterator().forEachRemaining(events::add);
        return events;
    }

    /**
     * Creates and saves an Event with given name
     *
     * @param name name of the event to be created
     * @return the newly created Event object
     */
    public Event createOne(String name) {
        String code = generateCode();
        LocalDateTime creationDate = LocalDateTime.now();

        Event newEvent = new Event(name, code, creationDate);
        newEvent.setLastActivity(creationDate);

        eventRepository.save(newEvent);
        return newEvent;
    }

    /**
     * Deletes the event with the specified event code.
     *
     * @param eventCode The event code of the event to be deleted.
     * @return The deleted event.
     * @throws NotFoundInDatabaseException If the event with the specified event code is not found.
     */
    public Event deleteOne(String eventCode) throws NotFoundInDatabaseException {
        Event found = getOne(eventCode);
        // if not found exception will be thrown

        eventRepository.deleteById(eventCode);

        return found;
    }

    /**
     * Updates the name of the event with the specified event code.
     *
     * @param eventCode The event code of the event to be updated.
     * @param newName The new name for the event.
     * @return The updated event.
     * @throws NotFoundInDatabaseException If the event with the specified event code is not found.
     */
    public Event updateOne(String eventCode, String newName) throws NotFoundInDatabaseException {
        Event found = getOne(eventCode);
        // if not found exception will be thrown

        found.setName(newName);
        LocalDateTime l = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        found.setLastActivity(l);
        eventRepository.save(found);
        return found;

    }

    /**
     * Generates a UNIQUE 8 character code.
     * Checks if it is already present in the Event database table, if it is the code gets
     * regenerated until it is unique.
     *
     * @return a unique 8 character code to be used as a Event code
     */
    public String generateCode() {
        // Creates a list of characters [[0-9], [A-Z], [a-z]]
        List<Character> possibleCharacters = new ArrayList<>();
        for (int i = 48; i <= 57; i++) {
            possibleCharacters.add((char) i);
        }
        for (int i = 65; i <= 90; i++) {
            possibleCharacters.add((char) i);
        }
        for (int i = 97; i <= 122; i++) {
            possibleCharacters.add((char) i);
        }

        String code = "";

        while (code.isEmpty() || eventRepository.findById(code).isPresent()) {
            code = "";
            for (int i = 0; i < 8; i++) {
                Character c = possibleCharacters.get(
                        (int) ((Math.random() * Integer.MAX_VALUE) % possibleCharacters.size())
                );
                code += c;
            }
        }
        // code is now unique
        return code;
    }
}
