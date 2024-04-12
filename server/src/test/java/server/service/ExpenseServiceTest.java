package server.service;

import commons.dto.ExpenseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.expense.ExpenseId;
import server.entities.participant.Participant;
import server.service.exceptions.NotFoundInDatabaseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ExpenseService expenseService;


    @Test
    void testGetAllInEvent() {
        String eventCode = "testEventCode";
        List<Expense> expectedExpenses = Collections.singletonList(new Expense());
        when(expenseRepository.findAllExpensesInEvent(eventCode)).thenReturn(expectedExpenses);

        List<Expense> actualExpenses = expenseService.getAllInEvent(eventCode);

        assertEquals(expectedExpenses, actualExpenses);
    }

    @Test
    void testGetAllInEventAndPaidByParticipant() {
        String name = "A";
        String eventCode = "1234";
        Event event = new Event(name, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food", newName = "New Name";
        Participant participant = new Participant(name, event, email, iban, bic);
        List<Expense> expectedExpenses = Collections.singletonList(new Expense());
        when(expenseRepository.findAllExpensesInEventDependantOnParticipant(eventCode, participant.getName())).thenReturn(expectedExpenses);

        List<Expense> actualExpenses = expenseService.getAllInEventAndPaidByParticipant(eventCode, participant);

        assertEquals(expectedExpenses, actualExpenses);
    }

    @Test
    void testGetOneExpenseFound() throws NotFoundInDatabaseException {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        double price = 123.0;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        Expense expense = new Expense(price, item, participant, date);
        long id = 1L;
        expense.setId(id);
        when(participantRepository.findParticipantByEventCodeAndName(anyString(),anyString()))
                .thenReturn(Optional.of(participant));
        when(expenseRepository.findById(any(ExpenseId.class))).thenReturn(Optional.of(expense));


        Expense actualExpense = expenseService.getOne(eventCode, participantName, id);

        assertEquals(expense, actualExpense);
    }

    @Test
    void testGetOneExpenseNotFound() {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        double price = 123.0;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        Expense expense = new Expense(price, item, participant, date);
        long id = 1L;
        expense.setId(id);
        when(participantRepository.findParticipantByEventCodeAndName(anyString(),anyString()))
                .thenReturn(Optional.of(participant));
        when(expenseRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundInDatabaseException.class, () -> expenseService.getOne(eventCode, participantName, id));
    }

    @Test
    void testCreateOne() throws NotFoundInDatabaseException {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        ExpenseDTO body = new ExpenseDTO(100L, 123.0, "Test item","testParticipant", LocalDate.now());
        Expense expense = new Expense(body.price(), body.item(), participant, body.date());
        when(participantRepository.findParticipantByEventCodeAndName(anyString(),anyString()))
                .thenReturn(Optional.of(participant));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);


        Expense actualExpense = expenseService.createOne(eventCode, body);

        assertEquals(expense, actualExpense);
    }

    @Test
    void testDeleteOneExpenseFound() throws NotFoundInDatabaseException {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        double price = 123.0;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        Expense expense = new Expense(price, item, participant, date);
        long id = 1L;
        expense.setId(id);
        when(participantRepository.findParticipantByEventCodeAndName(anyString(),anyString()))
                .thenReturn(Optional.of(participant));
        when(expenseRepository.findById(any(ExpenseId.class))).thenReturn(Optional.of(expense));
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Expense actualExpense = expenseService.deleteOne(eventCode, participantName, id);

        assertEquals(expense, actualExpense);
        verify(expenseRepository, times(1)).deleteById(any());
    }

    @Test
    void testDeleteOneExpenseNotFound() {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        double price = 123.0;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        Expense expense = new Expense(price, item, participant, date);
        long id = 1L;
        expense.setId(id);
        when(participantRepository.findParticipantByEventCodeAndName(anyString(),anyString()))
                .thenReturn(Optional.of(participant));
        when(expenseRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundInDatabaseException.class, () -> expenseService.deleteOne(eventCode, participantName, id));
        verify(expenseRepository, never()).deleteById(any());
    }

    @Test
    void testUpdateOneExpenseFound() throws NotFoundInDatabaseException {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        double price = 123.0;
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        ExpenseDTO body = new ExpenseDTO(100L, 123.0, "Test item","testParticipant", LocalDate.now());
        Expense expense = new Expense(body.price(), body.item(), participant, body.date());
        when(participantRepository.findParticipantByEventCodeAndName(anyString(),anyString()))
                .thenReturn(Optional.of(participant));
        when(expenseRepository.findById(any(ExpenseId.class))).thenReturn(Optional.of(expense));
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Expense actualExpense = expenseService.updateOne(eventCode, 100L, body);
        assertEquals(expense, actualExpense);
        verify(expenseRepository, times(1)).save(any());
    }

    @Test
    void testUpdateOneExpenseNotFound() {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        ExpenseDTO body = new ExpenseDTO(100L, 123.0, "Test item","testParticipant", LocalDate.now());
        Expense expense = new Expense(body.price(), body.item(), participant, body.date());
        assertThrows(NotFoundInDatabaseException.class, () -> expenseService.updateOne(eventCode, 100L, body));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void testGetOneParticipantParticipantFound() throws NotFoundInDatabaseException {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        when(participantRepository.findParticipantByEventCodeAndName(anyString(), anyString())).thenReturn(Optional.of(participant));
        Participant actualParticipant = expenseService.getOneParticipant(eventCode, participantName);
        assertEquals(participant, actualParticipant);
    }

    @Test
    void testGetOneParticipantParticipantNotFound() {
        String newName = "New Name";
        String eventCode = "testEventCode";
        String participantName = "testParticipant";
        Event event = new Event(newName, eventCode, LocalDateTime.now());
        String email = "123", iban = "134", bic = "123", item = "food";
        Participant participant = new Participant(participantName, event, email, iban, bic);
        participant.setName(newName);
        LocalDate date = LocalDate.ofEpochDay(2024-01-01);
        when(participantRepository.findParticipantByEventCodeAndName(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundInDatabaseException.class, () -> expenseService.getOneParticipant(eventCode, participantName));
    }

    @Test
    void testGetOneEventEventFound() throws NotFoundInDatabaseException {
        String eventCode = "testEventCode";
        Event expectedEvent = new Event();
        when(eventRepository.findById(eventCode)).thenReturn(Optional.of(expectedEvent));

        Event actualEvent = expenseService.getOneEvent(eventCode);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    void testGetOneEventEventNotFound() {
        String eventCode = "testEventCode";
        when(eventRepository.findById(eventCode)).thenReturn(Optional.empty());

        assertThrows(NotFoundInDatabaseException.class, () -> expenseService.getOneEvent(eventCode));
    }

    @Test
    void testUpdateDate() throws NotFoundInDatabaseException {
        String eventCode = "testEventCode";
        Event event = new Event();
        when(eventRepository.findById(eventCode)).thenReturn(Optional.of(event));

        expenseService.updateDate(eventCode);

        assertNotNull(event.getLastActivity());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testUpdateDateEventNotFound() {
        String eventCode = "testEventCode";
        when(eventRepository.findById(eventCode)).thenReturn(Optional.empty());

        assertThrows(NotFoundInDatabaseException.class, () -> expenseService.updateDate(eventCode));
        verify(eventRepository, never()).save(any());
    }

}
