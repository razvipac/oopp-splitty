package server.api.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.participant.Participant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private LocalDateTime now;
    private LocalDateTime earlier;
    private LocalDateTime later;

    private Event event1;
    private Event event2;
    private Event event3;
    private Event event4;

    private Expense expense1;
    private Expense expense2;
    private Expense expense3;

    private Participant participant1;
    private Participant participant2;
    private Participant participant3;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        earlier = now.minusDays(1);
        later = now.plusDays(1);

        event1 = new Event("Event A", "CODE1", now);
        event2 = new Event("Event C", "CODE2", now);
        event3 = new Event("Event B", "CODE3", now);
        event4 = new Event("Event D", "CODE4", later);

        LocalDate date = LocalDate.now();
        LocalDate specificDate = LocalDate.of(2024, 3, 29);
        LocalDate specificDate2 = LocalDate.of(2024, 3, 28);

        expense1 = new Expense(100.0, "Item 1", null, date);
        expense2 = new Expense(200.0, "Item 2", null, specificDate);
        expense3 = new Expense(300.0, "Item 3", null, specificDate2);

        participant1 = new Participant("Participant 1", null, "email1", "iban1", "bic1");
        participant2 = new Participant("Participant 2", null, "email2", "iban2", "bic2");
        participant3 = new Participant("Participant 3", null, "email3", "iban3", "bic3");
    }

    @Test
    void getName() {
        assertEquals("Event A", event1.getName());
    }

    @Test
    void getCode() {
        assertEquals("CODE1", event1.getCode());
    }

    @Test
    void getCreationDate() {
        assertEquals(now, event1.getCreationDate());
    }

    @Test
    void getLastActivity() {
        assertNotNull(event1.getLastActivity());
    }

    @Test
    void setName() {
        event1.setName("New Name");
        assertEquals("New Name", event1.getName());
    }

    @Test
    void setCode() {
        event1.setCode("NEWCODE");
        assertEquals("NEWCODE", event1.getCode());
    }

    @Test
    void setCreationDate() {
        event1.setCreationDate(now);
        assertEquals(now, event1.getCreationDate());
    }

    @Test
    void setLastActivity() {
        LocalDateTime newLastActivity = LocalDateTime.now().minusDays(1);
        event1.setLastActivity(newLastActivity);
        assertEquals(newLastActivity, event1.getLastActivity());
    }


    @Test
    public void testEquals() {
        assertEquals(event1, event1);

        assertNotEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertNotEquals(event2, event3);

        assertNotEquals(event1, null);
        assertNotEquals("not an Event", event1);

        assertNotEquals(event1, new Event("Event X", "CODE1", now));
        assertNotEquals(event1, new Event("Event A", "CODEX", now));
        assertNotEquals(event1, new Event("Event A", "CODE1", earlier));
        assertNotEquals(event1, new Event("Event A", "CODE1", later));
    }
    @Test
    void testHashCode() {
        assertNotEquals(event2.hashCode(), event4.hashCode());
    }

    @Test
    void testToString() {
        String expected = "Event Event A:\t- code = CODE1\t- creationDate = "
                + event1.getCreationDate() + "\t- lastActivity = " + event1.getLastActivity();
        assertEquals(expected, event1.toString());
    }

    @Test
    void orderByTitle() {
        List<Event> events = List.of(event1, event2, event3);

        List<Event> sortedEvents = Event.orderByTitle(events);

        assertEquals("Event A", sortedEvents.get(0).getName());
        assertEquals("Event B", sortedEvents.get(1).getName());
        assertEquals("Event C", sortedEvents.get(2).getName());
    }

    @Test
    void orderByLastActivity() {
        List<Event> events = List.of(event1, event2, event3);

        LocalDateTime recentActivity = LocalDateTime.now().plusDays(1);
        event2.setLastActivity(recentActivity);

        LocalDateTime newLastActivity = LocalDateTime.now().minusDays(2);
        event3.setLastActivity(newLastActivity);

        List<Event> sortedEvents = Event.orderByLastActivity(events);

        assertEquals("Event C", sortedEvents.get(0).getName());
        assertEquals("Event A", sortedEvents.get(1).getName());
        assertEquals("Event B", sortedEvents.get(2).getName());
    }

    @Test
    void sumOfAllExpenses() {
        List<Expense> expenses = List.of(expense1, expense2, expense3);

        double totalExpenses = Event.sumOfAllExpenses(expenses);

        assertEquals(600.0, totalExpenses);
    }

    @Test
    void testOrderByCreationDate() {
        List<Event> events = new ArrayList<>();
        Event event1 = new Event("Event 1", "EVT1", LocalDateTime.now().minusDays(1));
        Event event2 = new Event("Event 2", "EVT2", LocalDateTime.now().minusDays(2));
        events.add(event1);
        events.add(event2);

        List<Event> orderedEvents = Event.orderByCreationDate(events);

        assertEquals(event2, orderedEvents.get(0));
        assertEquals(event1, orderedEvents.get(1));
    }

    @Test
    void settleDebts() {
        // Create expenses
        Expense expense1 = new Expense(100.0, "Item 1", participant1, LocalDate.now());
        Expense expense2 = new Expense(200.0, "Item 2", participant2, LocalDate.now());
        Expense expense3 = new Expense(300.0, "Item 3", participant3, LocalDate.now());

        // Associate participants with expenses
        Map<Expense, List<Participant>> expenses = new HashMap<>();
        expenses.put(expense1, List.of(participant1, participant2, participant3));
        expenses.put(expense2, List.of(participant1, participant2, participant3));
        expenses.put(expense3, List.of(participant1, participant2, participant3));

        // Perform settling
        Map<Participant, Map<Participant, Double>> debts = Event.settleDebts(expenses);

        // Ensure debts are settled correctly
        assertEquals(3, debts.size());

        // Ensure correct debts for participant 1
        assertTrue(debts.containsKey(participant1));
        Map<Participant, Double> participant1Debts = debts.get(participant1);
        assertEquals(2, participant1Debts.size());
        assertEquals(66.67, participant1Debts.get(participant2));
        assertEquals(100.0, participant1Debts.get(participant3));

        // Ensure correct debts for participant 2
        assertTrue(debts.containsKey(participant2));
        Map<Participant, Double> participant2Debts = debts.get(participant2);
        assertEquals(2, participant2Debts.size());
        assertEquals(33.33, participant2Debts.get(participant1));

        // Ensure correct debts for participant 3
        assertTrue(debts.containsKey(participant3));
        Map<Participant, Double> participant3Debts = debts.get(participant3);
        assertEquals(2, participant3Debts.size());
        assertEquals(33.33, participant3Debts.get(participant1));
    }



    /*@Test
    void testHashCode() {
        Event event2 = new Event("Event A", "CODE1", event1.getCreationDate());
        assertEquals(event1.hashCode(), event2.hashCode());
    }*/

}
