package model.achievement;

import model.HabitStatistics;
import model.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static model.achievement.AchievementTier.*;
import static model.achievement.AchievementTier.GOLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AchievementManagerTest {
    private HabitStatistics noAchievements;
    private HabitStatistics someAchievements;
    private HabitStatistics allAchievements;

    @BeforeEach
    void runBefore() {
        noAchievements = new HabitStatistics(0, 0, 0, 0, 0);
        someAchievements = new HabitStatistics(0, 10, 20, 20, 20);
        allAchievements = new HabitStatistics(0, 2000, 4000, 2000, 2000);
    }

    @Test
    void testGetAchievedDaily() {
        List<Achievement> none = AchievementManager.getAchieved(noAchievements, Period.DAILY);
        assertEquals(new ArrayList<Achievement>(), none);
        assertTrue(none.isEmpty());
        assertEquals(AchievementManager.getAllAchievements(Period.DAILY),
                AchievementManager.getAchieved(allAchievements, Period.DAILY));
        assertEquals(getSomeDailyAchieved(),
                AchievementManager.getAchieved(someAchievements, Period.DAILY));
    }

    @Test
    void testGetAchievedWeekly() {
        List<Achievement> none = AchievementManager.getAchieved(noAchievements, Period.WEEKLY);
        assertEquals(new ArrayList<Achievement>(), none);
        assertTrue(none.isEmpty());
        assertEquals(AchievementManager.getAllAchievements(Period.WEEKLY),
                AchievementManager.getAchieved(allAchievements, Period.WEEKLY));
        assertEquals(getSomeWeeklyAchieved(),
                AchievementManager.getAchieved(someAchievements, Period.WEEKLY));
    }

    @Test
    void testGetAchievedMonthly() {
        List<Achievement> none = AchievementManager.getAchieved(noAchievements, Period.MONTHLY);
        assertEquals(new ArrayList<Achievement>(), none);
        assertTrue(none.isEmpty());
        assertEquals(AchievementManager.getAllAchievements(Period.MONTHLY),
                AchievementManager.getAchieved(allAchievements, Period.MONTHLY));
        assertEquals(getSomeMonthlyAchieved(),
                AchievementManager.getAchieved(someAchievements, Period.MONTHLY));
    }

    @Test
    void testGetNewlyAchievedDaily() {
        List<Achievement> current = getSomeDailyAchieved();
        List<Achievement> old = new ArrayList<>(current);
        List<Achievement> newAchieved = List.of(new Achievement("Streak Starter",
                        "Reach the habit target for 1 day", 1, AchievementType.STREAK, BRONZE),
                new Achievement("Twenty Twenty",
                        "Reach the habit target for 20 days", 20, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Quintuple Quest",
                        "Complete the habit for the fifth time", 5, AchievementType.SINGULAR_SUCCESSES, BRONZE));
        old.removeAll(newAchieved);
        assertEquals(new ArrayList<>(), AchievementManager.getNewlyAchieved(current, someAchievements, Period.DAILY));
        List<Achievement> newlyAchieved = AchievementManager.getNewlyAchieved(old, someAchievements, Period.DAILY);
        checkTwoListsEqualWithoutOrder(newAchieved, newlyAchieved);
    }

    @Test
    void testGetNewlyAchievedWeekly() {
        List<Achievement> current = getSomeWeeklyAchieved();
        List<Achievement> old = new ArrayList<>(current);
        List<Achievement> newAchieved = List.of(new Achievement("Double Completion",
                        "Complete the habit for the second time", 2, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("20's",
                        "Track the habit for 20 weeks", 20, AchievementType.PERIOD_COUNT, SILVER),
                new Achievement("Triple Threat",
                        "Reach the habit target for 3 weeks", 3, AchievementType.PERIODIC_SUCCESSES, BRONZE));
        old.removeAll(newAchieved);
        assertEquals(new ArrayList<>(), AchievementManager.getNewlyAchieved(current, someAchievements, Period.WEEKLY));
        List<Achievement> newlyAchieved = AchievementManager.getNewlyAchieved(old, someAchievements, Period.WEEKLY);
        checkTwoListsEqualWithoutOrder(newAchieved, newlyAchieved);
    }

    @Test
    void testGetNewlyAchievedMonthly() {
        List<Achievement> current = getSomeMonthlyAchieved();
        List<Achievement> old = new ArrayList<>(current);
        List<Achievement> newAchieved = List.of(new Achievement("Twenty Triumphs",
                "Complete the habit 20 times", 20, AchievementType.SINGULAR_SUCCESSES, SILVER),
                new Achievement("A Year of Tracking",
                        "Track the habit for 12 months", 12, AchievementType.PERIOD_COUNT, GOLD),
                new Achievement("Five for Five",
                        "Reach the habit target for 5 months in a row", 5, AchievementType.STREAK, SILVER));
        old.removeAll(newAchieved);
        assertEquals(new ArrayList<Achievement>(), AchievementManager.getNewlyAchieved(current, someAchievements, Period.MONTHLY));
        List<Achievement> newlyAchieved = AchievementManager.getNewlyAchieved(old, someAchievements, Period.MONTHLY);
        checkTwoListsEqualWithoutOrder(newAchieved, newlyAchieved);
    }

    private void checkTwoListsEqualWithoutOrder(List<Achievement> first, List<Achievement> second) {
        assertTrue(first.size() == second.size()
                && first.containsAll(second) && second.containsAll(first));
    }

    private List<Achievement> getSomeDailyAchieved() {
        return List.of(new Achievement("Streak Starter",
                "Reach the habit target for 1 day", 1, AchievementType.STREAK, AchievementTier.BRONZE),
                new Achievement("Double Trouble",
                        "Reach the habit target for 2 days in a row", 2, AchievementType.STREAK, AchievementTier.BRONZE),
                new Achievement("Oh Baby A Triple",
                        "Reach the habit target for 3 days in a row", 3, AchievementType.STREAK, AchievementTier.BRONZE),
                new Achievement("Streak Apprentice",
                        "Reach the habit target for 4 days in a row", 4, AchievementType.STREAK, BRONZE),
                new Achievement("Streak Challenger",
                        "Reach the habit target for 5 days in a row", 5, AchievementType.STREAK, BRONZE),
                new Achievement("Streak Master",
                        "Reach the habit target for 10 days in a row", 10, AchievementType.STREAK, BRONZE),
                new Achievement("First Time?",
                        "Complete the habit for the first time", 1, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Second Time's the Charm",
                        "Complete the habit for the second time", 2, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Triple Triumph",
                        "Complete the habit for the third time", 3, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Quadruple Quest",
                        "Complete the habit for the fourth time", 4, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Quintuple Quest",
                        "Complete the habit for the fifth time", 5, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Deca-Daily",
                        "Complete the habit 10 times", 10, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Fifteen Feats",
                        "Complete the habit 15 times", 15, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Twenty Triumphs",
                        "Complete the habit 20 times", 20, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("A New Beginning",
                        "Reach the habit target for the first time", 1, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Double Down",
                        "Reach the habit target for 2 days", 2, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Triple Threat",
                        "Reach the habit target for 3 days", 3, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Habit Quarter",
                        "Reach the habit target for 4 days", 4, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("The Quintessential Habit",
                        "Reach the habit target for 5 days", 5, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Deca-days",
                        "Reach the habit target for 10 days", 10, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Twenty Twenty",
                        "Reach the habit target for 20 days", 20, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Habit Born",
                        "Track the habit for 1 day", 1, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Toddler",
                        "Track the habit for 2 days", 2, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Child",
                        "Track the habit for 3 days", 3, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Teen",
                        "Track the habit for 4 days", 4, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Adult",
                        "Track the habit for 5 days", 5, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Exists 10?",
                        "Track the habit for 10 days", 10, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("20's",
                        "Track the habit for 20 days", 20, AchievementType.PERIOD_COUNT, BRONZE));
    }

    private List<Achievement> getSomeWeeklyAchieved() {
        return List.of(new Achievement("Weekly Streak Initiator",
                        "Reach the habit target for 1 week", 1, AchievementType.STREAK, BRONZE),
                new Achievement("Weekly Streaker",
                        "Reach the habit target for 2 weeks in a row", 2, AchievementType.STREAK, BRONZE),
                new Achievement("Streak Thrice",
                        "Reach the habit target for 3 weeks in a row", 3, AchievementType.STREAK, BRONZE),
                new Achievement("A Month of Streaks",
                        "Reach the habit target for 4 weeks in a row", 4, AchievementType.STREAK, SILVER),
                new Achievement("Five for Five",
                        "Reach the habit target for 5 weeks in a row", 5, AchievementType.STREAK, SILVER),
                new Achievement("Streak Deca",
                        "Reach the habit target for 10 weeks in a row", 10, AchievementType.STREAK, GOLD),
                new Achievement("Weekly First Time?",
                        "Complete the habit for the first time", 1, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Double Completion",
                        "Complete the habit for the second time", 2, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Triple Completion",
                        "Complete the habit for the third time", 3, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Quadruple Completion",
                        "Complete the habit for the fourth time", 4, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Quintuple Completion",
                        "Complete the habit for the fifth time", 5, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Deca-Weekly",
                        "Complete the habit 10 times", 10, AchievementType.SINGULAR_SUCCESSES, SILVER),
                new Achievement("Fifteen Feats",
                        "Complete the habit 15 times", 15, AchievementType.SINGULAR_SUCCESSES, SILVER),
                new Achievement("Twenty Triumphs",
                        "Complete the habit 20 times", 20, AchievementType.SINGULAR_SUCCESSES, SILVER),
                new Achievement("A New Weekly Beginning",
                        "Reach the habit target for the first time", 1, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Double Down",
                        "Reach the habit target for 2 weeks", 2, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Triple Threat",
                        "Reach the habit target for 3 weeks", 3, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Habit Quarter",
                        "Reach the habit target for 4 weeks", 4, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("The Quintessential Habit",
                        "Reach the habit target for 5 weeks", 5, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Deca-Weeks",
                        "Reach the habit target for 10 weeks", 10, AchievementType.PERIODIC_SUCCESSES, SILVER),
                new Achievement("Twenty Twenty",
                        "Reach the habit target for 20 weeks", 20, AchievementType.PERIODIC_SUCCESSES, SILVER),
                new Achievement("Habit Born",
                        "Track the habit for 1 week", 1, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Toddler",
                        "Track the habit for 2 weeks", 2, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Child",
                        "Track the habit for 3 weeks", 3, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Teen",
                        "Track the habit for 4 weeks", 4, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Adult",
                        "Track the habit for 5 weeks", 5, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Exists 10?",
                        "Track the habit for 10 weeks", 10, AchievementType.PERIOD_COUNT, SILVER),
                new Achievement("20's",
                        "Track the habit for 20 weeks", 20, AchievementType.PERIOD_COUNT, SILVER));
    }

    private List<Achievement> getSomeMonthlyAchieved() {
        return List.of(new Achievement("Monthly Streak Initiator",
                        "Reach the habit target for 1 month", 1, AchievementType.STREAK, BRONZE),
                new Achievement("Monthly Streaker",
                        "Reach the habit target for 2 months in a row", 2, AchievementType.STREAK, BRONZE),
                new Achievement("Streak Thrice",
                        "Reach the habit target for 3 months in a row", 3, AchievementType.STREAK, BRONZE),
                new Achievement("A Quarter of Streaks",
                        "Reach the habit target for 4 months in a row", 4, AchievementType.STREAK, SILVER),
                new Achievement("Five for Five",
                        "Reach the habit target for 5 months in a row", 5, AchievementType.STREAK, SILVER),
                new Achievement("Monthly First Time?",
                        "Complete the habit for the first time", 1, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Double Completion",
                        "Complete the habit for the second time", 2, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Triple Completion",
                        "Complete the habit for the third time", 3, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Quadruple Completion",
                        "Complete the habit for the fourth time", 4, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Quintuple Completion",
                        "Complete the habit for the fifth time", 5, AchievementType.SINGULAR_SUCCESSES, BRONZE),
                new Achievement("Deca-Monthly",
                        "Complete the habit 10 times", 10, AchievementType.SINGULAR_SUCCESSES, SILVER),
                new Achievement("Fifteen Feats",
                        "Complete the habit 15 times", 15, AchievementType.SINGULAR_SUCCESSES, SILVER),
                new Achievement("Twenty Triumphs",
                        "Complete the habit 20 times", 20, AchievementType.SINGULAR_SUCCESSES, SILVER),
                new Achievement("A New Monthly Beginning",
                        "Reach the habit target for the first time", 1, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Double Down",
                        "Reach the habit target for 2 months", 2, AchievementType.PERIODIC_SUCCESSES, BRONZE),
                new Achievement("Triple Threat",
                        "Reach the habit target for 3 months", 3, AchievementType.PERIODIC_SUCCESSES, SILVER),
                new Achievement("Habit Quarter",
                        "Reach the habit target for 4 months", 4, AchievementType.PERIODIC_SUCCESSES, SILVER),
                new Achievement("The Quintessential Habit",
                        "Reach the habit target for 5 months", 5, AchievementType.PERIODIC_SUCCESSES, GOLD),
                new Achievement("Yearly Habit",
                        "Reach the habit target for 12 months", 12, AchievementType.PERIODIC_SUCCESSES, GOLD),
                new Achievement("Habit Born",
                        "Track the habit for 1 month", 1, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Habit Toddler",
                        "Track the habit for 2 months", 2, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Triple Track",
                        "Track the habit for 3 months", 3, AchievementType.PERIOD_COUNT, BRONZE),
                new Achievement("Tracker Quarter",
                        "Track the habit for 4 months", 4, AchievementType.PERIOD_COUNT, SILVER),
                new Achievement("The Quintessential Tracker",
                        "Track the habit for 5 months", 5, AchievementType.PERIOD_COUNT, SILVER),
                new Achievement("A Year of Tracking",
                        "Track the habit for 12 months", 12, AchievementType.PERIOD_COUNT, GOLD));
    }
}
