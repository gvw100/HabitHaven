package model;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ReminderScheduler;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// A test class for MonthlyReminder
public class MonthlyReminderTest extends HabitHelperTest {
    private MonthlyReminder mr1;
    private MonthlyReminder mr2;
    private MonthlyReminder mr3;

    private Habit h1;
    private Habit h2;
    private Habit h3;

    private Clock c1;
    private Clock c2;
    private Clock c3;

    @BeforeEach
    void runBefore() {
        c1 = getFixedClock("2024-01-01T09:00:00Z");
        c2 = getFixedClock("2024-02-01T08:59:00Z");
        c3 = getFixedClock("2024-03-15T09:01:00Z");

        h1 = new Habit("habit1", "monthly", Period.MONTHLY, 5, true, c1);
        h2 = new Habit("habit2", "monthly", Period.MONTHLY, 15, true, c2);
        h3 = new Habit("habit3", "monthly", Period.MONTHLY, 1, true, c3);

        mr1 = (MonthlyReminder) h1.getHabitReminder();
        mr2 = (MonthlyReminder) h2.getHabitReminder();
        mr3 = (MonthlyReminder) h3.getHabitReminder();
    }

    @Test
    void testConstructor() {
        assertTrue(mr1.isDefault());
        assertTrue(mr2.isDefault());
        assertTrue(mr3.isDefault());
        assertEquals(c1, mr1.clock);
        assertEquals(c2, mr2.clock);
        assertEquals(c3, mr3.clock);
        assertEquals(h1, mr1.habit);
        assertEquals(h2, mr2.habit);
        assertEquals(h3, mr3.habit);
        testJobSize(mr1, 31);
        testJobSize(mr2, 29);
        testJobSize(mr3, 31);
        assertNull(mr1.getCustomReminders());
        assertNull(mr2.getCustomReminders());
        assertNull(mr3.getCustomReminders());
    }

    @Test
    void testLoadConstructor() {
        Set<LocalDateTime> reminders1 = new HashSet<>();
        for (int i = 0; i < 31; i++) {
            reminders1.add(LocalDateTime.of(2024, 1, i + 1, 9, 0));
        }
        Set<Pair<Integer, LocalTime>> customReminders = new HashSet<>();
        customReminders.add(new Pair<>(1, LocalTime.of(9, 0)));
        customReminders.add(new Pair<>(5, LocalTime.of(9, 0)));
        customReminders.add(new Pair<>(15, LocalTime.of(13, 0)));
        MonthlyReminder mr = new MonthlyReminder(customReminders, reminders1, c1, false, h1, new ReminderScheduler());
        assertEquals(customReminders, mr.getCustomReminders());
        assertEquals(reminders1, mr.reminders);
        assertEquals(c1, mr.clock);
        assertFalse(mr.isDefault());
    }

    @Test
    void testDistributeReminders() {
        mr1.cancelReminders();
        mr1.updateDefaultReminders();
        Set<LocalDateTime> reminders1 = new HashSet<>();
        for (int i = 0; i < 31; i++) {
            reminders1.add(LocalDateTime.of(2024, 1, i + 1, 9, 0));
        }
        testCorrectDistribution(mr1, reminders1);
        testJobSize(mr1, 31);

        mr2.cancelReminders();
        mr2.updateDefaultReminders();
        Set<LocalDateTime> reminders2 = new HashSet<>();
        for (int i = 0; i < 29; i++) {
            reminders2.add(LocalDateTime.of(2024, 2, i + 1, 9, 0));
        }
        testCorrectDistribution(mr2, reminders2);
        testJobSize(mr2, 29);

        mr3.cancelReminders();
        mr3.updateDefaultReminders();
        Set<LocalDateTime> reminders3 = new HashSet<>();
        for (int i = 0; i < 31; i++) {
            reminders3.add(LocalDateTime.of(2024, 3, i + 1, 9, 0));
        }
        testCorrectDistribution(mr3, reminders3);
        testJobSize(mr3, 31);
    }

