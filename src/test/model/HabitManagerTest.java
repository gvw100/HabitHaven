package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// A test class for HabitManager
public class HabitManagerTest extends HabitHelperTest {
    private HabitManager habitManager;

    private Habit h1;
    private Habit h2;
    private Habit h3;

    @BeforeEach
    void runBefore() {
        Clock c1 = getFixedClock("2024-02-16T17:00:00.00Z");
        Clock c2 = getFixedClock("2024-05-02T23:59:00.00Z");
        Clock c3 = getFixedClock("2024-03-15T10:30:00.00Z");
        habitManager = new HabitManager();
        h1 = new Habit("name", "description", Period.WEEKLY, 3, false, c1);
        h2 = new Habit("another name", "another description", Period.DAILY, 15, true, c2);
        h3 = new Habit("and another name", "yet another description", Period.MONTHLY, 7, true, c3);
    }

    @Test
    void testConstructor() {
        assertEquals(0, habitManager.getSize());
    }

    @Test
    void testGetHabits() {
        habitManager.addHabit(h1);
        habitManager.addHabit(h2);
        habitManager.addHabit(h3);
        assertEquals(h1, habitManager.getHabits().get(0));
        assertEquals(h2, habitManager.getHabits().get(1));
        assertEquals(h3, habitManager.getHabits().get(2));
    }

    @Test
    void testGetSize() {
        h1.setNotifyEnabled(true);
        assertEquals(0, habitManager.getSize());
        habitManager.addHabit(h1);
        assertEquals(1, habitManager.getSize());
        habitManager.addHabit(h2);
        assertEquals(2, habitManager.getSize());
        habitManager.deleteHabit(h1);
        assertEquals(1, habitManager.getSize());
        testJobSize(h1.getHabitReminder(), 0);
    }

    @Test
    void testGetAndSetUsername() {
        assertNull(HabitManager.getUsername());
        HabitManager.setUsername("username");
        assertEquals("username", HabitManager.getUsername());
    }

    @Test
    void testAddHabit() {
        habitManager.addHabit(h1);
        assertEquals(h1, habitManager.getHabits().get(0));
        habitManager.addHabit(h2);
        assertEquals(h2, habitManager.getHabits().get(1));
    }

    @Test
    void testDeleteHabit() {
        habitManager.addHabit(h1);
        habitManager.addHabit(h2);
        habitManager.addHabit(h3);
        habitManager.deleteHabit(h2);
        assertEquals(2, habitManager.getSize());
        assertEquals(h1, habitManager.getHabits().get(0));
        assertEquals(h3, habitManager.getHabits().get(1));
        testJobSize(h2.getHabitReminder(), 0);
        habitManager.deleteHabit(h1);
        assertEquals(1, habitManager.getSize());
        assertEquals(h3, habitManager.getHabits().get(0));
    }
}