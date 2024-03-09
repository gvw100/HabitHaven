package model.reminder;

import javafx.util.Pair;
import model.Habit;
import model.HabitHelperTest;
import model.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// A test class for HabitReminder
public class HabitReminderTest extends HabitHelperTest {
    private HabitReminder dr;
    private HabitReminder wr;
    private HabitReminder mr;


    private Habit wh;

    private Clock dc;
    private Clock wc;
    private Clock mc;

    @BeforeEach
    void runBefore() {
        dc = getFixedClock("2024-01-01T09:00:00Z");
        wc = getFixedClock("2024-04-01T09:00:00Z");
        mc = getFixedClock("2024-07-01T09:00:00Z");

        Habit dh = new Habit("daily habit omg", "daily", model.Period.DAILY, 5, true, dc);
        wh = new Habit("weekly habit?!?!", "weekly", model.Period.WEEKLY, 6, true, wc);
        Habit mh = new Habit("monthly habit!?!?!?!", "monthly", Period.MONTHLY, 7, true, mc);

        dr = dh.getHabitReminder();
        wr = wh.getHabitReminder();
        mr = mh.getHabitReminder();
    }

    @Test
    void testConstructor() {
       assertTrue(dr.isDefault());
       assertTrue(wr.isDefault());
       assertTrue(mr.isDefault());
       assertEquals(dc, dr.clock);
       assertEquals(wh, wr.habit);
    }

    @Test
    void testUpdateDefaultReminders() {
        dr.updateReminders();
        assertEquals(5, dr.reminders.size());
        testJobSize(dr, 4);
    }

