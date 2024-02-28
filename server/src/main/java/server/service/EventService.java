package server.service;

import commons.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.EventRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class EventService {
    private EventRepository eventRepository;

    public EventService(@Autowired EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAll(){
        List<Event> events = new LinkedList<>();
        eventRepository.findAll().iterator().forEachRemaining(events::add);
        return events;
    }

    public Event createOne(String name){
        String code = generateCode();
        LocalDateTime creationDate = LocalDateTime.now();

        Event newEvent = new Event(name, code, creationDate);
        newEvent.setLastActivity(creationDate);

        eventRepository.save(newEvent);
        return newEvent;
    }

    private String generateCode(){
        // Creates a list of characters 0-9 A-Z a-z
        List<Character> possibleCharacters = new ArrayList<>();
        for (int i = 48; i <= 57; i++){
            possibleCharacters.add((char) i);
        }
        for (int i = 65; i <= 90; i++){
            possibleCharacters.add((char) i);
        }
        for (int i = 97; i <= 122; i++){
            possibleCharacters.add((char) i);
        }

        String code = "";

        while (code.isEmpty() || eventRepository.findById(code).isPresent()){
            code = "";
            for (int i = 0; i < 8; i++){
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
