package model;

import model.reminder.HabitReminder;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// A helper class for testing
public class HabitHelperTest {
    protected Clock getFixedClock(String parse) {
        return Clock.fixed(Instant.parse(parse), ZoneId.of("Z"));
    }

    protected void testJobSize(HabitReminder hr, int size) {
        if (hr == null) {
            fail();
        }
        try {
            assertEquals(size, hr.getReminderScheduler().getScheduler()
                    .getJobKeys(GroupMatcher
                            .groupEquals(hr.getHabit().getId().toString()))
                    .size());
        } catch (SchedulerException e) {
            fail();
        }
    }

    protected void testCorrectDistribution(HabitReminder hr, Set<LocalDateTime> reminders) {
        for (LocalDateTime reminder : reminders) {
            assertTrue(hr.getReminders().contains(reminder));
        }
        assertEquals(reminders.size(), hr.getReminders().size());
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

    protected boolean finishHabitNumTimes(Habit habit, int num) {
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
}
