package server.service;

import org.mockito.Mockito;
import server.entities.event.Event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;
import server.service.exceptions.NotFoundInDatabaseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;
    @Test
    public void getAllEventsTest(){
        List<Event> list = new ArrayList<>();
        when(eventRepository.findAll()).thenReturn(list);
        List<Event> retrieved = eventService.getAll();
        assertNotNull(retrieved);
        assertEquals(retrieved.size(),0);

    }
    @Test
    public void getOneEventTest() throws Exception {
        String code = "1234";
        Optional<Event> event = Optional.of(new Event("name",code,LocalDateTime.now()));
        when(eventRepository.findById(code)).thenReturn(event);
        Optional<Event> a = Optional.ofNullable(eventService.getOne(code));
        assertEquals(event, a);
    }

    @Test
    public void createEventTest() {
        when(eventRepository.save(Mockito.any(Event.class))).thenAnswer(e -> {
            Event event = e.getArgument(0);
            event.setCode("generatedCode");
            return event;
        });

        Event createdEvent = eventService.createOne("Test Event");

        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getName());
        assertNotNull(createdEvent.getCode());
        assertNotNull(createdEvent.getCreationDate());
        assertNotNull(createdEvent.getLastActivity());

        assertEquals("Test Event", createdEvent.getName());
        assertNotNull(createdEvent.getCode());

    }
    @Test
    void testGenerateCode() {
        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        when(eventRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        EventService eventService = new EventService(eventRepository, null, null);
        String code = eventService.generateCode();

        assertNotNull(code);
        assertEquals(8, code.length());

        Mockito.verify(eventRepository).findById(code);
    }
    @Test
    void testDeleteOneSuccess() throws NotFoundInDatabaseException {
        String eventCode = "testEventCode";
        Event mockEvent = new Event("Test Event", eventCode, null);
        when(eventRepository.findById(eventCode)).thenReturn(Optional.of(mockEvent));

        Event deletedEvent = eventService.deleteOne(eventCode);

        verify(eventRepository, times(1)).deleteById(eventCode);
        assertNotNull(deletedEvent);
        assertEquals(mockEvent, deletedEvent);
    }

    @Test
    void testDeleteOneEventNotFound() {
        String eventCode = "nonExistentEventCode";
        when(eventRepository.findById(eventCode)).thenReturn(Optional.empty());

        assertThrows(NotFoundInDatabaseException.class, () -> eventService.deleteOne(eventCode));
        verify(eventRepository, never()).deleteById(eventCode);
    }

    @Test
    void testUpdateOneSuccess() throws NotFoundInDatabaseException {
        String eventCode = "testEventCode";
        String newName = "New Event Name";
        Event mockEvent = new Event("Test Event", eventCode, null);
        when(eventRepository.findById(eventCode)).thenReturn(Optional.of(mockEvent));

        Event updatedEvent = eventService.updateOne(eventCode, newName);

        assertEquals(newName, updatedEvent.getName());
        assertNotNull(updatedEvent.getLastActivity());
        verify(eventRepository, times(1)).save(updatedEvent);
    }

    @Test
    void testUpdateOneNotFound() {
        String eventCode = "nonExistentEventCode";
        String newName = "New Event Name";
        when(eventRepository.findById(eventCode)).thenReturn(Optional.empty());

        assertThrows(NotFoundInDatabaseException.class, () -> eventService.updateOne(eventCode, newName));
        verify(eventRepository, never()).save(any());
    }


}
