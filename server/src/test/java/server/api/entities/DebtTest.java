package server.api.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.entities.debt.Debt;
import server.entities.debt.DebtId;
import server.entities.event.Event;
import server.entities.expense.Expense;
import server.entities.participant.Participant;

import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DebtTest {

    private LocalDateTime now;
    private LocalDateTime earlier;
    private LocalDateTime later;

    private Event event1;
    private Event event2;
    private Event event3;

    private Expense expense1;
    private Expense expense2;
    private Expense expense3;

    private Participant participant1;
    private Participant participant2;
    private Participant participant3;

    private Debt debt1;
    private Debt debt2;
    private Debt debt3;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        earlier = now.minusDays(1);
        later = now.plusDays(1);

        event1 = new Event("Event A", "CODE1", now);
        event2 = new Event("Event C", "CODE2", now);
        event3 = new Event("Event B", "CODE3", now);

        LocalDate date = LocalDate.now();
        LocalDate specificDate = LocalDate.of(2024, 3, 29);
        LocalDate specificDate2 = LocalDate.of(2024, 3, 29);

        expense1 = new Expense(100.0, "Item 1", null, date);
        expense2 = new Expense(200.0, "Item 2", null, specificDate);
        expense3 = new Expense(300.0, "Item 3", null, specificDate2);

        participant1 = new Participant("Participant 1", null, "email1", "iban1", "bic1");
        participant2 = new Participant("Participant 2", null, "email2", "iban2", "bic2");
        participant3 = new Participant("Participant 3", null, "email3", "iban3", "bic3");

        debt1 = new Debt(participant1, participant2, 100);
        debt2 = new Debt(participant2, participant3, 200);
        debt3 = new Debt(participant3, participant1, 300);
    }

    @Test
    void testParameterizedConstructor() {
        DebtId id = new DebtId(participant1, participant2);
        Debt debt = new Debt(id, 100.0);

        assertEquals(id, debt.getId());
        assertEquals(100.0, debt.getAmount());
        assertFalse(debt.isReceived());
    }

    @Test
    void testDefaultConstructor() {
        Debt debt = new Debt();
        assertNotNull(debt);
    }

    @Test
    void getId() {
        assertNotNull(debt1.getId());
    }

    @Test
    void getDebtor() {
        assertEquals(participant1, debt1.getDebtor());
    }

    @Test
    void getCreditor() {
        assertEquals(participant2, debt1.getCreditor());
    }

    @Test
    void setId() {
        DebtId newId = new DebtId(participant2, participant1);
        debt1.setId(newId);
        assertEquals(newId, debt1.getId());
    }

    @Test
    void getAmount() {
        assertEquals(100, debt1.getAmount());
    }

    @Test
    void setAmount() {
        debt1.setAmount(200);
        assertEquals(200, debt1.getAmount());
    }

    @Test
    void isReceived() {
        assertFalse(debt1.isReceived());
    }

    @Test
    void setReceived() {
        debt1.setReceived(true);
        assertTrue(debt1.isReceived());
    }

    @Test
    void testEquals() {
        Debt debt2Copy = new Debt(participant1, participant2, 100);
        assertEquals(debt1, debt2Copy);
        assertNotEquals(debt1, debt2);
    }

    @Test
    void testHashCode() {
        Debt debt2Copy = new Debt(participant1, participant2, 100);
        assertEquals(debt1.hashCode(), debt2Copy.hashCode());
    }

    @Test
    public void testToString() {
        Participant debtor = new Participant("Debtor", null, "debtor@example.com", "DEBTORIBAN", "DEBTORBIC");
        Participant creditor = new Participant("Creditor", null, "creditor@example.com", "CREDITORIBAN", "CREDITORBIC");
        Debt debt = new Debt(debtor, creditor, 100.0);

        String expected = "Debt{" +
                "debtor=" + debtor +
                ", creditor=" + creditor +
                ", amount=100.0" +
                ", received=false" +
                '}';

        assertEquals(expected, debt.toString());
    }

    @Test
    void testDebtIdDefaultConstructor() {
        DebtId debtId = new DebtId();
        assertNotNull(debtId);
        assertNull(debtId.getDebtor());
        assertNull(debtId.getCreditor());
    }

    @Test
    void testDebtIdParameterizedConstructor() {
        DebtId debtId = new DebtId(participant1, participant2);
        assertEquals(participant1, debtId.getDebtor());
        assertEquals(participant2, debtId.getCreditor());
    }

    @Test
    void testDebtIdGetAndSetDebtor() {
        DebtId debtId = new DebtId();
        debtId.setDebtor(participant1);
        assertEquals(participant1, debtId.getDebtor());
    }

    @Test
    void testDebtIdGetAndSetCreditor() {
        DebtId debtId = new DebtId();
        debtId.setCreditor(participant2);
        assertEquals(participant2, debtId.getCreditor());
    }

    @Test
    void testDebtIdEquals() {
        DebtId debtId1 = new DebtId(participant1, participant2);
        DebtId debtId2 = new DebtId(participant1, participant2);
        assertEquals(debtId1, debtId2);
        assertNotEquals(debtId1, new DebtId(participant2, participant3));
    }

    @Test
    void testDebtIdHashCode() {
        DebtId debtId1 = new DebtId(participant1, participant2);
        DebtId debtId2 = new DebtId(participant1, participant2);
        assertEquals(debtId1.hashCode(), debtId2.hashCode());
    }

    @Test
    void testDebtIdToString() {
        DebtId debtId = new DebtId(participant1, participant2);
        String expected = "DebtId{debtor=" + participant1 + ", creditor=" + participant2 + '}';
        assertEquals(expected, debtId.toString());
    }

}
