package model;

import javafx.util.Pair;
import model.achievement.Achievement;
import model.achievement.AchievementType;
import model.reminder.DailyReminder;
import model.reminder.HabitReminder;
import model.reminder.MonthlyReminder;
import model.reminder.WeeklyReminder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.reminder.ReminderScheduler;

import java.time.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;
import static model.achievement.AchievementTier.BRONZE;
import static org.junit.jupiter.api.Assertions.*;

// A test class for Habit
public class HabitTest extends HabitHelperTest {
    private Clock c1;
    private Clock c2;
    private Clock c3;

    private Habit h1;
    private Habit h2;
    private Habit h3;
    private Habit h4;

    @BeforeEach
    void runBefore() {
        c1 = getFixedClock("2024-02-16T17:00:00.00Z");
        c2 = getFixedClock("2024-05-02T23:59:00.00Z");
        c3 = getFixedClock("2024-03-15T10:30:00.00Z");
        Clock c4 = getFixedClock("2024-06-30T23:59:00.00Z");

        h1 = new Habit("name", "description", Period.WEEKLY, 3, true, c1);
        h2 = new Habit("another name", "another description", Period.DAILY, 15, false, c2);
        h3 = new Habit("and another name", "yet another description", Period.MONTHLY, 7, true, c3);
        h4 = new Habit("please no more habits", "and no more descriptions", Period.MONTHLY, 5, false, c4);
    }

