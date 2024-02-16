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

    @BeforeEach
    void runBefore() {
        c1 = getFixedClock("2024-02-16T17:00:00.00Z");
        c2 = getFixedClock("2024-05-02T23:59:00.00Z");
        h1 = new Habit("name", "description", Period.WEEKLY, 3, c1);
        h2 = new Habit("another name", "another description", Period.DAILY, 15, c2);
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
        assertEquals(1, h1.getNumPeriod());
        assertEquals(LocalDateTime.of(2024, Month.FEBRUARY, 24, 23, 59), h1.getCurrentPeriodEnd());
        assertEquals(LocalDateTime.of(2024, Month.MARCH, 2, 23, 59), h1.getNextPeriodEnd());
    }

    @Test
    void testNextPeriodAfterNextPeriod() {
        Clock afterNextWeek = getFixedClock("2024-04-01T00:50:00.00Z");
        h1.setClock(afterNextWeek);
        h1.nextHabitPeriod();
        assertEquals(0, h1.getNumSuccess());
        assertEquals(1, h1.getNumPeriod());
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
        assertEquals(0, habit.getStreak());
        assertEquals(0, habit.getBestStreak());
        assertEquals(0, habit.getTotalNumSuccess());
        assertEquals(0, habit.getNumPeriodSuccess());
        assertEquals(0, habit.getNumPeriod());
    }

    private void checkStats(Habit habit, int streak, int bestStreak, int totalNumSuccess, int numPeriodSuccess, int numPeriod) {
        assertEquals(streak, habit.getStreak());
        assertEquals(bestStreak, habit.getBestStreak());
        assertEquals(totalNumSuccess, habit.getTotalNumSuccess());
        assertEquals(numPeriodSuccess, habit.getNumPeriodSuccess());
        assertEquals(numPeriod, habit.getNumPeriod());
    }
}