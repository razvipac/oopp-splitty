package commons;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class ParticipantTest {
    Participant p1 = new Participant("A", "1@mail", "1234", "4321");
    Participant p2 = new Participant("A", "1@mail", "1234", "4321");
    Participant p3 = new Participant("V", "1@mail", "1234", "4321");

    @Test
    void getName() {
        assertEquals(p1.getName(), p2.getName());
        assertNotEquals(p1.getName(), p3.getName());
    }
    @Test
    void setName() {
        assertNotEquals(p1, p3);
        p3.setName("A");
        assertEquals(p1, p3);
    }
    @Test
    void testEquals() {
        assertTrue(p1.equals(p2));
        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(p3));
    }

    @Test
    void testHashCode() {
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}