package ui.card;

import model.Habit;
import model.HabitStatistics;

import javax.swing.*;

import java.awt.*;

import static ui.Constants.*;

// Represents the habit statistics tab JPanel for a given habit
public class HabitStatisticsUI extends JPanel {
    private JLabel statsTitle;
    private JLabel streak;
    private JLabel bestStreak;
    private JLabel totalNumSuccess;
    private JLabel numPeriodSuccess;
    private JLabel numPeriod;
    private JLabel successRate;

    private Habit habit;
    private HabitStatistics habitStats;

    // EFFECTS: constructs a HabitStatisticsUI panel
    public HabitStatisticsUI(Habit habit) {
        this.habit = habit;
        this.habitStats = habit.getHabitStats();
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(APP_COLOUR);
        statsTitle = getTitleLabel();
        streak = getStreakLabel();
        bestStreak = getBestStreakLabel();
        totalNumSuccess = getTotalNumSuccessLabel();
        numPeriodSuccess = getNumPeriodSuccessLabel();
        numPeriod = getNumPeriodLabel();
        successRate = getSuccessRateLabel();
        add(statsTitle);
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        add(streak);
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        add(bestStreak);
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        add(totalNumSuccess);
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        add(numPeriodSuccess);
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        add(numPeriod);
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        add(successRate);
    }

    private JLabel getTitleLabel() {
        statsTitle = new JLabel(getTitleText());
        statsTitle.setFont(BIG_FONT);
        statsTitle.setForeground(FONT_COLOUR);
        statsTitle.setAlignmentX(CENTER_ALIGNMENT);
        return statsTitle;
    }

    private String getTitleText() {
        return "Statistics for " + habit.getName();
    }

    private JLabel getStreakLabel() {
        streak = new JLabel(getStreakText());
        streak.setFont(MEDIUM_FONT);
        streak.setForeground(FONT_COLOUR);
        streak.setAlignmentX(CENTER_ALIGNMENT);
        return streak;
    }

    private String getStreakText() {
        String[] strings = habitStats.getStreak() == 1 ? new String[]{"day", "week", "month"}
                : new String[]{"days", "weeks", "months"};
        return "Current Streak: " + habitStats.getStreak() + " " + getPeriodString(strings);
    }

    private JLabel getBestStreakLabel() {
        bestStreak = new JLabel(getBestStreakText());
        bestStreak.setFont(MEDIUM_FONT);
        bestStreak.setForeground(FONT_COLOUR);
        bestStreak.setAlignmentX(CENTER_ALIGNMENT);
        return bestStreak;
    }

    private String getBestStreakText() {
        String[] strings = habitStats.getBestStreak() == 1 ? new String[]{"day", "week", "month"}
                : new String[]{"days", "weeks", "months"};
        return "Best Streak: " + habitStats.getBestStreak() + " " + getPeriodString(strings);
    }

    private JLabel getTotalNumSuccessLabel() {
        totalNumSuccess = new JLabel(getTotalNumSuccessText());
        totalNumSuccess.setFont(MEDIUM_FONT);
        totalNumSuccess.setForeground(FONT_COLOUR);
        totalNumSuccess.setAlignmentX(CENTER_ALIGNMENT);
        return totalNumSuccess;
    }

    private String getTotalNumSuccessText() {
        return "Lifetime Number of Completions: " + habitStats.getTotalNumSuccess()
                + (habitStats.getTotalNumSuccess() == 1 ? " time" : " times");
    }

    private JLabel getNumPeriodSuccessLabel() {
        numPeriodSuccess = new JLabel(getNumPeriodSuccessText());
        numPeriodSuccess.setFont(MEDIUM_FONT);
        numPeriodSuccess.setForeground(FONT_COLOUR);
        numPeriodSuccess.setAlignmentX(CENTER_ALIGNMENT);
        return numPeriodSuccess;
    }

    private String getNumPeriodSuccessText() {
        String[] plural = new String[]{"Days", "Weeks", "Months"};
        String[] strings = habitStats.getNumPeriodSuccess() == 1 ? new String[]{"day", "week", "month"} :
                new String[]{"days", "weeks", "months"};
        return "Number of Successful " + getPeriodString(plural) + ": "
                + habitStats.getNumPeriodSuccess() + " " + getPeriodString(strings);
    }

    private JLabel getNumPeriodLabel() {
        numPeriod = new JLabel(getNumPeriodText());
        numPeriod.setFont(MEDIUM_FONT);
        numPeriod.setForeground(FONT_COLOUR);
        numPeriod.setAlignmentX(CENTER_ALIGNMENT);
        return numPeriod;
    }

    private String getNumPeriodText() {
        String[] plural = new String[]{"Days", "Weeks", "Months"};
        String[] strings = habitStats.getNumPeriod() == 1 ? new String[]{"day", "week", "month"}
                : new String[]{"days", "weeks", "months"};
        return "Number of " + getPeriodString(plural) + " Tracked: "
                + habitStats.getNumPeriod() + " " + getPeriodString(strings);
    }

    private JLabel getSuccessRateLabel() {
        successRate = new JLabel(getSuccessRateText());
        successRate.setFont(MEDIUM_FONT);
        successRate.setForeground(FONT_COLOUR);
        successRate.setAlignmentX(CENTER_ALIGNMENT);
        return successRate;
    }

    private String getSuccessRateText() {
        return "Success Rate: " + habitStats.getSuccessRate(habit.isPeriodComplete()) + "%";
    }

    private String getPeriodString(String[] strings) {
        switch (habit.getPeriod()) {
            case DAILY:
                return strings[0];
            case WEEKLY:
                return strings[1];
            default:
                return strings[2];
        }
    }

    public void updateStatsUI() {
        statsTitle.setText(getTitleText());
        streak.setText(getStreakText());
        bestStreak.setText(getBestStreakText());
        totalNumSuccess.setText(getTotalNumSuccessText());
        numPeriodSuccess.setText(getNumPeriodSuccessText());
        numPeriod.setText(getNumPeriodText());
        successRate.setText(getSuccessRateText());
    }
}