package model.achievement;

import java.util.List;

import static model.achievement.AchievementTier.*;
import static model.achievement.AchievementType.*;

// Represents lists of all possible achievements
public class AchievementList {
    protected static final List<Achievement> DAILY_ACHIEVEMENTS = List.of(
            new Achievement("Streak Starter",
                    "Reach the habit target for 1 day", 1, STREAK, BRONZE),
            new Achievement("Double Trouble",
                    "Reach the habit target for 2 days in a row", 2, STREAK, BRONZE),
            new Achievement("Oh Baby A Triple",
                    "Reach the habit target for 3 days in a row", 3, STREAK, BRONZE),
            new Achievement("Streak Apprentice",
                    "Reach the habit target for 4 days in a row", 4, STREAK, BRONZE),
            new Achievement("Streak Challenger",
                    "Reach the habit target for 5 days in a row", 5, STREAK, BRONZE),
            new Achievement("Streak Master",
                    "Reach the habit target for 10 days in a row", 10, STREAK, BRONZE),
            new Achievement("Streak Veteran",
                    "Reach the habit target for 15 days in a row", 15, STREAK, BRONZE),
            new Achievement("Streak Warrior",
                    "Reach the habit target for 20 days in a row", 20, STREAK, BRONZE),
            new Achievement("Monthly Maestro",
                    "Reach the habit target for 30 days in a row", 30, STREAK, BRONZE),
            new Achievement("Streak Virtuoso",
                    "Reach the habit target for 35 days in a row", 35, STREAK, SILVER),
            new Achievement("Streak Hero",
                    "Reach the habit target for 40 days in a row", 40, STREAK, SILVER),
            new Achievement("Streak Elite",
                    "Reach the habit target for 45 days in a row", 45, STREAK, SILVER),
            new Achievement("Streak Grandmaster",
                    "Reach the habit target for 50 days in a row", 50, STREAK, SILVER),
            new Achievement("Streak Legend",
                    "Reach the habit target for 60 days in a row", 60, STREAK, SILVER),
            new Achievement("Streak Champion",
                    "Reach the habit target for 70 days in a row", 70, STREAK, SILVER),
            new Achievement("Streak Superstar",
                    "Reach the habit target for 80 days in a row", 80, STREAK, SILVER),
            new Achievement("Streak Emperor",
                    "Reach the habit target for 90 days in a row", 90, STREAK, SILVER),
            new Achievement("Streak God",
                    "Reach the habit target for 100 days in a row", 100, STREAK, GOLD),
            new Achievement("Streak Wizard",
                    "Reach the habit target for 120 days in a row", 120, STREAK, GOLD),
            new Achievement("Streak Overlord",
                    "Reach the habit target for 140 days in a row", 140, STREAK, GOLD),
            new Achievement("Streak Titan",
                    "Reach the habit target for 160 days in a row", 160, STREAK, GOLD),
            new Achievement("Streak Sovereign",
                    "Reach the habit target for 180 days in a row", 180, STREAK, GOLD),
            new Achievement("Streak Star",
                    "Reach the habit target for 200 days in a row", 200, STREAK, GOLD),
            new Achievement("Streak Deity",
                    "Reach the habit target for 250 days in a row", 250, STREAK, GOLD),
            new Achievement("Streak Yoda",
                    "Reach the habit target for 300 days in a row", 300, STREAK, GOLD),
            new Achievement("Yearly Ace",
                    "Reach the habit target for 365 days in a row", 365, STREAK, PLATINUM),
            new Achievement("First Time?",
                    "Complete the habit for the first time", 1, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Second Time's the Charm",
                    "Complete the habit for the second time", 2, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Triple Triumph",
                    "Complete the habit for the third time", 3, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Quadruple Quest",
                    "Complete the habit for the fourth time", 4, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Quintuple Quest",
                    "Complete the habit for the fifth time", 5, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Deca-Daily",
                    "Complete the habit 10 times", 10, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Fifteen Feats",
                    "Complete the habit 15 times", 15, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Twenty Triumphs",
                    "Complete the habit 20 times", 20, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Three Zero",
                    "Complete the habit 30 times", 30, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Quadruple Zero",
                    "Complete the habit 40 times", 40, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Fifty Feats",
                    "Complete the habit 50 times", 50, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Century Club",
                    "Complete the habit 100 times", 100, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Double Century",
                    "Complete the habit 200 times", 200, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Triple Century",
                    "Complete the habit 300 times", 300, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Quadruple Century",
                    "Complete the habit 400 times", 400, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Quintuple Century",
                    "Complete the habit 500 times", 500, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Six Centuries?!?",
                    "Complete the habit 600 times", 600, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Eighth Wonder",
                    "Complete the habit 800 times", 800, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Millennium Master",
                    "Complete the habit 1000 times", 1000, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Double Millennium",
                    "Complete the habit 2000 times", 2000, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Completionist",
                    "Complete the habit 4000 times", 4000, SINGULAR_SUCCESSES, PLATINUM),
            new Achievement("A New Beginning",
                    "Reach the habit target for the first time", 1, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Double Down",
                    "Reach the habit target for 2 days", 2, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Triple Threat",
                    "Reach the habit target for 3 days", 3, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Habit Quarter",
                    "Reach the habit target for 4 days", 4, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("The Quintessential Habit",
                    "Reach the habit target for 5 days", 5, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Deca-days",
                    "Reach the habit target for 10 days", 10, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Twenty Twenty",
                    "Reach the habit target for 20 days", 20, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Thirty Thirty",
                    "Reach the habit target for 30 days", 30, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Forty Forty",
                    "Reach the habit target for 40 days", 40, PERIODIC_SUCCESSES, SILVER),
            new Achievement("Fifty Fifty",
                    "Reach the habit target for 50 days", 50, PERIODIC_SUCCESSES, SILVER),
            new Achievement("Century Habit",
                    "Reach the habit target for 100 days", 100, PERIODIC_SUCCESSES, SILVER),
            new Achievement("Double Century Habit",
                    "Reach the habit target for 200 days", 200, PERIODIC_SUCCESSES, GOLD),
            new Achievement("A Year of Habits",
                    "Reach the habit target for 365 days", 365, PERIODIC_SUCCESSES, GOLD),
            new Achievement("Habit Half a Millennium",
                    "Reach the habit target for 500 days", 500, PERIODIC_SUCCESSES, GOLD),
            new Achievement("Habit Millennium",
                    "Reach the habit target for 1000 days", 1000, PERIODIC_SUCCESSES, PLATINUM),
            new Achievement("Habit Born",
                    "Track the habit for 1 day", 1, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Toddler",
                    "Track the habit for 2 days", 2, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Child",
                    "Track the habit for 3 days", 3, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Teen",
                    "Track the habit for 4 days", 4, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Adult",
                    "Track the habit for 5 days", 5, PERIOD_COUNT, BRONZE),
            new Achievement("Exists 10?",
                    "Track the habit for 10 days", 10, PERIOD_COUNT, BRONZE),
            new Achievement("20's",
                    "Track the habit for 20 days", 20, PERIOD_COUNT, BRONZE),
            new Achievement("30's",
                    "Track the habit for 30 days", 30, PERIOD_COUNT, BRONZE),
            new Achievement("40's",
                    "Track the habit for 40 days", 40, PERIOD_COUNT, BRONZE),
            new Achievement("50's",
                    "Track the habit for 50 days", 50, PERIOD_COUNT, BRONZE),
            new Achievement("Tracker Perfect",
                    "Track the habit for 100 days", 100, PERIOD_COUNT, SILVER),
            new Achievement("Tracker Heaven",
                    "Track the habit for 250 days", 250, PERIOD_COUNT, SILVER),
            new Achievement("Year-Old Habit",
                    "Track the habit for 365 days", 365, PERIOD_COUNT, SILVER),
            new Achievement("Tracker God",
                    "Track the habit for 500 days", 500, PERIOD_COUNT, GOLD),
            new Achievement("Millennial Habit",
                    "Track the habit for 1000 days", 1000, PERIOD_COUNT, GOLD),
            new Achievement("Tracker 2000",
                    "Track the habit for 2000 days", 2000, PERIOD_COUNT, PLATINUM)
    );
    protected static final List<Achievement> WEEKLY_ACHIEVEMENTS = List.of(
            new Achievement("Weekly Streak Initiator",
                    "Reach the habit target for 1 week", 1, STREAK, BRONZE),
            new Achievement("Weekly Streaker",
                    "Reach the habit target for 2 weeks in a row", 2, STREAK, BRONZE),
            new Achievement("Streak Thrice",
                    "Reach the habit target for 3 weeks in a row", 3, STREAK, BRONZE),
            new Achievement("A Month of Streaks",
                    "Reach the habit target for 4 weeks in a row", 4, STREAK, SILVER),
            new Achievement("Five for Five",
                    "Reach the habit target for 5 weeks in a row", 5, STREAK, SILVER),
            new Achievement("Streak Deca",
                    "Reach the habit target for 10 weeks in a row", 10, STREAK, GOLD),
            new Achievement("Streak Champion",
                    "Reach the habit target for 20 weeks in a row", 20, STREAK, GOLD),
            new Achievement("Yearly Streaker",
                    "Reach the habit target for 52 weeks in a row", 52, STREAK, PLATINUM),
            new Achievement("Weekly First Time?",
                    "Complete the habit for the first time", 1, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Double Completion",
                    "Complete the habit for the second time", 2, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Triple Completion",
                    "Complete the habit for the third time", 3, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Quadruple Completion",
                    "Complete the habit for the fourth time", 4, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Quintuple Completion",
                    "Complete the habit for the fifth time", 5, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Deca-Weekly",
                    "Complete the habit 10 times", 10, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Fifteen Feats",
                    "Complete the habit 15 times", 15, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Twenty Triumphs",
                    "Complete the habit 20 times", 20, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Three Zero",
                    "Complete the habit 30 times", 30, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Quadruple Zero",
                    "Complete the habit 40 times", 40, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Fifty Feats",
                    "Complete the habit 50 times", 50, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Century Club",
                    "Complete the habit 100 times", 100, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Double Century Club",
                    "Complete the habit 200 times", 200, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Completionist",
                    "Complete the habit 400 times", 400, SINGULAR_SUCCESSES, PLATINUM),
            new Achievement("A New Weekly Beginning",
                    "Reach the habit target for the first time", 1, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Double Down",
                    "Reach the habit target for 2 weeks", 2, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Triple Threat",
                    "Reach the habit target for 3 weeks", 3, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Habit Quarter",
                    "Reach the habit target for 4 weeks", 4, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("The Quintessential Habit",
                    "Reach the habit target for 5 weeks", 5, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Deca-Weeks",
                    "Reach the habit target for 10 weeks", 10, PERIODIC_SUCCESSES, SILVER),
            new Achievement("Twenty Twenty",
                    "Reach the habit target for 20 weeks", 20, PERIODIC_SUCCESSES, SILVER),
            new Achievement("Thirty Thirty",
                    "Reach the habit target for 30 weeks", 30, PERIODIC_SUCCESSES, SILVER),
            new Achievement("Forty Forty",
                    "Reach the habit target for 40 weeks", 40, PERIODIC_SUCCESSES, GOLD),
            new Achievement("Habit Year",
                    "Reach the habit target for 52 weeks", 52, PERIODIC_SUCCESSES, GOLD),
            new Achievement("Century Habit",
                    "Reach the habit target for 100 weeks", 100, PERIODIC_SUCCESSES, PLATINUM),
            new Achievement("Habit Born",
                    "Track the habit for 1 week", 1, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Toddler",
                    "Track the habit for 2 weeks", 2, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Child",
                    "Track the habit for 3 weeks", 3, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Teen",
                    "Track the habit for 4 weeks", 4, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Adult",
                    "Track the habit for 5 weeks", 5, PERIOD_COUNT, BRONZE),
            new Achievement("Exists 10?",
                    "Track the habit for 10 weeks", 10, PERIOD_COUNT, SILVER),
            new Achievement("20's",
                    "Track the habit for 20 weeks", 20, PERIOD_COUNT, SILVER),
            new Achievement("30's",
                    "Track the habit for 30 weeks", 30, PERIOD_COUNT, SILVER),
            new Achievement("40's",
                    "Track the habit for 40 weeks", 40, PERIOD_COUNT, SILVER),
            new Achievement("Year of Tracking",
                    "Track the habit for 52 weeks", 52, PERIOD_COUNT, GOLD),
            new Achievement("Tracker Perfect",
                    "Track the habit for 100 weeks", 100, PERIOD_COUNT, GOLD),
            new Achievement("Double Century Tracker",
                    "Track the habit for 200 weeks", 200, PERIOD_COUNT, PLATINUM)
    );

    protected static final List<Achievement> MONTHLY_ACHIEVEMENTS = List.of(
            new Achievement("Monthly Streak Initiator",
                    "Reach the habit target for 1 month", 1, STREAK, BRONZE),
            new Achievement("Monthly Streaker",
                    "Reach the habit target for 2 months in a row", 2, STREAK, BRONZE),
            new Achievement("Streak Thrice",
                    "Reach the habit target for 3 months in a row", 3, STREAK, BRONZE),
            new Achievement("A Quarter of Streaks",
                    "Reach the habit target for 4 months in a row", 4, STREAK, SILVER),
            new Achievement("Five for Five",
                    "Reach the habit target for 5 months in a row", 5, STREAK, SILVER),
            new Achievement("Yearly Streaker",
                    "Reach the habit target for 12 months in a row", 12, STREAK, GOLD),
            new Achievement("Streak Champion",
                    "Reach the habit target for 24 months in a row", 24, STREAK, PLATINUM),
            new Achievement("Monthly First Time?",
                    "Complete the habit for the first time", 1, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Double Completion",
                    "Complete the habit for the second time", 2, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Triple Completion",
                    "Complete the habit for the third time", 3, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Quadruple Completion",
                    "Complete the habit for the fourth time", 4, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Quintuple Completion",
                    "Complete the habit for the fifth time", 5, SINGULAR_SUCCESSES, BRONZE),
            new Achievement("Deca-Monthly",
                    "Complete the habit 10 times", 10, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Fifteen Feats",
                    "Complete the habit 15 times", 15, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Twenty Triumphs",
                    "Complete the habit 20 times", 20, SINGULAR_SUCCESSES, SILVER),
            new Achievement("Three Zero",
                    "Complete the habit 30 times", 30, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Quadruple Zero",
                    "Complete the habit 40 times", 40, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Fifty Feats",
                    "Complete the habit 50 times", 50, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Century Club",
                    "Complete the habit 100 times", 100, SINGULAR_SUCCESSES, GOLD),
            new Achievement("Double Century Club",
                    "Complete the habit 200 times", 200, SINGULAR_SUCCESSES, GOLD),
            new Achievement("A Year's Worth?!?",
                    "Complete the habit 365 times", 365, SINGULAR_SUCCESSES, PLATINUM),
            new Achievement("A New Monthly Beginning",
                    "Reach the habit target for the first time", 1, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Double Down",
                    "Reach the habit target for 2 months", 2, PERIODIC_SUCCESSES, BRONZE),
            new Achievement("Triple Threat",
                    "Reach the habit target for 3 months", 3, PERIODIC_SUCCESSES, SILVER),
            new Achievement("Habit Quarter",
                    "Reach the habit target for 4 months", 4, PERIODIC_SUCCESSES, SILVER),
            new Achievement("The Quintessential Habit",
                    "Reach the habit target for 5 months", 5, PERIODIC_SUCCESSES, GOLD),
            new Achievement("Yearly Habit",
                    "Reach the habit target for 12 months", 12, PERIODIC_SUCCESSES, GOLD),
            new Achievement("Biyearly Habit",
                    "Reach the habit target for 24 months", 24, PERIODIC_SUCCESSES, GOLD),
            new Achievement("Crazy Consistency",
                    "Reach the habit target for 36 months", 36, PERIODIC_SUCCESSES, PLATINUM),
            new Achievement("Habit Born",
                    "Track the habit for 1 month", 1, PERIOD_COUNT, BRONZE),
            new Achievement("Habit Toddler",
                    "Track the habit for 2 months", 2, PERIOD_COUNT, BRONZE),
            new Achievement("Triple Track",
                    "Track the habit for 3 months", 3, PERIOD_COUNT, BRONZE),
            new Achievement("Tracker Quarter",
                    "Track the habit for 4 months", 4, PERIOD_COUNT, SILVER),
            new Achievement("The Quintessential Tracker",
                    "Track the habit for 5 months", 5, PERIOD_COUNT, SILVER),
            new Achievement("A Year of Tracking",
                    "Track the habit for 12 months", 12, PERIOD_COUNT, GOLD),
            new Achievement("Double Year Tracker",
                    "Track the habit for 24 months", 24, PERIOD_COUNT, GOLD),
            new Achievement("Quadruple Year Tracker",
                    "Track the habit for 48 months", 48, PERIOD_COUNT, PLATINUM)
    );
}