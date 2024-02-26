package model;

import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Represents a helper class for testing
public class HabitHelperTest {
    protected Clock getFixedClock(String parse) {
        return Clock.fixed(Instant.parse(parse), ZoneId.of("Z"));
    }

    protected void testJobSize(HabitReminder hr, int size) {
        if (hr == null) {
            fail();
        }
        try {
            assertEquals(size, hr.reminderScheduler.getScheduler()
                    .getJobKeys(GroupMatcher
                            .groupEquals(hr.habit.getId().toString()))
                    .size());
        } catch (SchedulerException e) {
            fail();
        }
    }

    protected void testCorrectDistribution(DailyReminder dr, Set<LocalDateTime> reminders) {
        for (LocalDateTime reminder : reminders) {
            assertTrue(dr.reminders.contains(reminder));
        }
        assertEquals(reminders.size(), dr.reminders.size());
    }

    protected void checkStats(HabitStatistics stat, int streak, int bestStreak, int totalNumSuccess, int numPeriodSuccess, int numPeriod) {
        assertEquals(streak, stat.getStreak());
        assertEquals(bestStreak, stat.getBestStreak());
        assertEquals(totalNumSuccess, stat.getTotalNumSuccess());
        assertEquals(numPeriodSuccess, stat.getNumPeriodSuccess());
        assertEquals(numPeriod, stat.getNumPeriod());
    }

    protected void checkStats(Habit habit, int streak, int bestStreak, int totalNumSuccess, int numPeriodSuccess, int numPeriod) {
        assertEquals(streak, habit.getHabitStats().getStreak());
        assertEquals(bestStreak, habit.getHabitStats().getBestStreak());
        assertEquals(totalNumSuccess, habit.getHabitStats().getTotalNumSuccess());
        assertEquals(numPeriodSuccess, habit.getHabitStats().getNumPeriodSuccess());
        assertEquals(numPeriod, habit.getHabitStats().getNumPeriod());
    }

    protected void checkResetStats(Habit habit) {
        assertEquals(0, habit.getHabitStats().getStreak());
        assertEquals(0, habit.getHabitStats().getBestStreak());
        assertEquals(0, habit.getHabitStats().getTotalNumSuccess());
        assertEquals(0, habit.getHabitStats().getNumPeriodSuccess());
        assertEquals(0, habit.getHabitStats().getNumPeriod());
    }
}
