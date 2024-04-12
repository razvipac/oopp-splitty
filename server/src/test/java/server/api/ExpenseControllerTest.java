// CHECKSTYLE:OFF
package server.api;

import commons.dto.ExpenseDTO;
import server.entities.DTOMapper;
import server.entities.event.Event;
import server.entities.participant.*;
import server.entities.expense.*;
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
import server.database.ExpenseRepository;
import server.service.ExpenseService;
import server.service.exceptions.NotFoundInDatabaseException;


import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private DTOMapper<Expense, ExpenseDTO> expenseDTOMapper;


    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;
    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(expenseController).build();
    }

    @Test
    public void testGet() throws Exception {
        String name = "A";
        String eventCode = "1234";
        Event event = new Event(name, eventCode, LocalDateTime.now());
        String email = "123";
        String iban = "134";
        String bic = "123";
        Participant participant = new Participant(name, event, email, iban, bic);
        double price = 123.0;
        String item = "food";
        String newName = "New Name";
        participant.setName(newName);
        Expense expense = new Expense(price, item, participant, LocalDate.now());
        long id = 1;
        expense.setId(id);
        List<Expense> ev = new LinkedList<>();
        ev.add(expense);

        // Mocking successful update
        when(expenseService.getAllInEvent(eventCode)).thenReturn(ev);
        List<ExpenseDTO> expenseDTOs = ev.stream()
                .map(expenseDTOMapper::toDTO)
                .toList();
        // Perform Get request and validate response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/{eventCode}/expense", eventCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        when(expenseService.getOne(eventCode, newName, id)).thenReturn(expense);
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/{eventCode}/expense", eventCode)
//                        .param("id", String.valueOf(id))
//                        .param("participantName", newName)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());

        // Stubbing to throw NotFoundInDatabaseException
        when(expenseService.getOne(eventCode, newName, id)).thenThrow(NotFoundInDatabaseException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/{eventCode}/expense", eventCode)
                        .param("id", String.valueOf(id))
                        .param("participantName", newName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createExpenseTest() throws Exception {
        String name = "A";
        String eventCode = "1234";
        Event event = new Event(name, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food", newName = "New Name";
        Participant participant = new Participant(name, event, email, iban, bic);
        double price = 123.0;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        Expense expense = new Expense(price, item, participant, date);
        long id = 1L;
        expense.setId(id);
        List<Expense> ev = new LinkedList<>();
        ev.add(expense);


        ExpenseDTO expenseDTO = new ExpenseDTO(id,price, item, participant.getName(), LocalDate.now());
        when(expenseService.createOne(anyString(), any(ExpenseDTO.class))).thenReturn(expense);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/{eventCode}/expense", eventCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "        \"id\": 1,\n" +
                                "        \"price\": 123,\n" +
                                "        \"item\": \"food\",\n" +
                                "        \"paidByName\": \"A\",\n" +
                                "        \"date\": \"2024-01-01\"\n" +
                                "    }"))
                .andExpect(status().isCreated());
        when(expenseService.createOne(anyString(), any(ExpenseDTO.class))).thenThrow(NotFoundInDatabaseException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/{eventCode}/expense", eventCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "        \"id\": 1,\n" +
                                "        \"price\": 123,\n" +
                                "        \"item\": \"food\",\n" +
                                "        \"paidByName\": \"A\",\n" +
                                "        \"date\": \"2024-01-01\"\n" +
                                "    }"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void deleteExpenseTest() throws Exception {
        String name = "A";
        String eventCode = "1234";
        Event event = new Event(name, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food", newName = "New Name";
        Participant participant = new Participant(name, event, email, iban, bic);
        double price = 123;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024 - 01 - 01);
        Expense expense = new Expense(price, item, participant, date);
        long id = 1L;
        expense.setId(id);

        when(expenseService.deleteOne(eventCode, name, id)).thenReturn(expense);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/{eventCode}/expense", eventCode)
                        .param("id", String.valueOf(id))
                        .param("participantName", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        when(expenseService.deleteOne(eventCode, name, id)).thenThrow(NotFoundInDatabaseException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/{eventCode}/expense", eventCode)
                        .param("id", String.valueOf(id))
                        .param("participantName", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
    @Test
    public void updateExpenseTest() throws Exception {
        String name = "A";
        String eventCode = "1234";
        Event event = new Event(name, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food", newName = "New Name";
        Participant participant = new Participant(name, event, email, iban, bic);
        double price = 123;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024 - 01 - 01);
        Expense expense = new Expense(price, item, participant, date);
        long id = 1L;
        expense.setId(id);
        ExpenseDTO expenseDTO = new ExpenseDTO(id,price, item, participant.getName(), LocalDate.now());

        when(expenseService.updateOne(anyString(), anyLong(), any(ExpenseDTO.class))).thenReturn(expense);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/{eventCode}/expense", eventCode)
                        .param("id", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "        \"id\": 1,\n" +
                                "        \"price\": 123,\n" +
                                "        \"item\": \"food\",\n" +
                                "        \"paidByName\": \"A\",\n" +
                                "        \"date\": \"2024-01-01\"\n" +
                                "    }"))
                .andExpect(status().isOk());

        when(expenseService.updateOne(anyString(), anyLong(), any(ExpenseDTO.class)))
                .thenThrow(NotFoundInDatabaseException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/{eventCode}/expense", eventCode)
                        .param("id", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "        \"id\": 1,\n" +
                                "        \"price\": 123,\n" +
                                "        \"item\": \"food\",\n" +
                                "        \"paidByName\": \"A\",\n" +
                                "        \"date\": \"2024-01-01\"\n" +
                                "    }"))
                .andExpect(status().isNotFound());
    }
}
