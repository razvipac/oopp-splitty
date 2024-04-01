package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        expense1 = new Expense(100, "Item 1", null, date);
        expense2 = new Expense(200, "Item 2", null, specificDate);
        expense3 = new Expense(300, "Item 3", null, specificDate2);

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

        int totalExpenses = Event.sumOfAllExpenses(expenses);

        assertEquals(600, totalExpenses);
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

    /*@Test
    void testHashCode() {
        Event event2 = new Event("Event A", "CODE1", event1.getCreationDate());
        assertEquals(event1.hashCode(), event2.hashCode());
    }*/

    @Test
    public void testGetDebtorsWithinExpense() {
        Event event = new Event("Test Event", "TEST123", LocalDateTime.now());
        Expense expense = new Expense(100, "Test Expense", participant1, LocalDate.now());
        List<Participant> allParticipants = List.of(participant1, participant2, participant3);

        Set<Participant> debtors = event.getDebtorsWithinExpense(allParticipants, expense);

        assertTrue(debtors.contains(participant2));
        assertTrue(debtors.contains(participant3));
        assertFalse(debtors.contains(participant1));
    }

    @Test
    public void testCalculateTotalExpenses() {
        List<Expense> expenses = new ArrayList<>();
        Participant participant1 = new Participant("Alice", null, "email1", "iban1", "bic1");
        Participant participant2 = new Participant("Bob", null, "email2", "iban2", "bic2");

        expenses.add(new Expense(100, "Test Expense 1", participant1, LocalDate.now()));
        expenses.add(new Expense(200, "Test Expense 2", participant2, LocalDate.of(2024, 3, 29)));
        expenses.add(new Expense(150, "Test Expense 3", participant1, LocalDate.of(2024, 2, 29)));

        // Calculate total expenses by participant
        Map<Participant, Double> totalExpenses = Event.calculateTotalExpenses(expenses);

        // Verify total expenses
        assertEquals(250.0, totalExpenses.get(participant1));
        assertEquals(200.0, totalExpenses.get(participant2));
    }

    @Test
    public void testCalculateIndividualShare() {
        Participant participant1 = new Participant("Alice", null, "email1", "iban1", "bic1");
        Participant participant2 = new Participant("Bob", null, "email2", "iban2", "bic2");
        List<Participant> participants = List.of(participant1, participant2);

        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(100, "Test Expense 1", participant1, LocalDate.now()));
        expenses.add(new Expense(200, "Test Expense 2", participant2, LocalDate.of(2024, 3, 29)));

        // Calculate individual share of expenses
        Map<Participant, Double> individualShare = Event.calculateIndividualShare(participants, expenses);

        // Verify individual share of expenses
        assertEquals(100.0, individualShare.get(participant1));
        assertEquals(50.0, individualShare.get(participant2));
    }

    @Test
    public void testCalculateDebtAmount() {
        Participant debtor = new Participant("Alice", null, "email1", "iban1", "bic1");
        Participant creditor = new Participant("Bob", null, "email2", "iban2", "bic2");

        // Create total expenses and individual share maps
        Map<Participant, Double> totalExpenses = Map.of(creditor, 300.0, debtor, 200.0);
        Map<Participant, Double> individualShare = Map.of(creditor, 150.0, debtor, 100.0);

        // Calculate debt amount
        double debtAmount = Event.calculateDebtAmount(debtor, creditor, totalExpenses, individualShare);

        // Verify debt amount
        assertEquals(150.0, debtAmount);
    }

    @Test
    void settleDebts() {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(100, "Expense 1", participant1, LocalDate.now()));
        expenses.add(new Expense(200, "Expense 2", participant2, LocalDate.of(2024, 3, 29)));
        expenses.add(new Expense(150, "Expense 3", participant3, LocalDate.of(2024, 2, 29)));

        List<Debt> debts = Event.settleDebts(List.of(participant1, participant2, participant3), expenses);

        // Verifying the debts
        assertEquals(6, debts.size());
        for (Debt debt : debts) {
            assertTrue(debt.getAmount() >= 0); // Debt amount should not be negative
        }
    }

    @Test
    void calculateTotalExpenses() {
        List<Expense> expenses = new ArrayList<>();

        expenses.add(new Expense(100, "Test Expense 1", participant1, LocalDate.now()));
        expenses.add(new Expense(200, "Test Expense 2", participant2, LocalDate.of(2024, 3, 29)));
        expenses.add(new Expense(150, "Test Expense 3", participant1, LocalDate.of(2024, 2, 29)));

        Map<Participant, Double> totalExpenses = Event.calculateTotalExpenses(expenses);

        assertEquals(250.0, totalExpenses.get(participant1));
        assertEquals(200.0, totalExpenses.get(participant2));
    }

    @Test
    void calculateDebtAmount() {
        Participant debtor = new Participant("Alice", null, "email1", "iban1", "bic1");
        Participant creditor = new Participant("Bob", null, "email2", "iban2", "bic2");

        Map<Participant, Double> totalExpenses = Map.of(creditor, 300.0, debtor, 200.0);
        Map<Participant, Double> individualShare = Map.of(creditor, 150.0, debtor, 100.0);

        double debtAmount = Event.calculateDebtAmount(debtor, creditor, totalExpenses, individualShare);

        assertEquals(150.0, debtAmount);
    }


}