    @Test
    void testUpdateCustomReminders() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(wc));
        reminders.add(LocalDateTime.now(wc).plusDays(1));
        reminders.add(LocalDateTime.now(wc).plusDays(2));
        wr.setCustomReminders(reminders);
        wr.cancelReminders();
        wr.updateReminders();
        assertEquals(3, wr.reminders.size());
        testJobSize(wr, 2);
    }

    @Test
    void testIsDefault() {
        assertTrue(dr.isDefault());
        assertTrue(wr.isDefault());
        assertTrue(mr.isDefault());
        Set<LocalDateTime> customReminders = new HashSet<>();
        customReminders.add(LocalDateTime.now(dc));
        dr.setCustomReminders(customReminders);
        assertFalse(dr.isDefault());
        assertTrue(wr.isDefault());
        assertTrue(mr.isDefault());
    }

    @Test
    void testGetClock() {
        assertEquals(dc, dr.getClock());
        assertEquals(wc, wr.getClock());
        assertEquals(mc, mr.getClock());
    }

    @Test
    void testGetActiveReminders() {
        LocalDateTime boundary = LocalDateTime.now(wc);
        LocalDateTime boundaryUpper = LocalDateTime.now(wc).plusNanos(1);
        LocalDateTime boundaryLower = LocalDateTime.now(wc).minusNanos(1);
        LocalDateTime aboveBoundary = LocalDateTime.now(wc).plusDays(1);
        LocalDateTime belowBoundary = LocalDateTime.now(wc).minusDays(1);
        LocalDateTime aboveBoundary2 = LocalDateTime.now(wc).plusHours(1);
        LocalDateTime belowBoundary2 = LocalDateTime.now(wc).minusHours(1);
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(boundary);
        reminders.add(boundaryUpper);
        reminders.add(boundaryLower);
        reminders.add(aboveBoundary);
        reminders.add(belowBoundary);
        reminders.add(aboveBoundary2);
        reminders.add(belowBoundary2);
        wr.setCustomReminders(reminders);
        Set<LocalDateTime> activeReminders = wr.getActiveReminders();
        assertEquals(3, activeReminders.size());
        assertFalse(activeReminders.contains(boundary));
        assertTrue(activeReminders.contains(boundaryUpper));
        assertFalse(activeReminders.contains(boundaryLower));
        assertTrue(activeReminders.contains(aboveBoundary));
        assertFalse(activeReminders.contains(belowBoundary));
        assertTrue(activeReminders.contains(aboveBoundary2));
        assertFalse(activeReminders.contains(belowBoundary2));
    }

    @Test
    void testSetCustomReminders() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(wc));
        reminders.add(LocalDateTime.now(wc).plusDays(1));
        reminders.add(LocalDateTime.now(wc).plusDays(2));
        assertTrue(wr.isDefault());
        wr.setCustomReminders(reminders);
        assertFalse(wr.isDefault());
        assertEquals(3, wr.reminders.size());
        assertTrue(wr.reminders.contains(LocalDateTime.now(wc)));
        assertTrue(wr.reminders.contains(LocalDateTime.now(wc).plusDays(1)));
        assertTrue(wr.reminders.contains(LocalDateTime.now(wc).plusDays(2)));
        testJobSize(wr, 2);
    }

    @Test
    void testSetCustomRemindersPeriodComplete() {
        testJobSize(wr, 5);
        finishHabitNumTimes(wh, wh.getFrequency());
        testJobSize(wr, 0);
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(wc));
        reminders.add(LocalDateTime.now(wc).plusDays(1));
        reminders.add(LocalDateTime.now(wc).plusDays(2));
        reminders.add(LocalDateTime.now(wc).plusDays(3));
        wr.setCustomReminders(reminders);
        testJobSize(wr, 0);
    }

    @Test
    void testSetCustomRemindersMonthlyException() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(mc));
        reminders.add(LocalDateTime.now(mc).plusDays(1));
        reminders.add(LocalDateTime.now(mc).plusDays(2));
        assertTrue(mr.isDefault());
        Exception e = assertThrows(UnsupportedOperationException.class, () -> mr.setCustomReminders(reminders));
        assertEquals("Use setCustomMonthlyReminders instead for MonthlyReminder", e.getMessage());
    }

    @Test
    void testSetDefaultRemindersDaily() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(dc));
        reminders.add(LocalDateTime.now(dc).plusDays(1));
        reminders.add(LocalDateTime.now(dc).plusDays(2));
        dr.setCustomReminders(reminders);
        assertFalse(dr.isDefault());
        assertEquals(3, dr.reminders.size());
        testJobSize(dr, 2);
        dr.setDefaultReminders();
        assertTrue(dr.isDefault());
        assertEquals(5, dr.reminders.size());
        testJobSize(dr, 4);
    }

    @Test
    void testSetDefaultRemindersWeekly() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(wc));
        reminders.add(LocalDateTime.now(wc).plusDays(1));
        reminders.add(LocalDateTime.now(wc).plusDays(2));
        wr.setCustomReminders(reminders);
        assertFalse(wr.isDefault());
        assertEquals(3, wr.reminders.size());
        testJobSize(wr, 2);
        wr.setDefaultReminders();
        assertTrue(wr.isDefault());
        assertEquals(7, wr.reminders.size());
        testJobSize(wr, 5);
    }

    @Test
    void testSetDefaultRemindersMonthly() {
        Set<Pair<Integer, LocalTime>> reminders = new HashSet<>();
        reminders.add(new Pair<>(1, LocalTime.of(6, 30)));
        reminders.add(new Pair<>(2, LocalTime.of(9, 0)));
        reminders.add(new Pair<>(3, LocalTime.of(23, 0)));
        ((MonthlyReminder) mr).setCustomMonthlyReminders(reminders);
        assertFalse(mr.isDefault());
        assertEquals(3, mr.reminders.size());
        testJobSize(mr, 2);
        mr.setDefaultReminders();
        assertTrue(mr.isDefault());
        assertEquals(YearMonth.now(mc).lengthOfMonth(), mr.reminders.size());
        testJobSize(mr, 30);
    }

    @Test
    void testSetDefaultRemindersPeriodComplete() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(wc));
        reminders.add(LocalDateTime.now(wc).plusDays(1));
        reminders.add(LocalDateTime.now(wc).plusDays(2));
        wr.setCustomReminders(reminders);
        testJobSize(wr, 2);
        finishHabitNumTimes(wh, wh.getFrequency());
        testJobSize(wr, 0);
        wr.setDefaultReminders();
        testJobSize(wr, 0);
    }

    @Test
    void testCancelReminders() {
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.now(wc));
        reminders.add(LocalDateTime.now(wc).plusDays(1));
        reminders.add(LocalDateTime.now(wc).plusDays(2));
        wr.setCustomReminders(reminders);
        assertEquals(3, wr.reminders.size());
        testJobSize(wr, 2);
        wr.cancelReminders();
        testJobSize(wr, 0);
        wr.cancelReminders();
        testJobSize(wr, 0);
    }
}