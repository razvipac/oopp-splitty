// CHECKSTYLE:OFF
package commons;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
class ParticipantTest {
    Participant p1 = new Participant(
            "A",
            new Event(
                    "test",
                    "codecode",
                    LocalDateTime.of(1900, 1, 1, 0, 0, 0)
                    ),
            "a@mail.com",
            "1234",
            "1234"
    );

    Participant p2 = new Participant(
            "A",
            new Event(
                    "test",
                    "codecode",
                    LocalDateTime.of(1900, 1, 1, 0, 0, 0)
            ),
            "a@mail.com",
            "1234",
            "1234"
    );
    Participant p3 = new Participant(
            "V",
            new Event(
                    "test",
                    "codecode",
                    LocalDateTime.of(1900, 1, 1, 0, 0, 0)
            ),
            "a@mail.com",
            "1234",
            "1234"
    );

    @Test
    void getName() {
        assertEquals(p1.getName(), p2.getName());
        assertNotEquals(p1.getName(), p3.getName());
    }
    @Test
    void setName() {
        assertNotEquals(p1, p3);
        p3.setName("A");
        p1.setEvent(p3.getEvent());
        assertEquals(p1, p3);
    }
    @Test
    void testEquals() {
        p1.setEvent(p2.getEvent());
        assertEquals(p1, p2);
        assertEquals(p1, p1);
        assertNotEquals(p1, p3);
    }

    @Test
    void testHashCode() {
        p1.setEvent(p2.getEvent());
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}