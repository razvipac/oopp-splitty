// CHECKSTYLE:OFF
package server.api;

import commons.dto.DebtDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import server.database.DebtRepository;
import server.entities.DTOMapper;
import server.entities.debt.Debt;
import server.entities.event.Event;
import server.entities.participant.Participant;
import server.service.DebtService;
import server.service.exceptions.NotFoundInDatabaseException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class DebtControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DebtRepository debtRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private DTOMapper<Debt, DebtDTO> debtDTOMapper;


    @Mock
    private DebtService debtService;

    @InjectMocks
    private DebtController debtController;
    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(debtController).build();
    }

    @Test
    public void getAllDebtsTest() throws Exception{
        Event event = new Event();
        String eventCode = "1234";
        String newName = "New Name";
        event.setCode(eventCode);
        event.setName(newName);
        String email = "123";
        String iban = "134";
        String bic = "123";
        String name = "A";
        double amount = 123;
        Participant participant = new Participant(name, event, email, iban, bic);
        Debt debt = new Debt(participant, participant, amount);
        List<Debt> debts = new ArrayList<>();
        debts.add(debt);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/{eventCode}/debt", eventCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void updateDebtTest() throws Exception{
        Event event = new Event();
        String eventCode = "1234";
        String newName = "New Name";
        event.setCode(eventCode);
        event.setName(newName);
        String email = "123";
        String iban = "134";
        String bic = "123";
        String name = "A";
        double amount = 123;
        Participant participant = new Participant(name, event, email, iban, bic);
        Debt debt = new Debt(participant, participant, amount);
        DebtDTO debtDTO = new DebtDTO(participant.getName(), participant.getName(), amount, true);

        when(debtService.updateOne(anyString(), any(DebtDTO.class))).thenReturn(debt);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/{eventCode}/debt", eventCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "        \"debtorName\": \"Ivan\",\n" +
                                "        \"creditorName\": \"Ivan\",\n" +
                                "        \"amount\": 123.0,\n" +
                                "        \"received\": true\n" +
                                "    }"))
                .andExpect(status().isOk());

        when(debtService.updateOne(anyString(), any(DebtDTO.class))).thenThrow(NotFoundInDatabaseException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/{eventCode}/debt", eventCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "        \"debtorName\": \"Ivan\",\n" +
                                "        \"creditorName\": \"Ivan\",\n" +
                                "        \"amount\": 123.0,\n" +
                                "        \"received\": true\n" +
                                "    }"))
                .andExpect(status().isNotFound());
    }
}
