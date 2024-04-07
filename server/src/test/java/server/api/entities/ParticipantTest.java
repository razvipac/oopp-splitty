package server.api.entities;

import org.junit.jupiter.api.Test;
import server.entities.event.Event;
import server.entities.participant.Participant;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    private Event event = new Event(
            "test",
            "codecode",
            LocalDateTime.of(1900, 1, 1, 0, 0, 0)
    );

    private Participant p1 = new Participant(
            "A",
            event,
            "a@mail.com",
            "1234",
            "1234"
    );

    private Participant p2 = new Participant(
            "A",
            event,
            "a@mail.com",
            "1234",
            "1234"
    );

    private Participant p3 = new Participant(
            "V",
            event,
            "a@mail.com",
            "1234",
            "1234"
    );

    @Test
    public void testDefaultConstructor() {
        Participant participant = new Participant();
        assertNotNull(participant);
    }

    @Test
    public void testParameterizedConstructor() {

        assertNotNull(p1);
        assertEquals("A", p1.getName());
        assertEquals(event, p1.getEvent());
        assertEquals("a@mail.com", p1.getEmail());
        assertEquals("1234", p1.getIban());
        assertEquals("1234", p1.getBic());
    }

    @Test
    void getName() {
        assertEquals("A", p1.getName());
    }

    @Test
    void setName() {
        p1.setName("NewName");
        assertEquals("NewName", p1.getName());
    }

    @Test
    void getEvent() {
        assertEquals(event, p1.getEvent());
    }

    @Test
    void setEvent() {
        Event newEvent = new Event(
                "newTest",
                "newCodecode",
                LocalDateTime.of(2000, 1, 1, 0, 0, 0)
        );
        p1.setEvent(newEvent);
        assertEquals(newEvent, p1.getEvent());
    }

    @Test
    void getEmail() {
        assertEquals("a@mail.com", p1.getEmail());
    }

    @Test
    void setEmail() {
        p1.setEmail("new@mail.com");
        assertEquals("new@mail.com", p1.getEmail());
    }

    @Test
    void getIban() {
        assertEquals("1234", p1.getIban());
    }

    @Test
    void setIban() {
        p1.setIban("5678");
        assertEquals("5678", p1.getIban());
    }

    @Test
    void getBic() {
        assertEquals("1234", p1.getBic());
    }

    @Test
    void setBic() {
        p1.setBic("5678");
        assertEquals("5678", p1.getBic());
    }

    @Test
    void testEquals() {
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
    }

    @Test
    void testHashCode() {
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testToString() {
        String expected = "Participant{name='A', event=Event@<event_hashcode>, email='a@mail.com'," +
                "iban='1234', bic='1234'}";
        assertNotEquals(expected, p1.toString());
    }

    

}
