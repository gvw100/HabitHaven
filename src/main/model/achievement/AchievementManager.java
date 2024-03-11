package model.achievement;

import model.HabitStatistics;
import model.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Represents the manager for achievements
public class AchievementManager extends AchievementList {

    // EFFECTS: returns all achievements with given period, for testing purposes
    public static List<Achievement> getAllAchievements(Period period) {
        switch (period) {
            case DAILY:
                return DAILY_ACHIEVEMENTS;
            case WEEKLY:
                return WEEKLY_ACHIEVEMENTS;
            default:
                return MONTHLY_ACHIEVEMENTS;
        }
    }

    // EFFECTS: returns list of earned achievements
    public static List<Achievement> getAchieved(HabitStatistics habitStatistics, Period period) {
        switch (period) {
            case DAILY:
                return getAchievements(habitStatistics, DAILY_ACHIEVEMENTS);
            case WEEKLY:
                return getAchievements(habitStatistics, WEEKLY_ACHIEVEMENTS);
            default:
                return getAchievements(habitStatistics, MONTHLY_ACHIEVEMENTS);
        }
    }

    // EFFECTS: given an old list, returns list of newly achieved achievements
    public static List<Achievement> getNewlyAchieved(List<Achievement> old,
                                                     HabitStatistics habitStatistics, Period period) {
        List<Achievement> newlyAchieved = getAchieved(habitStatistics, period);
        newlyAchieved.removeAll(old);
        return newlyAchieved;
    }

    // EFFECTS: returns whether the given achievement is achieved
    public static boolean isAchieved(HabitStatistics habitStatistics, Achievement achievement) {
        if (achievement.getType() == AchievementType.STREAK) {
            return habitStatistics.getBestStreak() >= achievement.getTarget();
        } else if (achievement.getType() == AchievementType.SINGULAR_SUCCESSES) {
            return habitStatistics.getTotalNumSuccess() >= achievement.getTarget();
        } else if (achievement.getType() == AchievementType.PERIODIC_SUCCESSES) {
            return habitStatistics.getNumPeriodSuccess() >= achievement.getTarget();
        } else {
            return habitStatistics.getNumPeriod() >= achievement.getTarget();
        }
    }

    // EFFECTS: returns all achievements in achievements that have been achieved
    private static List<Achievement> getAchievements(HabitStatistics habitStatistics, List<Achievement> achievements) {
        List<Achievement> achievedAchievements = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (isAchieved(habitStatistics, achievement)) {
                achievedAchievements.add(achievement);
            }
        }
        return achievedAchievements;
    }

    // EFFECTS: returns list of all bronze achievements
    public static List<Achievement> getBronzeAchievements(Period period) {
        switch (period) {
            case DAILY:
                return getTierAchievements(DAILY_ACHIEVEMENTS, AchievementTier.BRONZE);
            case WEEKLY:
                return getTierAchievements(WEEKLY_ACHIEVEMENTS, AchievementTier.BRONZE);
            default:
                return getTierAchievements(MONTHLY_ACHIEVEMENTS, AchievementTier.BRONZE);
        }
    }

    // EFFECTS: returns list of all silver achievements
    public static List<Achievement> getSilverAchievements(Period period) {
        switch (period) {
            case DAILY:
                return getTierAchievements(DAILY_ACHIEVEMENTS, AchievementTier.SILVER);
            case WEEKLY:
                return getTierAchievements(WEEKLY_ACHIEVEMENTS, AchievementTier.SILVER);
            default:
                return getTierAchievements(MONTHLY_ACHIEVEMENTS, AchievementTier.SILVER);
        }
    }

    // EFFECTS: returns list of all gold achievements
    public static List<Achievement> getGoldAchievements(Period period) {
        switch (period) {
            case DAILY:
                return getTierAchievements(DAILY_ACHIEVEMENTS, AchievementTier.GOLD);
            case WEEKLY:
                return getTierAchievements(WEEKLY_ACHIEVEMENTS, AchievementTier.GOLD);
            default:
                return getTierAchievements(MONTHLY_ACHIEVEMENTS, AchievementTier.GOLD);
        }
    }

    // EFFECTS: returns list of all platinum achievements
    public static List<Achievement> getPlatinumAchievements(Period period) {
        switch (period) {
            case DAILY:
                return getTierAchievements(DAILY_ACHIEVEMENTS, AchievementTier.PLATINUM);
            case WEEKLY:
                return getTierAchievements(WEEKLY_ACHIEVEMENTS, AchievementTier.PLATINUM);
            default:
                return getTierAchievements(MONTHLY_ACHIEVEMENTS, AchievementTier.PLATINUM);
        }
    }

    // EFFECTS: returns list of all achievements of the given tier
    private static List<Achievement> getTierAchievements(List<Achievement> achievements, AchievementTier tier) {
        return achievements.stream()
                .filter(achievement -> achievement.getTier() == tier)
                .collect(Collectors.toList());
    }
}
