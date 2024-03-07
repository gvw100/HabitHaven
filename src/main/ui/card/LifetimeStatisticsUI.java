package ui.card;

import model.Habit;
import model.HabitManager;

import javax.swing.*;

import java.awt.*;

import static ui.Constants.*;

public class LifetimeStatisticsUI extends JPanel {
    private HabitManager habitManager;
    private JScrollPane scrollPane;
    private JPanel mainPanel;
    private JLabel title;
    private JLabel numHabits;
    private JLabel allSingularCompletions;
    private JLabel dailySingularCompletions;
    private JLabel weeklySingularCompletions;
    private JLabel monthlySingularCompletions;
    private JLabel allPeriodicCompletions;
    private JLabel dailyPeriodicCompletions;
    private JLabel weeklyPeriodicCompletions;
    private JLabel monthlyPeriodicCompletions;
    private JLabel dailyBestStreak;
    private JLabel weeklyBestStreak;
    private JLabel monthlyBestStreak;
    private JLabel averageSuccessRate;
    private JLabel dailyAverageSuccessRate;
    private JLabel weeklyAverageSuccessRate;
    private JLabel monthlySuccessRate;

    public LifetimeStatisticsUI(HabitManager habitManager) {
        this.habitManager = habitManager;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new GridLayout(1, 1));
        scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        mainPanel = new JPanel();
        mainPanel.setBackground(APP_COLOUR);
        mainPanel.setLayout(new GridBagLayout());
        setupComponents();
        scrollPane.setViewportView(mainPanel);
        add(scrollPane);
    }

    private void setupComponents() {
        setupTitle();
        setupBody();
    }

    private void setupTitle() {
        title = new JLabel("Lifetime Habit Statistics");
        title.setFont(HUGE_FONT);
        title.setForeground(FONT_COLOUR);
        title.setAlignmentX(CENTER_ALIGNMENT);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, PADDING * 2, 0);
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.PAGE_START;
        mainPanel.add(title, constraints);
    }

    private void setupBody() {
        if (habitManager.getHabits().isEmpty()) {
            setupNoHabits();
        } else {
            setupNumHabits();
            setupSingularCompletions();
            setupStreak();
            setupPeriodicCompletions();
            setupSuccessRate();
        }
        setupEmptyRow();
    }

    private void setupNoHabits() {
        JLabel noHabits = new JLabel("No habits to display statistics for.");
        noHabits.setFont(BIG_FONT);
        noHabits.setForeground(FONT_COLOUR);
        noHabits.setAlignmentX(CENTER_ALIGNMENT);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        mainPanel.add(noHabits, constraints);
    }

    private void setupNumHabits() {
        numHabits = new JLabel("        Number of Habits: " + habitManager.getHabits().size());
        numHabits.setFont(MEDIUM_FONT);
        numHabits.setForeground(FONT_COLOUR);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(numHabits, constraints);
    }

    private void setupSingularCompletions() {
        int[] total = new int[1];
        int[] daily = new int[1];
        int[] weekly = new int[1];
        int[] monthly = new int[1];
        getTotalSingularCompletions(total, daily, weekly, monthly);
        setupAllSingularCompletionsLabels(total[0]);
        setupDailySingularCompletionsLabels(daily[0]);
        setupWeeklySingularCompletionsLabels(weekly[0]);
        setupMonthlySingularCompletionsLabels(monthly[0]);
        GridBagConstraints constraints = getLeftConstraints(2);
        mainPanel.add(allSingularCompletions, constraints);
        mainPanel.add(dailySingularCompletions, getCenterConstraints(3));
        mainPanel.add(weeklySingularCompletions, getCenterConstraints(4));
        mainPanel.add(monthlySingularCompletions, getCenterConstraints(5));
    }

    private void getTotalSingularCompletions(int[] total, int[] daily, int[] weekly, int[] monthly) {
        total[0] = daily[0] = weekly[0] = monthly[0] = 0;
        for (Habit habit : habitManager.getHabits()) {
            total[0] += habit.getHabitStats().getTotalNumSuccess();
            switch (habit.getPeriod()) {
                case DAILY:
                    daily[0] += habit.getHabitStats().getNumPeriodSuccess();
                    break;
                case WEEKLY:
                    weekly[0] += habit.getHabitStats().getNumPeriodSuccess();
                    break;
                default:
                    monthly[0] += habit.getHabitStats().getNumPeriodSuccess();
            }
        }
    }

    private void setupAllSingularCompletionsLabels(int total) {
        allSingularCompletions = new JLabel("        Total Number of Singular Completions: " + total);
        allSingularCompletions.setFont(MEDIUM_FONT);
        allSingularCompletions.setForeground(FONT_COLOUR);
    }

    private void setupDailySingularCompletionsLabels(int daily) {
        dailySingularCompletions = new JLabel("Total Daily Completions: " + daily);
        dailySingularCompletions.setFont(MEDIUM_FONT);
        dailySingularCompletions.setForeground(FONT_COLOUR);
    }

    private void setupWeeklySingularCompletionsLabels(int weekly) {
        weeklySingularCompletions = new JLabel("Total Weekly Completions: " + weekly);
        weeklySingularCompletions.setFont(MEDIUM_FONT);
        weeklySingularCompletions.setForeground(FONT_COLOUR);
    }

    private void setupMonthlySingularCompletionsLabels(int monthly) {
        monthlySingularCompletions = new JLabel("Total Monthly Completions: " + monthly);
        monthlySingularCompletions.setFont(MEDIUM_FONT);
        monthlySingularCompletions.setForeground(FONT_COLOUR);
    }

    private void setupStreak() {
        int[] daily = new int[1];
        int[] weekly = new int[1];
        int[] monthly = new int[1];
        getBestStreak(daily, weekly, monthly);
        setupDailyBestStreakLabel(daily[0]);
        setupWeeklyBestStreakLabel(weekly[0]);
        setupMonthlyBestStreakLabel(monthly[0]);
        mainPanel.add(dailyBestStreak, getLeftConstraints(6));
        mainPanel.add(weeklyBestStreak, getLeftConstraints(7));
        mainPanel.add(monthlyBestStreak, getLeftConstraints(8));
    }

    private void setupDailyBestStreakLabel(int streak) {
        dailyBestStreak = new JLabel("        Best Daily Streak: " + streak);
        dailyBestStreak.setFont(MEDIUM_FONT);
        dailyBestStreak.setForeground(FONT_COLOUR);
    }

    private void setupWeeklyBestStreakLabel(int streak) {
        weeklyBestStreak = new JLabel("        Best Weekly Streak: " + streak);
        weeklyBestStreak.setFont(MEDIUM_FONT);
        weeklyBestStreak.setForeground(FONT_COLOUR);
    }

    private void setupMonthlyBestStreakLabel(int streak) {
        monthlyBestStreak = new JLabel("        Best Monthly Streak: " + streak);
        monthlyBestStreak.setFont(MEDIUM_FONT);
        monthlyBestStreak.setForeground(FONT_COLOUR);
    }

    private void getBestStreak(int[] daily, int[] weekly, int[] monthly) {
        daily[0] = weekly[0] = monthly[0] = 0;
        for (Habit habit : habitManager.getHabits()) {
            switch (habit.getPeriod()) {
                case DAILY:
                    daily[0] = Math.max(daily[0], habit.getHabitStats().getBestStreak());
                    break;
                case WEEKLY:
                    weekly[0] = Math.max(weekly[0], habit.getHabitStats().getBestStreak());
                    break;
                default:
                    monthly[0] = Math.max(monthly[0], habit.getHabitStats().getBestStreak());
            }
        }
    }

    private void setupPeriodicCompletions() {
        int[] total = new int[1];
        int[] daily = new int[1];
        int[] weekly = new int[1];
        int[] monthly = new int[1];
        getTotalPeriodicCompletions(total, daily, weekly, monthly);
        setupAllPeriodicCompletionsLabels(total[0]);
        setupDailyPeriodicCompletionsLabels(daily[0]);
        setupWeeklyPeriodicCompletionsLabels(weekly[0]);
        setupMonthlyPeriodicCompletionsLabels(monthly[0]);
        mainPanel.add(allPeriodicCompletions, getLeftConstraints(9));
        mainPanel.add(dailyPeriodicCompletions, getCenterConstraints(10));
        mainPanel.add(weeklyPeriodicCompletions, getCenterConstraints(11));
        mainPanel.add(monthlyPeriodicCompletions, getCenterConstraints(12));
    }

    private void getTotalPeriodicCompletions(int[] total, int[] daily, int[] weekly, int[] monthly) {
        total[0] = daily[0] = weekly[0] = monthly[0] = 0;
        for (Habit habit : habitManager.getHabits()) {
            total[0] += habit.getHabitStats().getNumPeriodSuccess();
            switch (habit.getPeriod()) {
                case DAILY:
                    daily[0] += habit.getHabitStats().getNumPeriodSuccess();
                    break;
                case WEEKLY:
                    weekly[0] += habit.getHabitStats().getNumPeriodSuccess();
                    break;
                default:
                    monthly[0] += habit.getHabitStats().getNumPeriodSuccess();
            }
        }
    }

    private void setupAllPeriodicCompletionsLabels(int total) {
        allPeriodicCompletions = new JLabel("        Total Number of Periodic Completions: " + total);
        allPeriodicCompletions.setFont(MEDIUM_FONT);
        allPeriodicCompletions.setForeground(FONT_COLOUR);
    }

    private void setupDailyPeriodicCompletionsLabels(int daily) {
        dailyPeriodicCompletions = new JLabel("Total Periodic Daily Completions: " + daily);
        dailyPeriodicCompletions.setFont(MEDIUM_FONT);
        dailyPeriodicCompletions.setForeground(FONT_COLOUR);
    }

    private void setupWeeklyPeriodicCompletionsLabels(int weekly) {
        weeklyPeriodicCompletions = new JLabel("Total Periodic Weekly Completions: " + weekly);
        weeklyPeriodicCompletions.setFont(MEDIUM_FONT);
        weeklyPeriodicCompletions.setForeground(FONT_COLOUR);
    }

    private void setupMonthlyPeriodicCompletionsLabels(int monthly) {
        monthlyPeriodicCompletions = new JLabel("Total Periodic Monthly Completions: " + monthly);
        monthlyPeriodicCompletions.setFont(MEDIUM_FONT);
        monthlyPeriodicCompletions.setForeground(FONT_COLOUR);
    }

    private void setupSuccessRate() {
        double[] average = new double[1];
        double[] daily = new double[1];
        double[] weekly = new double[1];
        double[] monthly = new double[1];
        getAverageSuccessRate(average, daily, weekly, monthly);
        setupAverageSuccessRateLabel(average[0]);
        setupDailyAverageSuccessRateLabel(daily[0]);
        setupWeeklyAverageSuccessRateLabel(weekly[0]);
        setupMonthlyAverageSuccessRateLabel(monthly[0]);
        mainPanel.add(averageSuccessRate, getLeftConstraints(13));
        mainPanel.add(dailyAverageSuccessRate, getCenterConstraints(14));
        mainPanel.add(weeklyAverageSuccessRate, getCenterConstraints(15));
        mainPanel.add(monthlySuccessRate, getCenterConstraints(16));
    }

    private void getAverageSuccessRate(double[] average, double[] daily, double[] weekly, double[] monthly) {
        int dailyCount = 0;
        int weeklyCount = 0;
        int monthlyCount = 0;
        average[0] = daily[0] = weekly[0] = monthly[0] = 0;
        for (Habit habit : habitManager.getHabits()) {
            average[0] += habit.getHabitStats().getSuccessRate(habit.isPeriodComplete());
            switch (habit.getPeriod()) {
                case DAILY:
                    dailyCount++;
                    daily[0] += habit.getHabitStats().getSuccessRate(habit.isPeriodComplete());
                    break;
                case WEEKLY:
                    weeklyCount++;
                    weekly[0] += habit.getHabitStats().getSuccessRate(habit.isPeriodComplete());
                    break;
                default:
                    monthlyCount++;
                    monthly[0] += habit.getHabitStats().getSuccessRate(habit.isPeriodComplete());
            }
        }
        calculateAverage(dailyCount, weeklyCount, monthlyCount, average, daily, weekly, monthly);
    }

    private void calculateAverage(int dailyCount, int weeklyCount, int monthlyCount,
                                  double[] average, double[] daily, double[] weekly, double[] monthly) {
        average[0] = habitManager.getHabits().size() == 0 ? 0 :
                Math.round(average[0] / habitManager.getHabits().size());
        daily[0] = dailyCount == 0 ? 0 : Math.round(daily[0] / dailyCount);
        weekly[0] = weeklyCount == 0 ? 0 : (Math.round(weekly[0] / weeklyCount));
        monthly[0] = monthlyCount == 0 ? 0 : Math.round(monthly[0] / monthlyCount);
    }

    private void setupAverageSuccessRateLabel(double average) {
        averageSuccessRate = new JLabel("        All Habits Average Success Rate: " + (int) average + "%");
        averageSuccessRate.setFont(MEDIUM_FONT);
        averageSuccessRate.setForeground(FONT_COLOUR);
    }

    private void setupDailyAverageSuccessRateLabel(double daily) {
        dailyAverageSuccessRate = new JLabel("Daily Habits Average Success Rate: " + (int) daily + "%");
        dailyAverageSuccessRate.setFont(MEDIUM_FONT);
        dailyAverageSuccessRate.setForeground(FONT_COLOUR);
    }

    private void setupWeeklyAverageSuccessRateLabel(double weekly) {
        weeklyAverageSuccessRate = new JLabel("Weekly Habits Average Success Rate: " + (int) weekly + "%");
        weeklyAverageSuccessRate.setFont(MEDIUM_FONT);
        weeklyAverageSuccessRate.setForeground(FONT_COLOUR);
    }

    private void setupMonthlyAverageSuccessRateLabel(double monthly) {
        monthlySuccessRate = new JLabel("Monthly Habits Average Success Rate: " + (int) monthly + "%");
        monthlySuccessRate.setFont(MEDIUM_FONT);
        monthlySuccessRate.setForeground(FONT_COLOUR);
    }

    private GridBagConstraints getLeftConstraints(int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = y;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        return constraints;
    }

    private GridBagConstraints getCenterConstraints(int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = y;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    private void setupEmptyRow() {
        JPanel panel = new JPanel();
        panel.setBackground(APP_COLOUR);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 1;
        mainPanel.add(new JLabel(), constraints);
    }
}