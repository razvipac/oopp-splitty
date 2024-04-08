// CHECKSTYLE:OFF
package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import server.entities.DTOMapper;
import server.entities.event.Event;
import server.entities.participant.Participant;
import commons.dto.EventDTO;
import commons.dto.WSWrapperResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import commons.dto.WSAction;
import server.database.EventRepository;
import server.service.EventService;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;


import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private EventService eventService;

    @Mock
    private ParticipantService participantService;

    @Mock
    private DTOMapper<Event, EventDTO> eventDTOMapper;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private EventController eventController;

    @Mock
    private ParticipantController participantController;
    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(eventController).build();
    }

    @Test
    public void testGet() throws Exception {
        Event event = new Event();
        String eventCode = "1234";
        String newName = "New Name";
        event.setCode(eventCode);
        event.setName(newName);
        List<Event> ev = new LinkedList<>();
        ev.add(event);

        // Mocking successful update
        when(eventService.getAll()).thenReturn(ev);
        List<EventDTO> eventDTOs = ev
                .stream()
                .map(eventDTOMapper::toDTO)
                .toList();


        // Perform Get request and validate response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/")
                        .param("name", newName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createEventTest() throws Exception {
        Event event = new Event();
        String name = "Name";
        event.setName(name);
        String code = "1234";
        event.setCode(code);


        EventDTO eventDTO = new EventDTO(name, code, LocalDateTime.now(), LocalDateTime.now());

        when(eventService.createOne("Name")).thenReturn(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/")
                        .param("name", name))
                .andExpect(status().isCreated());

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<WSWrapperResponseBody<Event>> payloadCaptor = ArgumentCaptor
                .forClass(WSWrapperResponseBody.class);
        verify(simpMessagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());

        String actualDestination = destinationCaptor.getValue();
        WSWrapperResponseBody<Event> actualPayload = payloadCaptor.getValue();

        // Assert that the destination and payload are correct
        assertEquals("/api/websocket/v1/channel/event", actualDestination);
        assertEquals(WSAction.CREATED, actualPayload.action());
        assertEquals(event, actualPayload.object());
    }

    @Test
    public void testUpdateEventSuccess() throws Exception {
        Event event = new Event();
        String eventCode = "1234";
        String newName = "New Name";
        event.setCode(eventCode);
        event.setName(newName);


        // Mocking successful update
        when(eventService.updateOne(eventCode, newName)).thenReturn(event);

        // Perform PUT request and validate response
        mockMvc.perform(put("/api/v1/{eventCode}", eventCode)
                        .param("name", newName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateEventNotFound() throws Exception {
        Event event = new Event();
        String eventCode = "1234";
        String newName = "New Name";
        event.setCode(eventCode);
        event.setName(newName);

        // Mocking NotFoundException
        when(eventService.updateOne(eventCode, newName)).thenThrow(NotFoundInDatabaseException.class);

        // Perform PUT request and validate response
        mockMvc.perform(put("/api/v1/{eventCode}", eventCode)
                        .param("name", newName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteEventSuccess() throws Exception {

        Event event = new Event();
        String eventCode = "1234";
        String newName = "New Name";
        event.setCode(eventCode);
        event.setName(newName);
        String email = "123";
        String iban = "134";
        String bic = "123";
        String name = "A";
        Participant participant = new Participant(name, event, email, iban, bic);
        List<Participant> p = event.getParticipants();

        when(eventService.getOne(eventCode)).thenReturn(event);
        EventDTO eventDTO = eventDTOMapper.toDTO(event);

        when(event.getParticipants()).thenReturn(p);
        // Mocking successful deletion
        when(eventService.deleteOne(eventCode)).thenReturn(event);

        // Perform DELETE request and validate response
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/")
                        .param("eventCode", eventCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<WSWrapperResponseBody<EventDTO>> payloadCaptor = ArgumentCaptor
                .forClass(WSWrapperResponseBody.class);
        verify(simpMessagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());

        String actualDestination = destinationCaptor.getValue();
        WSWrapperResponseBody<EventDTO> actualPayload = payloadCaptor.getValue();

        // Assert that the destination and payload are correct
        // Adjusted the eventCode to match the code variable
        assertEquals("/api/websocket/v1/channel/1234", actualDestination);
        // Corrected to expect DELETED for a successful deletion
        assertEquals(WSAction.DELETED, actualPayload.action());
        assertEquals(eventDTO, actualPayload.object()); // Changed to match the eventDTO
    }

    @Test
    public void testDeleteEventNotFound() throws Exception {
        String eventCode = "1234";

        when(eventService.getOne(eventCode)).thenThrow(NotFoundInDatabaseException.class);
        // Mocking NotFoundException

        // Perform DELETE request and validate response
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/")
                        .param("eventCode", eventCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

//    @Test
//    public void testDeleteDependants() throws NotFoundInDatabaseException {
//        String name = "A";
//        Event event = new Event();
//        String eventCode = "1234";
//        String email = "123";
//        String iban = "134";
//        String bic = "123";
//        Participant participant = new Participant(name, event, email, iban, bic);
//
//
//        // Mock the behavior of participantService.getOne
//        when(participantService.getOne(anyString(), anyString())).thenReturn(participant);
//
//        // Call the method under test
//        eventController.deleteDependants(event);
//
//        // Verify that deleteOne method is called for each participant
//        verify(participantController).deleteOne(eq(name), eq(eventCode));
//
//    }

}
