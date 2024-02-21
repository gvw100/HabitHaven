package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

// test class for Habit
public class HabitTest {
    private Clock c1;
    private Clock c2;

    private Habit h1;
    private Habit h2;
    private Habit h3;
    private Habit h4;

    @BeforeEach
    void runBefore() {
        c1 = getFixedClock("2024-02-16T17:00:00.00Z");
        c2 = getFixedClock("2024-05-02T23:59:00.00Z");
        Clock c3 = getFixedClock("2024-03-15T10:30:00.00Z");
        Clock c4 = getFixedClock("2024-06-30T23:59:00.00Z");

        h1 = new Habit("name", "description", Period.WEEKLY, 3, c1);
        h2 = new Habit("another name", "another description", Period.DAILY, 15, c2);
        h3 = new Habit("and another name", "yet another description", Period.MONTHLY, 7, c3);
        h4 = new Habit("please no more habits", "and no more descriptions", Period.MONTHLY, 5, c4);
    }

    @Test
    void testConstructor1() {
        assertEquals("name", h1.getName());
        assertEquals("description", h1.getDescription());
        assertEquals(Period.WEEKLY, h1.getPeriod());
        assertEquals(3, h1.getFrequency());
        assertEquals(0, h1.getNumSuccess());
        assertEquals(c1, h1.getClock());
        assertFalse(h1.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 17, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 24, 23, 59), h1.getNextPeriodEnd());
        checkResetStats(h1);
    }

    @Test
    void testConstructor2() {
        assertEquals("another name", h2.getName());
        assertEquals("another description", h2.getDescription());
        assertEquals(Period.DAILY, h2.getPeriod());
        assertEquals(15, h2.getFrequency());
        assertEquals(0, h2.getNumSuccess());
        assertEquals(c2, h2.getClock());
        assertFalse(h1.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 2, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MAY, 3, 23, 59), h2.getNextPeriodEnd());
        checkResetStats(h2);
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
    void testSetFrequencyDifferentFromBefore() {
        finishHabitNumTimes(h1,3);
        assertEquals(3, h1.getNumSuccess());
        h1.setFrequency(7);
        assertEquals(7, h1.getFrequency());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
    }

    @Test
    void testSetFrequencySameAsBefore() {
        h1.finishHabit();
        assertEquals(1, h1.getNumSuccess());
        h1.setFrequency(5);
        assertEquals(5, h1.getFrequency());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
        finishHabitNumTimes(h1,5);
        h1.setFrequency(5);
        assertEquals(5, h1.getFrequency());
        assertEquals(5, h1.getNumSuccess());
        checkStats(h1, 1, 1, 5, 1, 0);
    }

    @Test
    void testSetPeriodDifferentFromBefore() {
        finishHabitNumTimes(h1, 3);
        assertEquals(3, h1.getNumSuccess());
        h1.setPeriod(Period.DAILY);
        assertEquals(Period.DAILY, h1.getPeriod());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
    }

    @Test
    void testSetPeriodSameAsBefore() {
        h1.finishHabit();
        assertEquals(1, h1.getNumSuccess());
        h1.setPeriod(Period.DAILY);
        assertEquals(Period.DAILY, h1.getPeriod());
        assertEquals(0, h1.getNumSuccess());
        checkResetStats(h1);
        finishHabitNumTimes(h1,3);
        h1.setPeriod(Period.DAILY);
        assertEquals(Period.DAILY, h1.getPeriod());
        assertEquals(3, h1.getNumSuccess());
        checkStats(h1, 1, 1, 3, 1, 0);
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
    void testGetters() {
        finishHabitNumTimes(h2, 15);
        assertEquals("another name", h2.getName());
        assertEquals("another description", h2.getDescription());
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
        assertTrue(finishHabitNumTimes(h2, 9));
        assertEquals(9, h2.getNumSuccess());
        checkStats(h2, 0, 0, 9, 0, 0);
    }

    @Test
    void testFinishHabitNumSuccessSameAsFrequency() {
        assertTrue(finishHabitNumTimes(h2, 15));
        assertEquals(15, h2.getNumSuccess());
        checkStats(h2, 1, 1, 15, 1, 0);
    }

    @Test
    void testFinishHabitNumSuccessGreaterThanFrequency() {
        assertFalse(finishHabitNumTimes(h1, 10));
        assertEquals(3, h1.getNumSuccess());
        checkStats(h1, 1, 1, 3, 1, 0);
        assertFalse(finishHabitNumTimes(h2, 16));
        assertEquals(15, h2.getNumSuccess());
        checkStats(h2, 1, 1, 15, 1, 0);
    }

    @Test
    void testFinishHabitNextPeriodButPeriodNotComplete() {
        assertTrue(finishHabitNumTimes(h2, 10));
        assertEquals(10, h2.getNumSuccess());
        checkStats(h2, 0, 0, 10, 0, 0);
        Clock tomorrow = getFixedClock("2024-05-03T23:59:00.00Z");
        h2.setClock(tomorrow);
        assertTrue(finishHabitNumTimes(h2, 3));
        checkStats(h2, 0, 0, 13, 0, 1);
        assertEquals(3, h2.getNumSuccess());
    }

    @Test
    void testFinishHabitNextPeriodAndPeriodIsComplete() {
        assertFalse(finishHabitNumTimes(h1, 4));
        assertEquals(3, h1.getNumSuccess());
        checkStats(h1, 1, 1, 3, 1, 0);
        Clock nextWeek = getFixedClock("2024-02-23T17:00:00.00Z");
        h1.setClock(nextWeek);
        assertTrue(h1.finishHabit());
        assertEquals(1, h1.getNumSuccess());
        checkStats(h1, 1, 1, 4, 1, 1);
        assertTrue(finishHabitNumTimes(h1,2));
        assertEquals(3, h1.getNumSuccess());
        checkStats(h1, 2, 2, 6, 2, 1);
    }

    @Test
    void testFinishHabitAfterNextPeriodEnd() {
        assertFalse(finishHabitNumTimes(h2, 16));
        assertEquals(15, h2.getNumSuccess());
        checkStats(h2, 1, 1, 15, 1, 0);
        Clock afterTomorrow = getFixedClock("2024-05-04T00:00:00.00Z");
        h2.setClock(afterTomorrow);
        assertTrue(h2.finishHabit());
        assertEquals(1, h2.getNumSuccess());
        checkStats(h2, 0, 1, 16, 1, 1);
    }

    @Test
    void testCheckPeriodNotYetCompleted() {
        h1.checkPeriodComplete();
        assertFalse(h1.isPreviousComplete());
        checkStats(h1, 0, 0, 0, 0, 0);
        h1.setNumSuccess(2);
        h1.checkPeriodComplete();
        assertFalse(h1.isPreviousComplete());
        checkStats(h1, 0, 0, 0, 0, 0);
    }

    @Test
    void testCheckPeriodCompleted() {
        h1.checkPeriodComplete();
        assertFalse(h1.isPreviousComplete());
        checkStats(h1, 0, 0, 0, 0, 0);
        h1.setNumSuccess(3);
        h1.checkPeriodComplete();
        assertTrue(h1.isPreviousComplete());
        checkStats(h1, 1, 1, 0, 1, 0);
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
        finishHabitNumTimes(h2, 15);
        assertEquals(15, h2.getNumSuccess());
        checkStats(h2, 1, 1, 15, 1, 0);
        h2.resetProgress();
        assertEquals(0, h2.getNumSuccess());
        checkResetStats(h2);
    }

    @Test
    void testUpdateHabitNotAfterCurrentPeriodEnd() {
        finishHabitNumTimes(h3, 7);
        h3.updateHabit();
        assertEquals(7, h3.getNumSuccess());
        checkStats(h3, 1, 1, 7, 1, 0);
        assertTrue(h3.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 31, 23, 59), h3.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.APRIL, 30, 23, 59), h3.getNextPeriodEnd());
    }

    @Test
    void testUpdateHabitNotAfterCurrentPeriodEndBoundary() {
        finishHabitNumTimes(h4, 5);
        h4.updateHabit();
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
        h3.updateHabit();
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
        h1.updateHabit();
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
        h3.updateHabit();
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
        h4.updateHabit();
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
        h2.updateHabit();
        assertEquals(0, h2.getNumSuccess());
        checkStats(h2, 0, 1, 15, 1, 1);
        assertFalse(h2.isPreviousComplete());
        assertEquals(LocalDateTime.of(2024, 5, 4, 23, 59), h2.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, 5, 5, 23, 59), h2.getNextPeriodEnd());
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

    private Clock getFixedClock(String parse) {
        return Clock.fixed(Instant.parse(parse), ZoneId.of("Z"));
    }

    private boolean finishHabitNumTimes(Habit habit, int num) {
        boolean isIncremented = true;
        for (int i = 0; i < num; i++) {
            if (i == num - 1) {
                isIncremented = habit.finishHabit();
            } else {
                habit.finishHabit();
            }
        }
        return isIncremented;
    }

    private void checkResetStats(Habit habit) {
        assertEquals(0, habit.getHabitStats().getStreak());
        assertEquals(0, habit.getHabitStats().getBestStreak());
        assertEquals(0, habit.getHabitStats().getTotalNumSuccess());
        assertEquals(0, habit.getHabitStats().getNumPeriodSuccess());
        assertEquals(0, habit.getHabitStats().getNumPeriod());
    }

    private void checkStats(Habit habit, int streak, int bestStreak, int totalNumSuccess, int numPeriodSuccess, int numPeriod) {
        assertEquals(streak, habit.getHabitStats().getStreak());
        assertEquals(bestStreak, habit.getHabitStats().getBestStreak());
        assertEquals(totalNumSuccess, habit.getHabitStats().getTotalNumSuccess());
        assertEquals(numPeriodSuccess, habit.getHabitStats().getNumPeriodSuccess());
        assertEquals(numPeriod, habit.getHabitStats().getNumPeriod());
    }
}