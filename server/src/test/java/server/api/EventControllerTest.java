// CHECKSTYLE:OFF
package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.dto.EventDTO;
import commons.dto.ParticipantDTO;
import commons.dto.WSAction;
import commons.dto.WSWrapperResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import server.database.EventRepository;
import server.entities.DTOMapper;
import server.entities.event.Event;
import server.entities.event.EventDTOMapper;
import server.entities.participant.Participant;
import server.service.EventService;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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

    @Test
    public void testDeleteDependants() throws NotFoundInDatabaseException {

        Event event = mock(Event.class);
        String eventCode = "1234";


        List<Participant> participants = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String name = "Participant " + i;
            String email = "email" + i;
            String iban = "iban" + i;
            String bic = "bic" + i;
            Participant participant = new Participant(name, event, email, iban, bic);
            participants.add(participant);


            ParticipantDTO participantDTO = new ParticipantDTO(name, email, iban, bic);


            when(participantController.deleteOne(eq(participant.getName()), eq(eventCode))).thenReturn(ResponseEntity.ok(participantDTO));
        }


        when(event.getParticipants()).thenReturn(participants);


        when(event.getCode()).thenReturn(eventCode);


        eventController.deleteDependants(event);


        for (Participant participant : participants) {
            verify(participantController).deleteOne(eq(participant.getName()), eq(eventCode));
        }
    }




    @Test
    public void testDeleteOne() throws NotFoundInDatabaseException {
        String eventCode = "1234";
        Event event = new Event();
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDateTime lastActivity = LocalDateTime.now();

        EventDTO eventDTO = new EventDTO("Event Name", eventCode, creationDate, lastActivity);

        when(eventService.getOne(anyString())).thenReturn(event);
        when(eventDTOMapper.toDTO(event)).thenReturn(eventDTO);
        when(eventService.deleteOne(eventCode)).thenReturn(event);


        ResponseEntity<EventDTO> response = eventController.deleteOne(eventCode);


        verify(eventService).deleteOne(eq(eventCode));


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventDTO, response.getBody());
    }

}
