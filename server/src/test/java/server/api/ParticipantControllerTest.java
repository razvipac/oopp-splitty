package server.api;

import commons.dto.ParticipantDTO;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import server.entities.DTOMapper;
import server.entities.event.Event;
import server.entities.participant.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import server.service.ParticipantService;
import server.service.exceptions.NotFoundInDatabaseException;


import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
public class ParticipantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParticipantService participantService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private DTOMapper<Participant, ParticipantDTO> participantDTOMapper;

    @Mock
    private ExpenseController expenseController;

    @Mock
    private DebtController debtController;

    @InjectMocks
    private ParticipantController participantController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(participantController).build();
    }

    @Test
    public void testGetAllParticipantsSuccess() throws Exception {
        String eventCode = "1234";
        Event event = new Event("Event Name", eventCode, LocalDateTime.now());
        Participant participant = new Participant("Participant Name", event,
                "participant@example.com", "1234", "5678");
        List<Participant> participants = Collections.singletonList(participant);
        when(participantService.getAll(eventCode)).thenReturn(participants);

        // Perform GET request and validate response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/{eventCode}/participant", eventCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify interactions
        verify(participantService).getAll(eventCode);
    }

    @Test
    public void testCreateParticipantSuccess() throws Exception {
        // Prepare test data
        String eventCode = "1234";
        ParticipantDTO participantDTO = new ParticipantDTO("Participant Name",
                "participant@example.com", "1234", "5678");

        // Mock service method
        when(participantService.createOne(anyString(), any(ParticipantDTO.class))).thenReturn(new Participant());

        // Perform POST request and validate response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/{eventCode}/participant", eventCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"Participant Name\",\n" +
                                "    \"email\": \"participant@example.com\",\n" +
                                "    \"iban\": \"1234\",\n" +
                                "    \"bic\": \"5678\"\n" +
                                "}"))
                .andExpect(status().isCreated());

        // Verify interactions
        verify(participantService).createOne(eventCode, participantDTO);
    }

    @Test
    public void testDeleteParticipantSuccess() throws Exception {
        String eventCode = "1234";
        String participantName = "Participant Name";

        // Mock service method
        when(participantService.deleteOne(eventCode, participantName))
                .thenThrow(new NotFoundInDatabaseException("Participant not found"));

        // Perform DELETE request and validate response
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/{eventCode}/participant", eventCode)
                        .param("name", participantName))
                .andExpect(status().isNotFound());

        // Verify interactions
        verify(participantService).deleteOne(eventCode, participantName);
    }

    @Test
    public void testUpdateParticipantSuccess() throws Exception {
        String eventCode = "1234";
        String participantName = "Participant Name";
        ParticipantDTO participantDTO = new ParticipantDTO(participantName, "updated@example.com",
                "NL42 4242 4242 4242 42", "ABCEDFGH");

        // Mock service method
        when(participantService.updateOne(anyString(), anyString(), any(ParticipantDTO.class)))
                .thenReturn(new Participant());

        // Perform PUT request and validate response
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/{eventCode}/participant", eventCode)
                        .param("name", participantName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Participant Name",
                                                "email": "updated@example.com",
                                                "iban": "NL42 4242 4242 4242 42",
                                             "bic":"ABCDEFGH"}"""))
                .andExpect(status().isOk());

        // Verify interactions
        // verify(participantService).updateOne(eventCode, participantName, participantDTO);
    }
}
