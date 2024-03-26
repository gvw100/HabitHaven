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

    // MODIFIES: this
    // EFFECTS: setups habit statistics panel
    private void setupPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(APP_COLOUR);
        setupTitleLabel();
        setupStreakLabel();
        setupBestStreakLabel();
        setupTotalNumSuccessLabel();
        setupNumPeriodSuccessLabel();
        setupNumPeriodLabel();
        setupSuccessRateLabel();
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

    // MODIFIES: this
    // EFFECTS: setups title label
    private void setupTitleLabel() {
        statsTitle = new JLabel(getTitleText());
        statsTitle.setFont(BIG_FONT);
        statsTitle.setForeground(FONT_COLOUR);
        statsTitle.setAlignmentX(CENTER_ALIGNMENT);
    }

    // EFFECTS: returns habit statistics title string
    private String getTitleText() {
        return "Statistics for " + habit.getName();
    }

    // MODIFIES: this
    // EFFECTS: setups streak label
    private void setupStreakLabel() {
        streak = new JLabel(getStreakText());
        streak.setFont(MEDIUM_FONT);
        streak.setForeground(FONT_COLOUR);
        streak.setAlignmentX(CENTER_ALIGNMENT);
    }

    // EFFECTS: returns text for streak label
    private String getStreakText() {
        String[] strings = habitStats.getStreak() == 1 ? new String[]{"day", "week", "month"}
                : new String[]{"days", "weeks", "months"};
        return "Current Streak: " + habitStats.getStreak() + " " + getPeriodString(strings);
    }

    // MODIFIES: this
    // EFFECTS: setups best streak label
    private void setupBestStreakLabel() {
        bestStreak = new JLabel(getBestStreakText());
        bestStreak.setFont(MEDIUM_FONT);
        bestStreak.setForeground(FONT_COLOUR);
        bestStreak.setAlignmentX(CENTER_ALIGNMENT);
    }

    // EFFECTS: returns text for best streak label
    private String getBestStreakText() {
        String[] strings = habitStats.getBestStreak() == 1 ? new String[]{"day", "week", "month"}
                : new String[]{"days", "weeks", "months"};
        return "Best Streak: " + habitStats.getBestStreak() + " " + getPeriodString(strings);
    }

    // MODIFIES: this
    // EFFECTS: setups total num success label
    private void setupTotalNumSuccessLabel() {
        totalNumSuccess = new JLabel(getTotalNumSuccessText());
        totalNumSuccess.setFont(MEDIUM_FONT);
        totalNumSuccess.setForeground(FONT_COLOUR);
        totalNumSuccess.setAlignmentX(CENTER_ALIGNMENT);
    }

    // EFFECTS: returns text for total num success label
    private String getTotalNumSuccessText() {
        return "Lifetime Number of Completions: " + habitStats.getTotalNumSuccess()
                + (habitStats.getTotalNumSuccess() == 1 ? " time" : " times");
    }

    // MODIFIES: this
    // EFFECTS: setups num period success label
    private void setupNumPeriodSuccessLabel() {
        numPeriodSuccess = new JLabel(getNumPeriodSuccessText());
        numPeriodSuccess.setFont(MEDIUM_FONT);
        numPeriodSuccess.setForeground(FONT_COLOUR);
        numPeriodSuccess.setAlignmentX(CENTER_ALIGNMENT);
    }

    // EFFECTS: returns text for num period success label
    private String getNumPeriodSuccessText() {
        String[] plural = new String[]{"Days", "Weeks", "Months"};
        String[] strings = habitStats.getNumPeriodSuccess() == 1 ? new String[]{"day", "week", "month"} :
                new String[]{"days", "weeks", "months"};
        return "Number of Successful " + getPeriodString(plural) + ": "
                + habitStats.getNumPeriodSuccess() + " " + getPeriodString(strings);
    }

    // MODIFIES: this
    // EFFECTS: setups num period label
    private void setupNumPeriodLabel() {
        numPeriod = new JLabel(getNumPeriodText());
        numPeriod.setFont(MEDIUM_FONT);
        numPeriod.setForeground(FONT_COLOUR);
        numPeriod.setAlignmentX(CENTER_ALIGNMENT);
    }

    // EFFECTS: returns text for num period label
    private String getNumPeriodText() {
        String[] plural = new String[]{"Days", "Weeks", "Months"};
        String[] strings = habitStats.getNumPeriod() == 1 ? new String[]{"day", "week", "month"}
                : new String[]{"days", "weeks", "months"};
        return "Number of " + getPeriodString(plural) + " Tracked: "
                + habitStats.getNumPeriod() + " " + getPeriodString(strings);
    }

    // MODIFIES: this
    // EFFECTS: setups success rate label
    private void setupSuccessRateLabel() {
        successRate = new JLabel(getSuccessRateText());
        successRate.setFont(MEDIUM_FONT);
        successRate.setForeground(FONT_COLOUR);
        successRate.setAlignmentX(CENTER_ALIGNMENT);
    }

    // EFFECTS: returns text for success rate label
    private String getSuccessRateText() {
        return "Success Rate: " + habitStats.getSuccessRate(habit.isPeriodComplete()) + "%";
    }

    // EFFECTS: returns period string in given array based on period of habit
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

    // MODIFIES: this
    // EFFECTS: updates habit statistics UI
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