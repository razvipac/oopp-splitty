// CHECKSTYLE:OFF
package server.api;

import commons.dto.EventDTO;
import commons.dto.ExpenseDTO;
import commons.dto.JSONDumpEventDTO;
import commons.dto.ParticipantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import server.entities.DTOMapper;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.participant.Participant;
import server.service.JSONDumpService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
@ExtendWith(MockitoExtension.class)
public class JSONDumpControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DTOMapper<Event, EventDTO> eventDTOMapper;
    @Mock
    private DTOMapper<Expense, ExpenseDTO> expenseDTOMapper;

    @Mock
    private JSONDumpService jsonDumpService;

    @InjectMocks
    private JSONDumpController jsonDumpController;
    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(jsonDumpController).build();
    }

    @Test
    public void getTest() throws Exception{
        String name = "A";
        String eventCode = "1234";
        Event event = new Event(name, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food", newName = "New Name";
        Participant participant = new Participant(name, event, email, iban, bic);
        double price = 123.0;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024 - 01 - 01);
        Expense expense = new Expense(price, item, participant, date);
        long id = 1L;
        expense.setId(id);
        ParticipantDTO participantDTO = new ParticipantDTO(name, email, iban, bic);
        ExpenseDTO expenseDTO = new ExpenseDTO(id, price, item, participant.getName(), LocalDate.now());
        List<JSONDumpEventDTO> list = new ArrayList<>();
        JSONDumpEventDTO jsonDumpEventDTO =
                new JSONDumpEventDTO(eventDTOMapper.toDTO(event), new ArrayList<>(),
                        new ArrayList<>(), new ArrayList<>());
        jsonDumpEventDTO.participantDTOs().add(participantDTO);
        jsonDumpEventDTO.expenseDTOs().add(expenseDTO);
        list.add(jsonDumpEventDTO);
        when(jsonDumpService.createDump()).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/jsondump")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