    @Test
    void testUpdateCustomReminders() {
        Set<Pair<Integer, LocalTime>> cr1 = new HashSet<>();
        cr1.add(new Pair<>(1, LocalTime.of(9, 0)));
        cr1.add(new Pair<>(5, LocalTime.of(9, 0)));
        cr1.add(new Pair<>(15, LocalTime.of(13, 0)));
        mr1.setCustomMonthlyReminders(cr1);
        mr1.cancelReminders();
        mr1.updateCustomReminders();
        testCorrectDistribution(mr1, Set.of(
                LocalDateTime.of(2024, 1, 1, 9, 0),
                LocalDateTime.of(2024, 1, 5, 9, 0),
                LocalDateTime.of(2024, 1, 15, 13, 0)
        ));
        testJobSize(mr1, 3);

        Set<Pair<Integer, LocalTime>> cr2 = new HashSet<>();
        cr2.add(new Pair<>(1, LocalTime.of(9, 0)));
        cr2.add(new Pair<>(15, LocalTime.of(13, 0)));
        cr2.add(new Pair<>(29, LocalTime.of(17, 0)));
        mr2.setCustomMonthlyReminders(cr2);
        mr2.cancelReminders();
        mr2.updateCustomReminders();
        testCorrectDistribution(mr2, Set.of(
                LocalDateTime.of(2024, 2, 1, 9, 0),
                LocalDateTime.of(2024, 2, 15, 13, 0),
                LocalDateTime.of(2024, 2, 29, 17, 0)
        ));
        testJobSize(mr2, 3);

        Set<Pair<Integer, LocalTime>> cr3 = new HashSet<>();
        cr3.add(new Pair<>(1, LocalTime.of(9, 0)));
        cr3.add(new Pair<>(15, LocalTime.of(9, 1)));
        cr3.add(new Pair<>(15, LocalTime.of(9, 2)));
        cr3.add(new Pair<>(15, LocalTime.of(9, 3)));
        cr3.add(new Pair<>(20, LocalTime.of(10, 0)));
        cr3.add(new Pair<>(25, LocalTime.of(11, 0)));
        mr3.setCustomMonthlyReminders(cr3);
        mr3.cancelReminders();
        mr3.updateCustomReminders();
        testCorrectDistribution(mr3, Set.of(
                LocalDateTime.of(2024, 3, 1, 9, 0),
                LocalDateTime.of(2024, 3, 15, 9, 1),
                LocalDateTime.of(2024, 3, 15, 9, 2),
                LocalDateTime.of(2024, 3, 15, 9, 3),
                LocalDateTime.of(2024, 3, 20, 10, 0),
                LocalDateTime.of(2024, 3, 25, 11, 0)
        ));
        testJobSize(mr3, 6);
    }

    @Test
    void testUpdateCustomRemindersShortMonth() {
        Set<Pair<Integer, LocalTime>> customReminders = new HashSet<>();
        customReminders.add(new Pair<>(28, LocalTime.of(9, 0)));
        customReminders.add(new Pair<>(29, LocalTime.of(9, 0)));
        customReminders.add(new Pair<>(30, LocalTime.of(9, 0)));
        customReminders.add(new Pair<>(31, LocalTime.of(9, 0)));
        mr2.setCustomMonthlyReminders(customReminders);
        mr2.cancelReminders();
        mr2.updateCustomReminders();
        testCorrectDistribution(mr2, Set.of(
                LocalDateTime.of(2024, 2, 28, 9, 0),
                LocalDateTime.of(2024, 2, 29, 9, 0)
        ));
        testJobSize(mr2, 2);
    }

    @Test
    void testSetCustomReminders() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(c1));
        reminders.add(LocalDateTime.now(c1).plusDays(1));
        reminders.add(LocalDateTime.now(c1).plusDays(2));
        Exception e = assertThrows(UnsupportedOperationException.class, () -> mr1.setCustomReminders(reminders));
        assertEquals("Use setCustomMonthlyReminders instead for MonthlyReminder", e.getMessage());
    }

    @Test
    void testSetCustomMonthlyReminders() {
        Set<Pair<Integer, LocalTime>> reminders = new HashSet<>();
        reminders.add(new Pair<>(1, LocalTime.of(6, 30)));
        reminders.add(new Pair<>(2, LocalTime.of(9, 0)));
        reminders.add(new Pair<>(3, LocalTime.of(23, 0)));
        assertTrue(mr1.isDefault());
        mr1.setCustomMonthlyReminders(reminders);
        assertFalse(mr1.isDefault());
        assertEquals(3, mr1.getCustomReminders().size());
        assertEquals(3, mr1.reminders.size());
        assertTrue(mr1.reminders.contains(LocalDateTime.of(2024, 1, 1, 6, 30)));
        assertTrue(mr1.reminders.contains(LocalDateTime.of(2024, 1, 2, 9, 0)));
        assertTrue(mr1.reminders.contains(LocalDateTime.of(2024, 1, 3, 23, 0)));
        assertTrue(mr1.getCustomReminders().contains(new Pair<>(1, LocalTime.of(6, 30))));
        assertTrue(mr1.getCustomReminders().contains(new Pair<>(2, LocalTime.of(9, 0))));
        assertTrue(mr1.getCustomReminders().contains(new Pair<>(3, LocalTime.of(23, 0))));
        testJobSize(mr1, 3);
    }

    @Test
    void testSetCustomMonthlyRemindersPeriodComplete() {
        Set<Pair<Integer, LocalTime>> reminders = new HashSet<>();
        reminders.add(new Pair<>(1, LocalTime.of(6, 30)));
        reminders.add(new Pair<>(2, LocalTime.of(9, 0)));
        reminders.add(new Pair<>(3, LocalTime.of(23, 0)));
        assertTrue(mr1.isDefault());
        testJobSize(mr1, 31);
        finishHabitNumTimes(h1, h1.getFrequency());
        testJobSize(mr1, 0);
        mr1.setCustomMonthlyReminders(reminders);
        testJobSize(mr1, 0);
    }
}