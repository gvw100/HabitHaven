package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// A test class for HabitStatistics
public class HabitStatisticsTest extends HabitHelperTest {
    private HabitStatistics habitStats;

    @BeforeEach
    void runBefore() {
        habitStats = new HabitStatistics();
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
    void testDecrementStreak() {
        for (int i = 0; i < 5; i++) {
            habitStats.incrementStreak();
        }
        checkStats(habitStats, 5, 5, 0, 0, 0);
        habitStats.decrementStreak();
        checkStats(habitStats, 4, 4, 0, 0, 0);
        habitStats.decrementStreak();
        checkStats(habitStats, 3, 3, 0, 0, 0);
        habitStats.decrementStreak();
        checkStats(habitStats, 2, 2, 0, 0, 0);
        habitStats.decrementStreak();
        checkStats(habitStats, 1, 1, 0, 0, 0);
        habitStats.decrementStreak();
        checkStats(habitStats, 0, 0, 0, 0, 0);
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

    @Test
    void testDecrement() {
        for (int i = 0; i < 4; i++) {
            habitStats.incrementTotalNumSuccess();
        }
        for (int i = 0; i < 20; i++) {
            habitStats.incrementNumPeriodSuccess();
        }
        for (int i = 0; i < 9; i++) {
            habitStats.incrementNumPeriod();
        }
        checkStats(habitStats, 0, 0, 4, 20, 9);
        habitStats.decrementTotalNumSuccess();
        checkStats(habitStats, 0, 0, 3, 20, 9);
        habitStats.decrementNumPeriodSuccess();
        checkStats(habitStats, 0, 0, 3, 19, 9);
    }
}