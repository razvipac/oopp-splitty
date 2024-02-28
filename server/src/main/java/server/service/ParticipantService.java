package server.service;

import commons.Event;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.api.request_bodies.ParticipantBody;
import server.database.ParticipantRepository;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final EventService eventService;

    public ParticipantService(@Autowired ParticipantRepository participantRepository,
                              @Autowired EventService eventService) {
        this.participantRepository = participantRepository;
        this.eventService = eventService;
    }

    public Participant getOne(String eventCode, String name) throws NotFoundInDatabaseException {
        Optional<Participant> searchResult = participantRepository.findParticipantByEventCodeAndName(name, eventCode);
        if (searchResult.isEmpty()) throw new NotFoundInDatabaseException(
                "A Participant of event " + eventCode + " with name " + name + "cannot be found!"
        );

        return searchResult.get();
    }

    public List<Participant> getAll(String eventCode){
        List<Participant> result = new LinkedList<>();
        participantRepository.findAllParticipantsInEvent(eventCode).iterator().forEachRemaining(result::add);
        return result;
    }

    public Participant createOne(String eventCode,
                                 ParticipantBody body) throws NotFoundInDatabaseException {
        Event event = eventService.getOneById(eventCode);

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
}
