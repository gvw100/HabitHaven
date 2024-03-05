package model.reminder;

import model.Habit;
import model.HabitHelperTest;
import model.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.reminder.ReminderScheduler;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// A test class for DailyReminder
public class DailyReminderTest extends HabitHelperTest {
    private Habit h1;
    private Habit h2;

    private Clock c1;
    private Clock c2;
    private Clock c3;

    private DailyReminder dr1;
    private DailyReminder dr2;
    private DailyReminder dr3;

    @BeforeEach
    void runBefore() {
        c1 = getFixedClock("2024-08-23T09:00:00Z");
        c2 = getFixedClock("2024-11-12T08:59:00Z");
        c3 = getFixedClock("2024-03-31T09:01:00Z");

        h1 = new Habit("habit1", "daily", model.Period.DAILY, 5, true, c1);
        h2 = new Habit("habit2", "daily", model.Period.DAILY, 7, true, c2);
        Habit h3 = new Habit("habit3", "daily", Period.DAILY, 3, true, c3);

        dr1 = (DailyReminder) h1.getHabitReminder();
        dr2 = (DailyReminder) h2.getHabitReminder();
        dr3 = (DailyReminder) h3.getHabitReminder();
    }

    @Test
    void testConstructor() {
        assertEquals(5, dr1.getHabit().getFrequency());
        assertEquals(7, dr2.getHabit().getFrequency());
        assertTrue(dr1.isDefault());
        assertTrue(dr2.isDefault());
        assertEquals(c1, dr1.clock);
        assertEquals(c2, dr2.clock);
        assertEquals(h1, dr1.habit);
        assertEquals(h2, dr2.habit);
        testJobSize(dr1, 4);
        testJobSize(dr2, 7);
    }

    @Test
    void testLoadConstructor() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.of(2024, 8, 23, 9, 0));
        reminders.add(LocalDateTime.of(2024, 8, 23, 11, 24));
        reminders.add(LocalDateTime.of(2024, 8, 23, 13, 48));
        reminders.add(LocalDateTime.of(2024, 8, 23, 16, 12));
        reminders.add(LocalDateTime.of(2024, 8, 23, 18, 36));
        DailyReminder dr = new DailyReminder(reminders, c1, true, h1, new ReminderScheduler());
        assertEquals(reminders, dr.getReminders());
        assertTrue(dr.isDefault());
        assertEquals(c1, dr.clock);
        assertEquals(h1, dr.habit);
    }

    @Test
    void testDistributeReminders() {
        dr1.cancelReminders();
        dr1.updateDefaultReminders();
        testCorrectDistribution(dr1, Set.of(
                LocalDateTime.of(2024, 8, 23, 9, 0),
                LocalDateTime.of(2024, 8, 23, 11, 24),
                LocalDateTime.of(2024, 8, 23, 13, 48),
                LocalDateTime.of(2024, 8, 23, 16, 12),
                LocalDateTime.of(2024, 8, 23, 18, 36)
        ));
        testJobSize(dr1, 4);
        dr2.cancelReminders();
        dr2.updateDefaultReminders();
        testCorrectDistribution(dr2, Set.of(
                LocalDateTime.of(2024, 11, 12, 9, 0),
                LocalDateTime.of(2024, 11, 12, 10, 43),
                LocalDateTime.of(2024, 11, 12, 12, 26),
                LocalDateTime.of(2024, 11, 12, 14, 9),
                LocalDateTime.of(2024, 11, 12, 15, 52),
                LocalDateTime.of(2024, 11, 12, 17, 35),
                LocalDateTime.of(2024, 11, 12, 19, 18)
        ));
        testJobSize(dr2, 7);
        dr3.cancelReminders();
        dr3.updateDefaultReminders();
        testCorrectDistribution(dr3, Set.of(
                LocalDateTime.of(2024, 3, 31, 9, 0),
                LocalDateTime.of(2024, 3, 31, 13, 0),
                LocalDateTime.of(2024, 3, 31, 17, 0)
        ));
        testJobSize(dr3, 2);
    }

    @Test
    void testUpdateCustomReminders() {
        Set<LocalDateTime> cr1 = new HashSet<>();
        cr1.add(LocalDateTime.now(c1));
        cr1.add(LocalDateTime.now(c1).plusHours(1));
        cr1.add(LocalDateTime.now(c1).plusHours(2));
        Set<LocalDateTime> copy1 = new HashSet<>(cr1);
        dr1.setCustomReminders(cr1);
        dr1.cancelReminders();
        dr1.updateCustomReminders();
        testCorrectDistribution(dr1, copy1);
        testJobSize(dr1, 2);

        Set<LocalDateTime> cr2 = new HashSet<>();
        cr2.add(LocalDateTime.now(c2));
        cr2.add(LocalDateTime.now(c2).plusHours(1));
        cr2.add(LocalDateTime.now(c2).plusHours(2));
        cr2.add(LocalDateTime.now(c2).plusHours(3));
        Set<LocalDateTime> copy2 = new HashSet<>(cr2);
        dr2.setCustomReminders(cr2);
        dr2.cancelReminders();
        dr2.updateCustomReminders();
        testCorrectDistribution(dr2, copy2);
        testJobSize(dr2, 3);

        Set<LocalDateTime> cr3 = new HashSet<>();
        cr3.add(LocalDateTime.now(c3).plusNanos(1));
        cr3.add(LocalDateTime.now(c3).plusHours(1));
        cr3.add(LocalDateTime.now(c3).plusHours(2));
        cr3.add(LocalDateTime.now(c3).plusHours(3));
        cr3.add(LocalDateTime.now(c3).plusHours(4));
        Set<LocalDateTime> copy3 = new HashSet<>(cr3);
        dr3.setCustomReminders(cr3);
        dr3.cancelReminders();
        dr3.updateCustomReminders();
        testCorrectDistribution(dr3, copy3);
        testJobSize(dr3, 5);
    }

    @Test
    void testMakeDailyReminder() {
        LocalDateTime ldt = DailyReminder.makeDailyReminder(LocalTime.of(10, 0), c1);
        assertEquals(LocalDateTime.now(c1).toLocalDate(), ldt.toLocalDate());
        assertEquals(LocalTime.of(10, 0), ldt.toLocalTime());
    }
}