    @Test
    void testConstructor1() {
        assertEquals("name", h1.getName());
        assertEquals("description", h1.getDescription());
        assertTrue(h1.isNotifyEnabled());
        assertEquals(Period.WEEKLY, h1.getPeriod());
        assertEquals(3, h1.getFrequency());
        assertTrue(h1.isNotifyEnabled());
        assertEquals(0, h1.getNumSuccess());
        assertEquals(c1, h1.getClock());
        assertFalse(h1.isPreviousComplete());
        assertTrue(h1.getHabitReminder() instanceof WeeklyReminder);
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 17, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 24, 23, 59), h1.getNextPeriodEnd());
        assertFalse(h1.isArchived());
        checkResetStats(h1);
    }

    @Test
    void testConstructor2() {
        assertEquals("another name", h2.getName());
        assertEquals("another description", h2.getDescription());
        assertFalse(h2.isNotifyEnabled());
        assertEquals(Period.DAILY, h2.getPeriod());
        assertEquals(15, h2.getFrequency());
        assertFalse(h2.isNotifyEnabled());
        assertEquals(0, h2.getNumSuccess());
        assertEquals(c2, h2.getClock());
        assertFalse(h1.isPreviousComplete());
        assertNull(h2.getHabitReminder());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 2, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 3, 23, 59), h2.getNextPeriodEnd());
        assertFalse(h2.isArchived());
        checkResetStats(h2);
    }

    @Test
    void testLoadConstructor() {
        String n = "a name";
        String d = "a description";
        Period p = Period.DAILY;
        int f = 10;
        UUID id = UUID.randomUUID();
        boolean ne = true;
        int ns = 5;
        LocalDateTime cpe = LocalDateTime.of(2024, Month.MARCH, 15, 23, 59);
        LocalDateTime npe = LocalDateTime.of(2024, Month.MARCH, 16, 23, 59);
        boolean ipc = false;
        Clock c = getFixedClock("2024-03-15T10:30:00.00Z");
        HabitStatistics hs = new HabitStatistics(0, 3, 10, 2, 1);
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.of(2024, Month.MARCH, 15, 10, 0));
        reminders.add(LocalDateTime.of(2024, Month.MARCH, 15, 23, 59));
        Habit h = new Habit(n, d, p, f, id, ne, ns, cpe, npe, ipc, c, hs, null, true);
        assertEquals(n, h.getName());
        assertEquals(d, h.getDescription());
        assertEquals(p, h.getPeriod());
        assertEquals(f, h.getFrequency());
        assertEquals(id, h.getId());
        assertEquals(ne, h.isNotifyEnabled());
        assertEquals(ns, h.getNumSuccess());
        assertEquals(cpe, h.getCurrentPeriodEnd());
        assertEquals(npe, h.getNextPeriodEnd());
        assertEquals(ipc, h.isPreviousComplete());
        assertEquals(c, h.getClock());
        assertEquals(hs, h.getHabitStats());
        assertNull(h.getHabitReminder());
        assertTrue(h.isArchived());
        HabitReminder hr = new DailyReminder(reminders, c, false, h, new ReminderScheduler());
        h.setHabitReminder(hr);
        assertEquals(hr, h.getHabitReminder());
    }

    @Test
    void testSetName() {
        String name = "a different name from before";
        h1.setName(name);
        assertEquals(name, h1.getName());
    }

    @Test
    void testSetDescription() {
        String description = "a different description from before";
        h2.setDescription(description);
        assertEquals(description, h2.getDescription());
    }

    @Test
    void testToggleNotifyEnabledFalseToTrue() {
        h2.setClock(getFixedClock("2024-05-02T11:59:00.00Z"));
        assertFalse(h2.isNotifyEnabled());
        assertNull(h2.getHabitReminder());
        h2.toggleNotifyEnabled();
        assertTrue(h2.isNotifyEnabled());
        assertTrue(h2.getHabitReminder() instanceof DailyReminder);
        testJobSize(h2.getHabitReminder(), 11);
    }

    @Test
    void testToggleNotifyEnabledTrueToFalse() {
        HabitReminder hr = h3.getHabitReminder();
        assertTrue(h3.isNotifyEnabled());
        assertTrue(h3.getHabitReminder() instanceof MonthlyReminder);
        long daysLeft = DAYS.between(LocalDateTime.now(c3), h3.getCurrentPeriodEnd());
        assertEquals((int) daysLeft, hr.getActiveReminders().size());
        testJobSize(hr, (int) daysLeft);
        h3.toggleNotifyEnabled();
        assertFalse(h3.isNotifyEnabled());
        assertNull(h3.getHabitReminder());
        testJobSize(hr, 0);
    }

    @Test
    void testToggleNotifyEnabledFalseToTrueAndPeriodComplete() {
        assertFalse(h4.isNotifyEnabled());
        assertNull(h4.getHabitReminder());
        finishHabitNumTimes(h4, h4.getFrequency());
        assertTrue(h4.isPeriodComplete());
        h4.toggleNotifyEnabled();
        assertTrue(h4.isNotifyEnabled());
        assertTrue(h4.getHabitReminder() instanceof MonthlyReminder);
        testJobSize(h4.getHabitReminder(), 0);
    }

    @Test
    void testToggleIsArchivedFalseToTrue() {
        finishHabitNumTimes(h1, 3);
        checkStats(h1, 1, 1, 3, 1, 0);
        h1.setClock(getFixedClock("2024-02-18T00:00:00.00Z"));
        h1.toggleIsArchived();
        assertTrue(h1.isArchived());
        checkStats(h1, 1, 1, 3, 1, 0);
    }

    @Test
    void testToggleIsArchivedTrueToFalse() {
        finishHabitNumTimes(h1, 3);
        h1.toggleIsArchived();
        assertTrue(h1.isArchived());
        h1.setClock(getFixedClock("2024-02-18T00:00:00.00Z"));
        checkStats(h1, 1, 1, 3, 1, 0);
        h1.toggleIsArchived();
        assertFalse(h1.isArchived());
        checkStats(h1, 1, 1, 3, 1, 1);
    }

    @Test
    void testSetFrequencyDifferentFromBefore() {
        finishHabitNumTimes(h1,3);
        assertNotEquals(0, h1.getAchievements().size());
        assertEquals(3, h1.getNumSuccess());
        assertTrue(h1.setFrequency(7));
        assertEquals(7, h1.getFrequency());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
        assertEquals(0, h1.getAchievements().size());
    }

    @Test
    void testSetFrequencySameAsBefore() {
        h1.finishHabit();
        assertEquals(1, h1.getNumSuccess());
        assertTrue(h1.setFrequency(5));
        assertEquals(5, h1.getFrequency());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
        finishHabitNumTimes(h1,5);
        assertNotEquals(0, h1.getAchievements().size());
        assertFalse(h1.setFrequency(5));
        assertEquals(5, h1.getFrequency());
        assertEquals(5, h1.getNumSuccess());
        checkStats(h1, 1, 1, 5, 1, 0);
        assertNotEquals(0, h1.getAchievements().size());
    }

    @Test
    void testSetFrequencyNotDailyAndNotificationsNotEnabled() {
        assertTrue(h4.setFrequency(10));
        assertEquals(10, h4.getFrequency());
        assertNull(h4.getHabitReminder());
        assertFalse(h4.getHabitReminder() instanceof DailyReminder);
    }

    @Test
    void testSetFrequencyNotDailyAndDefaultNotificationsEnabled() {
        assertTrue(h1.setFrequency(11));
        assertEquals(11, h1.getFrequency());
        assertTrue(h1.getHabitReminder().isDefault());
        assertTrue(h1.getHabitReminder() instanceof WeeklyReminder);
        assertFalse(h1.getHabitReminder() instanceof DailyReminder);
    }

    @Test
    void testSetFrequencyNotDailyAndCustomNotificationsEnabled() {
        Set<Pair<Integer, LocalTime>> customReminders = new HashSet<>();
        customReminders.add(new Pair<>(8, LocalTime.of(10, 0)));
        assertTrue(h3.getHabitReminder().isDefault());
        ((MonthlyReminder) h3.getHabitReminder()).setCustomMonthlyReminders(customReminders);
        assertFalse(h3.getHabitReminder().isDefault());
        assertTrue(h3.setFrequency(8));
        assertEquals(8, h3.getFrequency());
        assertFalse(h3.getHabitReminder().isDefault());
        assertTrue(h3.getHabitReminder() instanceof MonthlyReminder);
        assertFalse(h3.getHabitReminder() instanceof DailyReminder);
    }

    @Test
    void testSetFrequencyDailyAndNotificationsNotEnabled() {
        h4.setPeriod(Period.DAILY);
        assertTrue(h4.setFrequency(1));
        assertEquals(1, h4.getFrequency());
        assertNull(h4.getHabitReminder());
        assertFalse(h4.getHabitReminder() instanceof DailyReminder);
    }

    @Test
    void testSetFrequencyDailyAndCustomNotificationsEnabled() {
        h3.setPeriod(Period.DAILY);
        Set<LocalDateTime> customReminders = new HashSet<>();
        customReminders.add(LocalDateTime.of(2024, Month.MARCH, 15, 10, 0));
        assertTrue(h3.getHabitReminder().isDefault());
        h3.getHabitReminder().setCustomReminders(customReminders);
        assertFalse(h3.getHabitReminder().isDefault());
        assertTrue(h3.setFrequency(8));
        assertEquals(8, h3.getFrequency());
        assertTrue(h3.getHabitReminder() instanceof DailyReminder);
    }

    @Test
    void testSetFrequencyDailyAndDefaultNotificationsEnabled() {
        h2.setClock(getFixedClock("2024-05-02T09:00:00.00Z"));
        h2.toggleNotifyEnabled();
        assertTrue(h2.getHabitReminder().isDefault());
        assertTrue(h2.setFrequency(7));
        assertEquals(7, h2.getFrequency());
        assertTrue(h2.getHabitReminder() instanceof DailyReminder);
        testJobSize(h2.getHabitReminder(), 6);
    }

    @Test
    void setFrequencyNotificationsEnabledAndPeriodWasComplete() {
        testJobSize(h1.getHabitReminder(), 1);
        finishHabitNumTimes(h1, 3);
        testJobSize(h1.getHabitReminder(), 0);
        assertTrue(h1.isPreviousComplete());
        assertTrue(h1.isPeriodComplete());
        assertNotEquals(0, h1.getAchievements().size());
        assertTrue(h1.setFrequency(7));
        testJobSize(h1.getHabitReminder(), 1);
        assertEquals(7, h1.getFrequency());
        assertFalse(h1.isPreviousComplete());
        assertFalse(h1.isPeriodComplete());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
        assertEquals(0, h1.getAchievements().size());
    }

    @Test
    void testSetPeriodDifferentFromBefore() {
        finishHabitNumTimes(h1, 3);
        assertEquals(3, h1.getNumSuccess());
        assertNotEquals(0, h1.getAchievements().size());
        assertTrue(h1.setPeriod(Period.DAILY));
        assertEquals(Period.DAILY, h1.getPeriod());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
        assertEquals(0, h1.getAchievements().size());
    }

    @Test
    void testSetPeriodSameAsBefore() {
        h1.finishHabit();
        assertEquals(1, h1.getNumSuccess());
        assertNotEquals(0, h1.getAchievements().size());
        assertTrue(h1.setPeriod(Period.DAILY));
        assertEquals(Period.DAILY, h1.getPeriod());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
        assertEquals(0, h1.getAchievements().size());
        finishHabitNumTimes(h1,3);
        assertNotEquals(0, h1.getAchievements().size());
        assertFalse(h1.setPeriod(Period.DAILY));
        assertEquals(Period.DAILY, h1.getPeriod());
        assertEquals(3, h1.getNumSuccess());
        checkStats(h1, 1, 1, 3, 1, 0);
        assertNotEquals(0, h1.getAchievements().size());
    }

    @Test
    void testSetPeriodDifferentFromBeforeAndNotificationsNotEnabled() {
        assertTrue(h4.setPeriod(Period.WEEKLY));
        assertEquals(Period.WEEKLY, h4.getPeriod());
        assertNull(h4.getHabitReminder());
        assertFalse(h4.getHabitReminder() instanceof WeeklyReminder);
    }
    
    @Test
    void testSetPeriodDifferentFromBeforeAndDefaultNotificationsEnabled() {
        h1.setClock(getFixedClock("2024-02-14T08:59:00.00Z"));
        assertTrue(h1.getHabitReminder() instanceof WeeklyReminder);
        assertTrue(h1.getHabitReminder().isDefault());
        assertTrue(h1.setPeriod(Period.MONTHLY));
        assertEquals(Period.MONTHLY, h1.getPeriod());
        assertTrue(h1.getHabitReminder().isDefault());
        assertTrue(h1.getHabitReminder() instanceof MonthlyReminder);
        assertFalse(h1.getHabitReminder() instanceof WeeklyReminder);
        testJobSize(h1.getHabitReminder(), 16);
    }

    @Test
    void testSetPeriodDifferentFromBeforeAndCustomNotificationsEnabled() {
        Set<Pair<Integer, LocalTime>> customReminders = new HashSet<>();
        customReminders.add(new Pair<>(23, LocalTime.of(23, 59)));
        assertTrue(h3.getHabitReminder().isDefault());
        ((MonthlyReminder) h3.getHabitReminder()).setCustomMonthlyReminders(customReminders);
        assertFalse(h3.getHabitReminder().isDefault());
        assertTrue(h3.setPeriod(Period.WEEKLY));
        assertEquals(Period.WEEKLY, h3.getPeriod());
        assertTrue(h3.getHabitReminder().isDefault());
        assertTrue(h3.getHabitReminder() instanceof WeeklyReminder);
        assertFalse(h3.getHabitReminder() instanceof MonthlyReminder);
        testJobSize(h3.getHabitReminder(), 1);
    }

    @Test
    void testSetPeriodSameAsBeforeAndNotificationsEnabled() {
        h2.toggleNotifyEnabled();
        assertTrue(h2.isNotifyEnabled());
        assertFalse(h2.setPeriod(Period.DAILY));
        assertEquals(Period.DAILY, h2.getPeriod());
        assertTrue(h2.getHabitReminder() instanceof DailyReminder);
    }

    @Test
    void testSetClock() {
        h1.setClock(c2);
        assertEquals(c2, h1.getClock());
    }

    @Test
    void testSetNumSuccess() {
        h2.setNumSuccess(10);
        assertEquals(10, h2.getNumSuccess());
    }

    @Test
    void testSetHabitReminder() {
        h1.getHabitReminder().cancelReminders();
        h1.setHabitReminder(new DailyReminder(c1, h1));
        assertTrue(h1.getHabitReminder() instanceof DailyReminder);
        h2.setHabitReminder(new WeeklyReminder(c2, h2));
        assertTrue(h2.getHabitReminder() instanceof WeeklyReminder);
        h3.getHabitReminder().cancelReminders();
        h3.setHabitReminder(new MonthlyReminder(c3, h3));
        assertTrue(h3.getHabitReminder() instanceof MonthlyReminder);
        h4.setHabitReminder(new MonthlyReminder(c3, h4));
        assertTrue(h4.getHabitReminder() instanceof MonthlyReminder);
    }

    @Test
    void testGetNewReminder() {
        h2.toggleNotifyEnabled();
        h4.toggleNotifyEnabled();
        h1.getHabitReminder().cancelReminders();
        h2.getHabitReminder().cancelReminders();
        h3.getHabitReminder().cancelReminders();
        h4.getHabitReminder().cancelReminders();
        assertTrue(h1.getNewReminder() instanceof WeeklyReminder);
        assertTrue(h2.getNewReminder() instanceof DailyReminder);
        assertTrue(h3.getNewReminder() instanceof MonthlyReminder);
        assertTrue(h4.getNewReminder() instanceof MonthlyReminder);
    }

    @Test
    void testGetAchievements() {
        assertEquals(0, h1.getAchievements().size());
        h1.finishHabit();
        List<Achievement> achievements = h1.getAchievements();
        assertEquals(1, achievements.size());
        assertEquals(new Achievement("Weekly First Time?",
                "Complete the habit for the first time", 1,
                AchievementType.SINGULAR_SUCCESSES, BRONZE), achievements.get(0));
        h1.finishHabit();
        List<Achievement> achievements1 = h1.getAchievements();
        assertEquals(2, achievements1.size());
        assertEquals(new Achievement("Weekly First Time?",
                "Complete the habit for the first time", 1,
                AchievementType.SINGULAR_SUCCESSES, BRONZE), achievements1.get(0));
        assertEquals(new Achievement("Double Completion",
                "Complete the habit for the second time", 2,
                AchievementType.SINGULAR_SUCCESSES, BRONZE), achievements1.get(1));
    }

    @Test
    void testGetters() {
        finishHabitNumTimes(h2, 15);
        assertEquals("another name", h2.getName());
        assertEquals("another description", h2.getDescription());
        assertFalse(h2.isNotifyEnabled());
        assertNull(h2.getHabitReminder());
        h2.toggleNotifyEnabled();
        assertTrue(h2.isNotifyEnabled());
        assertTrue(h2.getHabitReminder() instanceof DailyReminder);
        assertEquals(15, h2.getFrequency());
        assertEquals(Period.DAILY, h2.getPeriod());
        assertEquals(15, h2.getNumSuccess());
        assertEquals(c2, h2.getClock());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 2, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 3, 23, 59), h2.getNextPeriodEnd());
    }

    @Test
    void testIsPeriodComplete() {
        assertFalse(h3.isPeriodComplete());
        finishHabitNumTimes(h3, 2);
        assertFalse(h3.isPeriodComplete());
        finishHabitNumTimes(h3, 5);
        assertTrue(h3.isPeriodComplete());
    }

    @Test
    void testIsPreviousComplete() {
        assertFalse(h3.isPreviousComplete());
        finishHabitNumTimes(h3, 7);
        assertTrue(h3.isPreviousComplete());
    }

    @Test
    void testFinishHabitNumSuccessLessThanFrequency() {
        assertEquals(0, h2.getAchievements().size());
        assertTrue(finishHabitNumTimes(h2, 9));
        assertEquals(9, h2.getNumSuccess());
        checkStats(h2, 0, 0, 9, 0, 0);
        assertNotEquals(0, h2.getAchievements().size());
    }

    @Test
    void testFinishHabitNumSuccessSameAsFrequency() {
        h2.setClock(getFixedClock("2024-05-02T15:23:00.00Z"));
        h2.toggleNotifyEnabled();
        testJobSize(h2.getHabitReminder(), 7);
        assertEquals(0, h2.getAchievements().size());
        assertTrue(finishHabitNumTimes(h2, 15));
        assertEquals(15, h2.getNumSuccess());
        checkStats(h2, 1, 1, 15, 1, 0);
        testJobSize(h2.getHabitReminder(), 0);
        assertNotEquals(0, h2.getAchievements().size());
    }

    @Test
    void testFinishHabitNumSuccessGreaterThanFrequency() {
        h2.setClock(getFixedClock("2024-05-02T15:24:00.00Z"));
        h2.toggleNotifyEnabled();
        testJobSize(h2.getHabitReminder(), 6);
        assertFalse(finishHabitNumTimes(h1, 10));
        assertEquals(3, h1.getNumSuccess());
        checkStats(h1, 1, 1, 3, 1, 0);
        assertNotEquals(0, h1.getAchievements().size());
        assertEquals(0, h2.getAchievements().size());
        assertFalse(finishHabitNumTimes(h2, 16));
        assertEquals(15, h2.getNumSuccess());
        checkStats(h2, 1, 1, 15, 1, 0);
        testJobSize(h2.getHabitReminder(), 0);
        assertNotEquals(0, h2.getAchievements().size());
    }

    @Test
    void testUndoFinishHabitNumSuccessIsZero() {
        assertFalse(h1.undoFinishHabit());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
    }

    @Test
    void testUndoFinishHabitNumSuccessNotEqualToFrequency() {
        assertEquals(0, h2.getAchievements().size());
        finishHabitNumTimes(h2, 10);
        int size1 = h2.getAchievements().size();
        assertNotEquals(0, size1);
        assertTrue(h2.undoFinishHabit());
        assertEquals(9, h2.getNumSuccess());
        checkStats(h2, 0, 0, 9, 0, 0);
        int size2 = h2.getAchievements().size();
        assertNotEquals(0, size2);
        assertNotEquals(size1, size2);
    }

    @Test
    void testUndoFinishHabitNumSuccessEqualToFrequency() {
        h2.setClock(getFixedClock("2024-05-02T13:00:00.00Z"));
        h2.toggleNotifyEnabled();
        testJobSize(h2.getHabitReminder(), 9);
        assertEquals(0, h2.getAchievements().size());
        finishHabitNumTimes(h2, 15);
        testJobSize(h2.getHabitReminder(), 0);
        checkStats(h2, 1, 1, 15, 1, 0);
        assertTrue(h2.isPreviousComplete());
        int size1 = h2.getAchievements().size();
        assertNotEquals(0, size1);
        assertTrue(h2.undoFinishHabit());
        assertEquals(14, h2.getNumSuccess());
        checkStats(h2, 0, 0, 14, 0, 0);
        assertFalse(h2.isPreviousComplete());
        testJobSize(h2.getHabitReminder(), 9);
        int size2 = h2.getAchievements().size();
        assertNotEquals(0, size2);
        assertNotEquals(size1, size2);
    }

    @Test
    void testCheckPeriodNotYetCompleted() {
        h1.checkPeriodComplete();
        assertFalse(h1.isPreviousComplete());
        checkStats(h1, 0, 0, 0, 0, 0);
        testJobSize(h1.getHabitReminder(), 1);
        h1.setNumSuccess(2);
        h1.checkPeriodComplete();
        assertFalse(h1.isPreviousComplete());
        checkStats(h1, 0, 0, 0, 0, 0);
        testJobSize(h1.getHabitReminder(), 1);
    }

    @Test
    void testCheckPeriodCompleted() {
        h1.checkPeriodComplete();
        assertFalse(h1.isPreviousComplete());
        checkStats(h1, 0, 0, 0, 0, 0);
        testJobSize(h1.getHabitReminder(), 1);
        h1.setNumSuccess(3);
        h1.checkPeriodComplete();
        assertTrue(h1.isPreviousComplete());
        checkStats(h1, 1, 1, 0, 1, 0);
        testJobSize(h1.getHabitReminder(), 0);
    }

    @Test
    void testNextPeriodWithinNextPeriod() {
        Clock nextWeek = getFixedClock("2024-02-23T17:00:00.00Z");
        h1.setClock(nextWeek);
        h1.nextHabitPeriod();
        assertEquals(0, h1.getNumSuccess());
        assertEquals(1, h1.getHabitStats().getNumPeriod());
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 24, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 2, 23, 59), h1.getNextPeriodEnd());
    }

    @Test
    void testNextPeriodAfterNextPeriod() {
        Clock afterNextWeek = getFixedClock("2024-04-01T00:50:00.00Z");
        h1.setClock(afterNextWeek);
        h1.nextHabitPeriod();
        assertEquals(0, h1.getNumSuccess());
        assertEquals(1, h1.getHabitStats().getNumPeriod());
        assertEquals(LocalDateTime.of(2024, Month.APRIL, 6, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.APRIL, 13, 23, 59), h1.getNextPeriodEnd());
    }

    @Test
    void testResetProgress() {
        h2.setClock(getFixedClock("2024-05-02T17:48:00.00Z"));
        h2.toggleNotifyEnabled();
        testJobSize(h2.getHabitReminder(), 3);
        finishHabitNumTimes(h2, 15);
        testJobSize(h2.getHabitReminder(), 0);
        assertEquals(15, h2.getNumSuccess());
        checkStats(h2, 1, 1, 15, 1, 0);
        h2.resetProgress();
        assertEquals(0, h2.getNumSuccess());
        assertFalse(h2.isPreviousComplete());
        checkResetStats(h2);
    }

    @Test
    void testUpdateHabitArchived() {
        finishHabitNumTimes(h3, 7);
        Clock clock = getFixedClock("2024-04-05T12:30:00.00Z");
        h3.setClock(clock);
        h3.toggleIsArchived();
        assertTrue(h3.isArchived());
        assertFalse(h3.updateHabit());
        assertEquals(7, h3.getNumSuccess());
        checkStats(h3, 1, 1, 7, 1, 0);
    }

    @Test
    void testUpdateHabitNotAfterCurrentPeriodEnd() {
        finishHabitNumTimes(h3, 7);
        assertFalse(h3.updateHabit());
        assertEquals(7, h3.getNumSuccess());
        checkStats(h3, 1, 1, 7, 1, 0);
        assertTrue(h3.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 31, 23, 59), h3.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.APRIL, 30, 23, 59), h3.getNextPeriodEnd());
    }

    @Test
    void testUpdateHabitNotAfterCurrentPeriodEndBoundary() {
        finishHabitNumTimes(h4, 5);
        assertFalse(h4.updateHabit());
        assertEquals(5, h4.getNumSuccess());
        checkStats(h4, 1, 1, 5, 1, 0);
        assertTrue(h4.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.JUNE, 30, 23, 59), h4.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.JULY, 31, 23, 59), h4.getNextPeriodEnd());
    }

    @Test
    void testUpdateHabitAfterCurrentPeriodIsComplete() {
        finishHabitNumTimes(h3, 7);
        Clock clock = getFixedClock("2024-04-05T12:30:00.00Z");
        h3.setClock(clock);
        assertTrue(h3.updateHabit());
        assertEquals(0, h3.getNumSuccess());
        checkStats(h3, 1, 1, 7, 1, 1);
        assertFalse(h3.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.APRIL, 30, 23, 59), h3.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 31, 23, 59), h3.getNextPeriodEnd());
    }

    @Test
    void testUpdateHabitAfterCurrentPeriodIsNotCompleteLowerBoundary() {
        finishHabitNumTimes(h1, 2);
        Clock clock = getFixedClock("2024-02-18T00:00:00.00Z");
        h1.setClock(clock);
        assertTrue(h1.updateHabit());
        assertEquals(0, h1.getNumSuccess());
        checkStats(h1, 0, 0, 2, 0, 1);
        assertFalse(h1.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 24, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 2, 23, 59), h1.getNextPeriodEnd());
    }

    @Test
    void testUpdateHabitAfterCurrentPeriodIsNotCompleteUpperBoundary() {
        finishHabitNumTimes(h3, 6);
        Clock clock = getFixedClock("2024-04-30T23:59:59.999Z");
        h3.setClock(clock);
        assertTrue(h3.updateHabit());
        assertEquals(0, h3.getNumSuccess());
        checkStats(h3, 0, 0, 6, 0, 1);
        assertFalse(h3.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.APRIL, 30, 23, 59), h3.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 31, 23, 59), h3.getNextPeriodEnd());
    }

    @Test
    void testUpdateHabitAfterNextPeriod() {
        finishHabitNumTimes(h4, 5);
        Clock clock = getFixedClock("2025-05-30T20:59:00.00Z");
        h4.setClock(clock);
        assertTrue(h4.updateHabit());
        assertEquals(0, h4.getNumSuccess());
        checkStats(h4, 0, 1, 5, 1, 1);
        assertFalse(h4.isPreviousComplete());
        assertEquals(LocalDateTime.of(2025, Month.MAY, 31, 23, 59), h4.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 30, 23, 59), h4.getNextPeriodEnd());
    }

    @Test
    void testUpdateHabitAfterNextPeriodBoundary() {
        finishHabitNumTimes(h2, 15);
        Clock clock = getFixedClock("2024-05-04T00:00:00.00Z");
        h2.setClock(clock);
        assertTrue(h2.updateHabit());
        assertEquals(0, h2.getNumSuccess());
        checkStats(h2, 0, 1, 15, 1, 1);
        assertFalse(h2.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, 5, 4, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, 5, 5, 23, 59), h2.getNextPeriodEnd());
    }

    @Test
    void updateHabitAndNotifyEnabledPeriodNotComplete() {
        finishHabitNumTimes(h1, 2);
        h1.setClock(getFixedClock("2024-02-18T00:00:00.00Z"));
        assertTrue(h1.updateHabit());
        assertEquals(0, h1.getNumSuccess());
        checkStats(h1, 0, 0, 2, 0, 1);
        assertFalse(h1.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 24, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 2, 23, 59), h1.getNextPeriodEnd());
        testJobSize(h1.getHabitReminder(), 7);
    }

    @Test
    void updateHabitAndNotifyEnabledPeriodComplete() {
        h1.setNumSuccess(3);
        testJobSize(h1.getHabitReminder(), 1);
        assertFalse(h1.updateHabit());
        testJobSize(h1.getHabitReminder(), 0);
    }

    @Test
    void testUpdateDateTimeDaily() {
        Clock clock = getFixedClock("2024-05-20T06:45:00.00Z");
        h2.setClock(clock);
        h2.updateDateTime();
        assertEquals(LocalDateTime.of(2024, Month.MAY, 20, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 21, 23, 59), h2.getNextPeriodEnd());
    }

    @Test
    void testUpdateDateTimeWeekly() {
        Clock clock = getFixedClock("2024-12-16T17:00:00.00Z");
        h1.setClock(clock);
        h1.updateDateTime();
        assertEquals(LocalDateTime.of(2024, Month.DECEMBER, 21, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.DECEMBER, 28, 23, 59), h1.getNextPeriodEnd());
    }
    @Test
    void testUpdateDateTimeMonthly() {
        Clock clock = getFixedClock("2024-07-30T19:59:00.00Z");
        h4.setClock(clock);
        h4.updateDateTime();
        assertEquals(LocalDateTime.of(2024, Month.JULY, 31, 23, 59), h4.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.AUGUST, 31, 23, 59), h4.getNextPeriodEnd());
    }

    @Test
    void testUpdateDaily() {
        Clock clock = getFixedClock("2024-10-02T12:59:00.00Z");
        h2.setClock(clock);
        h2.updateDaily();
        assertEquals(LocalDateTime.of(2024, Month.OCTOBER, 2, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.OCTOBER, 3, 23, 59), h2.getNextPeriodEnd());
    }

    @Test
    void testUpdateDailyBoundary() {
        Clock lowerBoundary = getFixedClock("2024-09-05T23:59:59.999Z");
        h2.setClock(lowerBoundary);
        h2.updateDaily();
        assertEquals(LocalDateTime.of(2024, Month.SEPTEMBER, 5, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.SEPTEMBER, 6, 23, 59), h2.getNextPeriodEnd());

        Clock upperBoundary = getFixedClock("2025-04-15T00:00:00.00Z");
        h2.setClock(upperBoundary);
        h2.updateDaily();
        assertEquals(LocalDateTime.of(2025, Month.APRIL, 15, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2025, Month.APRIL, 16, 23, 59), h2.getNextPeriodEnd());
    }

    @Test
    void testUpdateWeekly() {
        Clock clock = getFixedClock("2024-03-13T17:00:00.00Z");
        h1.setClock(clock);
        h1.updateWeekly();
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 16, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 23, 23, 59), h1.getNextPeriodEnd());
    }

    @Test
    void testUpdateWeeklyBoundary() {
        Clock lowerBoundary = getFixedClock("2024-04-06T23:59:59.999Z");
        h1.setClock(lowerBoundary);
        h1.updateWeekly();
        assertEquals(LocalDateTime.of(2024, Month.APRIL, 6, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.APRIL, 13, 23, 59), h1.getNextPeriodEnd());

        Clock upperBoundary = getFixedClock("2024-06-30T00:00:00.00Z");
        h1.setClock(upperBoundary);
        h1.updateWeekly();
        assertEquals(LocalDateTime.of(2024, Month.JULY, 6, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.JULY, 13, 23, 59), h1.getNextPeriodEnd());
    }

    @Test
    void testUpdateMonthly() {
        Clock clock = getFixedClock("2024-08-15T10:30:00.00Z");
        h3.setClock(clock);
        h3.updateMonthly();
        assertEquals(LocalDateTime.of(2024, Month.AUGUST, 31, 23, 59), h3.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.SEPTEMBER, 30, 23, 59), h3.getNextPeriodEnd());
    }

    @Test
    void testUpdateMonthlyBoundary() {
        Clock lowerBoundary = getFixedClock("2024-02-29T23:59:59.999Z");
        h4.setClock(lowerBoundary);
        h4.updateMonthly();
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 29, 23, 59), h4.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 31, 23, 59), h4.getNextPeriodEnd());

        Clock upperBoundary = getFixedClock("2024-12-01T00:00:00.00Z");
        h4.setClock(upperBoundary);
        h4.updateMonthly();
        assertEquals(LocalDateTime.of(2024, Month.DECEMBER, 31, 23, 59), h4.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2025, Month.JANUARY, 31, 23, 59), h4.getNextPeriodEnd());
    }
}