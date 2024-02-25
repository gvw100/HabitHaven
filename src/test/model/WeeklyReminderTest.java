package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import ui.ReminderScheduler;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyReminderTest {
    private Habit h1;
    private Habit h2;
    private Habit h3;

    private Clock c1;
    private Clock c2;
    private Clock c3;

    private WeeklyReminder wr1;
    private WeeklyReminder wr2;
    private WeeklyReminder wr3;

    @BeforeEach
    void runBefore() {
        c1 = getFixedClock("2024-02-18T09:00:00Z");
        c2 = getFixedClock("2024-11-10T08:59:00Z");
        c3 = getFixedClock("2024-03-27T09:01:00Z");

        h1 = new Habit("habit1", "weekly", Period.WEEKLY, 15, true, c1);
        h2 = new Habit("habit2", "weekly", Period.WEEKLY, 2, true, c2);
        h3 = new Habit("habit3", "weekly", Period.WEEKLY, 9, true, c3);

        wr1 = (WeeklyReminder) h1.getHabitReminder();
        wr2 = (WeeklyReminder) h2.getHabitReminder();
        wr3 = (WeeklyReminder) h3.getHabitReminder();
    }

    @Test
    void testConstructor() {
        assertTrue(wr1.isDefault());
        assertTrue(wr2.isDefault());
        assertTrue(wr3.isDefault());
        assertEquals(c1, wr1.clock);
        assertEquals(c2, wr2.clock);
        assertEquals(c3, wr3.clock);
        assertEquals(h1, wr1.habit);
        assertEquals(h2, wr2.habit);
        assertEquals(h3, wr3.habit);
        testJobSize(wr1, 6);
        testJobSize(wr2, 7);
        testJobSize(wr3, 3);
    }

    @Test
    void testLoadConstructor() {
        Set<LocalDateTime> reminders1 = new HashSet<>();
        reminders1.add(LocalDateTime.of(2024, 2, 18, 9, 0));
        reminders1.add(LocalDateTime.of(2024, 2, 19, 9, 0));
        reminders1.add(LocalDateTime.of(2024, 2, 20, 9, 0));

        WeeklyReminder wr1 = new WeeklyReminder(reminders1, c1, true, h1, new ReminderScheduler());
        assertEquals(reminders1, wr1.reminders);
        assertEquals(c1, wr1.clock);
        assertTrue(wr1.isDefault());
        assertEquals(h1, wr1.habit);
    }

    @Test
    void testDistributeReminders() {
        wr1.cancelReminders();
        wr1.distributeReminders();
        testCorrectDistribution(wr1, Set.of(
                LocalDateTime.of(2024, 2, 18, 9, 0),
                LocalDateTime.of(2024, 2, 19, 9, 0),
                LocalDateTime.of(2024, 2, 20, 9, 0),
                LocalDateTime.of(2024, 2, 21, 9, 0),
                LocalDateTime.of(2024, 2, 22, 9, 0),
                LocalDateTime.of(2024, 2, 23, 9, 0),
                LocalDateTime.of(2024, 2, 24, 9, 0)
        ));
        testJobSize(wr1, 6);
        wr2.cancelReminders();
        wr2.distributeReminders();
        testCorrectDistribution(wr2, Set.of(
                LocalDateTime.of(2024, 11, 10, 9, 0),
                LocalDateTime.of(2024, 11, 11, 9, 0),
                LocalDateTime.of(2024, 11, 12, 9, 0),
                LocalDateTime.of(2024, 11, 13, 9, 0),
                LocalDateTime.of(2024, 11, 14, 9, 0),
                LocalDateTime.of(2024, 11, 15, 9, 0),
                LocalDateTime.of(2024, 11, 16, 9, 0)
        ));
        testJobSize(wr2, 7);
        wr3.cancelReminders();
        wr3.distributeReminders();
        testCorrectDistribution(wr3, Set.of(
                LocalDateTime.of(2024, 3, 24, 9, 0),
                LocalDateTime.of(2024, 3, 25, 9, 0),
                LocalDateTime.of(2024, 3, 26, 9, 0),
                LocalDateTime.of(2024, 3, 27, 9, 0),
                LocalDateTime.of(2024, 3, 28, 9, 0),
                LocalDateTime.of(2024, 3, 29, 9, 0),
                LocalDateTime.of(2024, 3, 30, 9, 0)
        ));
        testJobSize(wr3, 3);
    }

    @Test
    void testUpdateCustomReminders() {
        Set<LocalDateTime> cr1 = new HashSet<>();
        cr1.add(LocalDateTime.now(c1).plusDays(1));
        cr1.add(LocalDateTime.now(c1).plusDays(3));
        Set<LocalDateTime> copy1 = new HashSet<>(cr1);
        wr1.setCustomReminders(cr1);
        wr1.cancelReminders();
        wr1.updateCustomReminders();
        testCorrectDistribution(wr1, copy1);
        testJobSize(wr1, 2);

        Set<LocalDateTime> cr2 = new HashSet<>();
        cr2.add(LocalDateTime.now(c2));
        cr2.add(LocalDateTime.now(c2).plusDays(1));
        cr2.add(LocalDateTime.now(c2).plusDays(2));
        cr2.add(LocalDateTime.now(c2).plusDays(3));
        Set<LocalDateTime> copy2 = new HashSet<>(cr2);
        wr2.setCustomReminders(cr2);
        wr2.cancelReminders();
        wr2.updateCustomReminders();
        testCorrectDistribution(wr2, copy2);
        testJobSize(wr2, 3);

        Set<LocalDateTime> cr3 = new HashSet<>();
        cr3.add(LocalDateTime.now(c3).minusDays(3));
        cr3.add(LocalDateTime.now(c3).minusDays(2));
        cr3.add(LocalDateTime.now(c3).minusDays(1));
        cr3.add(LocalDateTime.now(c3));
        Set<LocalDateTime> copy3 = new HashSet<>(cr3);
        wr3.setCustomReminders(cr3);
        wr3.cancelReminders();
        wr3.updateCustomReminders();
        testCorrectDistribution(wr3, copy3);
        testJobSize(wr3, 0);
    }

    @Test
    void testMakeWeeklyReminder() {
        Clock c1 = getFixedClock("2024-02-19T09:00:00Z");
        assertEquals(LocalDateTime.of(2024, 2, 18, 23, 59),
                WeeklyReminder.makeWeeklyReminder(DayOfWeek.SUNDAY, LocalTime.of(23, 59), c1));
        Clock c2 = getFixedClock("2024-02-18T10:00:00Z");
        assertEquals(LocalDateTime.of(2024, 2, 20, 3, 29),
                WeeklyReminder.makeWeeklyReminder(DayOfWeek.TUESDAY, LocalTime.of(3, 29), c2));
        Clock c3 = getFixedClock("2024-02-23T23:00:00Z");
        assertEquals(LocalDateTime.of(2024, 2, 23, 15, 30),
                WeeklyReminder.makeWeeklyReminder(DayOfWeek.FRIDAY, LocalTime.of(15, 30), c3));
    }

    void testCorrectDistribution(WeeklyReminder wr, Set<LocalDateTime> reminders) {
        for (LocalDateTime reminder : reminders) {
            assertTrue(wr.reminders.contains(reminder));
        }
        assertEquals(reminders.size(), wr.reminders.size());
    }

    private void testJobSize(WeeklyReminder wr, int size) {
        try {
            assertEquals(size, wr.reminderScheduler.getScheduler()
                    .getJobKeys(GroupMatcher
                            .groupEquals(wr.habit.getId().toString()))
                    .size());
        } catch (SchedulerException e) {
            fail();
        }
    }

    private Clock getFixedClock(String parse) {
        return Clock.fixed(Instant.parse(parse), ZoneId.of("Z"));
    }
}