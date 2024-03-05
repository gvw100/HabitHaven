package model.achievement;

import model.HabitStatistics;
import model.Period;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Achievement> getNotAchieved(HabitStatistics habitStatistics, Period period) {
        List<Achievement> notAchieved = new ArrayList<>();
        switch (period) {
            case DAILY:
                notAchieved.addAll(DAILY_ACHIEVEMENTS);
                break;
            case WEEKLY:
                notAchieved.addAll(WEEKLY_ACHIEVEMENTS);
                break;
            default:
                notAchieved.addAll(MONTHLY_ACHIEVEMENTS);
        }
        List<Achievement> achievedAchievements = getAchieved(habitStatistics, period);
        notAchieved.removeAll(achievedAchievements);
        return notAchieved;
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
}
