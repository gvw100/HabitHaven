package model.achievement;

import model.HabitStatistics;
import model.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Represents the manager for achievements
public class AchievementManager extends AchievementList {

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

    public static List<Achievement> getNewlyAchieved(List<Achievement> old,
                                                     HabitStatistics habitStatistics, Period period) {
        List<Achievement> newlyAchieved = getAchieved(habitStatistics, period);
        newlyAchieved.removeAll(old);
        return newlyAchieved;
    }

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

    private static List<Achievement> getAchievements(HabitStatistics habitStatistics, List<Achievement> achievements) {
        List<Achievement> achievedAchievements = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (achievement.getType() == AchievementType.STREAK) {
                if (habitStatistics.getBestStreak() >= achievement.getTarget()) {
                    achievedAchievements.add(achievement);
                }
            } else if (achievement.getType() == AchievementType.SINGULAR_SUCCESSES) {
                if (habitStatistics.getTotalNumSuccess() >= achievement.getTarget()) {
                    achievedAchievements.add(achievement);
                }
            } else if (achievement.getType() == AchievementType.PERIODIC_SUCCESSES) {
                if (habitStatistics.getNumPeriodSuccess() >= achievement.getTarget()) {
                    achievedAchievements.add(achievement);
                }
            } else {
                if (habitStatistics.getNumPeriod() >= achievement.getTarget()) {
                    achievedAchievements.add(achievement);
                }
            }
        }
        return achievedAchievements;
    }

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

    private static List<Achievement> getTierAchievements(List<Achievement> achievements, AchievementTier tier) {
        return achievements.stream()
                .filter(achievement -> achievement.getTier() == tier)
                .collect(Collectors.toList());
    }
}
