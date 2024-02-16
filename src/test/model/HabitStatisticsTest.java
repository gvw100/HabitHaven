package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;

// test class for HabitStatistics
public class HabitStatisticsTest {
    private HabitStatistics habitStats;

    @BeforeEach
    void runBefore() {
        habitStats = new Habit("name", "description", Period.WEEKLY, 9, Clock.systemDefaultZone());
    }

    @Test
    void testConstructor() {
        checkStats(habitStats, 0, 0, 0, 0, 0);
    }

    @Test
    void testGetters() {
        for (int i = 0; i < 2; i++) {
            habitStats.incrementStreak();
        }
        habitStats.resetStreak();
        habitStats.incrementStreak();
        for (int i = 0; i < 3; i++) {
            habitStats.incrementTotalNumSuccess();
        }
        for (int i = 0; i < 4; i++) {
            habitStats.incrementNumPeriodSuccess();
        }
        for (int i = 0; i < 5; i++) {
            habitStats.incrementNumPeriod();
        }
        checkStats(habitStats, 1, 2, 3, 4, 5);
    }

    @Test
    void testGetSuccessRateNonzeroPeriodAndPeriodIsComplete() {
        for (int i = 0; i < 2; i++) {
            habitStats.incrementNumPeriod();
        }
        for (int i = 0; i < 2; i++) {
            habitStats.incrementNumPeriodSuccess();
        }
        assertEquals(67, habitStats.getSuccessRate(true));
    }

    @Test
    void testGetSuccessRateNonzeroPeriodAndPeriodIsNotComplete() {
        for (int i = 0; i < 7; i++) {
            habitStats.incrementNumPeriod();
        }
        for (int i = 0; i < 2; i++) {
            habitStats.incrementNumPeriodSuccess();
        }
        assertEquals(29, habitStats.getSuccessRate(false));
    }

    @Test
    void testGetSuccessRateZeroPeriod() {
        assertEquals(0, habitStats.getSuccessRate(true));
        assertEquals(0, habitStats.getSuccessRate(false));
        habitStats.incrementNumPeriodSuccess();
        assertEquals(100, habitStats.getSuccessRate(true));
        assertEquals(100, habitStats.getSuccessRate(false));
    }

    @Test
    void testResetStreak() {
        for (int i = 0; i < 5; i ++) {
            habitStats.incrementStreak();
        }
        habitStats.incrementTotalNumSuccess();
        habitStats.incrementNumPeriodSuccess();
        habitStats.incrementNumPeriod();
        checkStats(habitStats, 5, 5, 1, 1, 1);
        habitStats.resetStreak();
        checkStats(habitStats, 0, 5, 1, 1, 1);
    }

    @Test
    void testResetStats() {
        habitStats.incrementStreak();
        habitStats.incrementTotalNumSuccess();
        habitStats.incrementNumPeriodSuccess();
        habitStats.incrementNumPeriod();
        checkStats(habitStats, 1, 1, 1, 1, 1);
        habitStats.resetStats();
        checkStats(habitStats, 0, 0, 0, 0, 0);
    }

    @Test
    void testIncrementStreak() {
        for (int i = 0; i < 5; i++) {
            habitStats.incrementStreak();
        }
        checkStats(habitStats, 5, 5, 0, 0, 0);
        habitStats.resetStreak();
        for (int i = 0; i < 4; i++) {
            habitStats.incrementStreak();
        }
        checkStats(habitStats, 4, 5, 0, 0, 0);
        habitStats.incrementStreak();
        checkStats(habitStats, 5, 5, 0, 0, 0);
        habitStats.incrementStreak();
        checkStats(habitStats, 6, 6, 0, 0, 0);
    }

    @Test
    void testIncrement() {
        for (int i = 0; i < 7; i++) {
            habitStats.incrementTotalNumSuccess();
        }
        checkStats(habitStats, 0, 0, 7, 0, 0);
        for (int i = 0; i < 6; i++) {
            habitStats.incrementNumPeriodSuccess();
        }
        checkStats(habitStats, 0, 0, 7, 6, 0);
        for (int i = 0; i < 11; i++) {
            habitStats.incrementNumPeriod();
        }
        checkStats(habitStats, 0, 0, 7, 6, 11);
    }

    private void checkStats(HabitStatistics stat, int streak, int bestStreak, int totalNumSuccess, int numPeriodSuccess, int numPeriod) {
        assertEquals(streak, stat.getStreak());
        assertEquals(bestStreak, stat.getBestStreak());
        assertEquals(totalNumSuccess, stat.getTotalNumSuccess());
        assertEquals(numPeriodSuccess, stat.getNumPeriodSuccess());
        assertEquals(numPeriod, stat.getNumPeriod());
    }
}