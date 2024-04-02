package model.log;

import model.HabitManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

// Taken from https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
/**
 * Unit tests for the Event class
 */
public class EventTest {
    private Event e;
    private Date d;

    //NOTE: these tests might fail if time at which line (2) below is executed
    //is different from time that line (1) is executed.  Lines (1) and (2) must
    //run in same second for this test to make sense and pass.

    @BeforeEach
    public void runBefore() {
        e = new Event("Sensor open at door");   // (1)
        d = Calendar.getInstance().getTime();   // (2)
    }

    @Test
    public void testEvent() {
        assertEquals("Sensor open at door", e.getDescription());
        Date date = e.getDate();
        boolean sameSecond = (d.before(Date.from(date.toInstant().plusSeconds(1))));
        assertTrue(sameSecond);
    }

    @Test
    public void testEquals() {
        assertFalse(e.equals(null));
        assertNotEquals(e, new HabitManager());
        assertEquals(e, e);
    }

    @Test
    public void testHashCode() {
        assertEquals(13 * e.getDate().hashCode() + e.getDescription().hashCode(), e.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(d.toString() + "\n" + "Sensor open at door", e.toString());
    }
}