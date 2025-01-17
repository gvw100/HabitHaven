package ui.card;

import model.Habit;
import model.HabitManager;

import javax.swing.*;

import java.awt.*;

import static ui.Constants.*;

// Represents the JPanel displaying lifetime statistics of all existing habits
public class LifetimeStatisticsUI extends JPanel {
    private HabitManager habitManager;
    private JPanel mainPanel;
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

    // EFFECTS: Constructs a lifetime statistics panel
    public LifetimeStatisticsUI(HabitManager habitManager) {
        this.habitManager = habitManager;
        setupPanel();
    }

    // MODIFIES: this
    // EFFECTS: setups lifetime statistics panel
    private void setupPanel() {
        setLayout(new GridLayout(1, 1));
        JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        mainPanel = new JPanel();
        mainPanel.setBackground(APP_COLOUR);
        mainPanel.setLayout(new GridBagLayout());
        setupComponents();
        scrollPane.setViewportView(mainPanel);
        add(scrollPane);
    }

    // MODIFIES: this
    // EFFECTS: setups components in the habit statistics panel
    private void setupComponents() {
        setupTitle();
        setupBody();
    }

    // MODIFIES: this
    // EFFECTS: setups title JLabel and adds it to mainPanel
    private void setupTitle() {
        JLabel title = new JLabel("Lifetime Habit Statistics");
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

    // MODIFIES: this
    // EFFECTS: setups body components of lifetime statistics panel
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

    // MODIFIES: this
    // EFFECTS: setups no habits label and adds to mainPanel, called if user has no habits
    private void setupNoHabits() {
        JLabel noHabits = new JLabel("No habits to display statistics for");
        noHabits.setFont(BIG_FONT);
        noHabits.setForeground(FONT_COLOUR);
        noHabits.setAlignmentX(CENTER_ALIGNMENT);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        mainPanel.add(noHabits, constraints);
    }

    // MODIFIES: this
    // EFFECTS: setups number of habits label and adds to mainPanel
    private void setupNumHabits() {
        JLabel numHabits = new JLabel("        Number of Habits: " + habitManager.getHabits().size());
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

    // MODIFIES: this
    // EFFECTS: sets up allSingularCompletions, dailySingularCompletions, weeklySingularCompletions, and
    //          monthlySingularCompletions and adds them to mainPanel
    private void setupSingularCompletions() {
        int[] total = new int[1];
        int[] daily = new int[1];
        int[] weekly = new int[1];
        int[] monthly = new int[1];
        getTotalSingularCompletions(total, daily, weekly, monthly);
        setupAllSingularCompletionsLabel(total[0]);
        setupDailySingularCompletionsLabel(daily[0]);
        setupWeeklySingularCompletionsLabel(weekly[0]);
        setupMonthlySingularCompletionsLabel(monthly[0]);
        GridBagConstraints constraints = getLeftConstraints(2);
        mainPanel.add(allSingularCompletions, constraints);
        mainPanel.add(dailySingularCompletions, getCenterConstraints(3));
        mainPanel.add(weeklySingularCompletions, getCenterConstraints(4));
        mainPanel.add(monthlySingularCompletions, getCenterConstraints(5));
    }

    // MODIFIES: total, daily, weekly, monthly
    // EFFECTS: calculates total number of completions for all periods, daily, weekly, and monthly,
    //          storing calculated values in total, daily, weekly, and monthly
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

    // MODIFIES: this
    // EFFECTS: setups all singular completions label
    private void setupAllSingularCompletionsLabel(int total) {
        allSingularCompletions = new JLabel("        Total Number of Singular Completions: " + total);
        allSingularCompletions.setFont(MEDIUM_FONT);
        allSingularCompletions.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups all daily singular completions label
    private void setupDailySingularCompletionsLabel(int daily) {
        dailySingularCompletions = new JLabel("Total Daily Completions: " + daily);
        dailySingularCompletions.setFont(MEDIUM_FONT);
        dailySingularCompletions.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups all weekly singular completions label
    private void setupWeeklySingularCompletionsLabel(int weekly) {
        weeklySingularCompletions = new JLabel("Total Weekly Completions: " + weekly);
        weeklySingularCompletions.setFont(MEDIUM_FONT);
        weeklySingularCompletions.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups all monthly singular completions label
    private void setupMonthlySingularCompletionsLabel(int monthly) {
        monthlySingularCompletions = new JLabel("Total Monthly Completions: " + monthly);
        monthlySingularCompletions.setFont(MEDIUM_FONT);
        monthlySingularCompletions.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups streak labels and adds to mainPanel
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

    // MODIFIES: this
    // EFFECTS: setups daily best streak label
    private void setupDailyBestStreakLabel(int streak) {
        dailyBestStreak = new JLabel("        Best Daily Streak: " + streak);
        dailyBestStreak.setFont(MEDIUM_FONT);
        dailyBestStreak.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups weekly best streak label
    private void setupWeeklyBestStreakLabel(int streak) {
        weeklyBestStreak = new JLabel("        Best Weekly Streak: " + streak);
        weeklyBestStreak.setFont(MEDIUM_FONT);
        weeklyBestStreak.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups monthly best streak label
    private void setupMonthlyBestStreakLabel(int streak) {
        monthlyBestStreak = new JLabel("        Best Monthly Streak: " + streak);
        monthlyBestStreak.setFont(MEDIUM_FONT);
        monthlyBestStreak.setForeground(FONT_COLOUR);
    }

    // MODIFIES: daily, weekly, monthly
    // EFFECTS: calculates best streak for daily, weekly, and monthly, storing calculated values
    //          into daily, weekly, and monthly
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

    // MODIFIES: this
    // EFFECTS: setups periodic completions labels
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

    // MODIFIES: total, daily, weekly, monthly
    // EFFECTS: calculates total number of periodic completions, storing results in total, daily, weekly, and monthly
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

    // MODIFIES: this
    // EFFECTS: setups all periodic completions label
    private void setupAllPeriodicCompletionsLabels(int total) {
        allPeriodicCompletions = new JLabel("        Total Number of Periodic Completions: " + total);
        allPeriodicCompletions.setFont(MEDIUM_FONT);
        allPeriodicCompletions.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups daily periodic completions label
    private void setupDailyPeriodicCompletionsLabels(int daily) {
        dailyPeriodicCompletions = new JLabel("Total Periodic Daily Completions: " + daily);
        dailyPeriodicCompletions.setFont(MEDIUM_FONT);
        dailyPeriodicCompletions.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups weekly periodic completions label
    private void setupWeeklyPeriodicCompletionsLabels(int weekly) {
        weeklyPeriodicCompletions = new JLabel("Total Periodic Weekly Completions: " + weekly);
        weeklyPeriodicCompletions.setFont(MEDIUM_FONT);
        weeklyPeriodicCompletions.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups monthly periodic completions label
    private void setupMonthlyPeriodicCompletionsLabels(int monthly) {
        monthlyPeriodicCompletions = new JLabel("Total Periodic Monthly Completions: " + monthly);
        monthlyPeriodicCompletions.setFont(MEDIUM_FONT);
        monthlyPeriodicCompletions.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups success rate labels
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
        mainPanel.add(getEmptyRow(), getCenterConstraints(17));
    }

    // EFFECTS: returns an empty JPanel, used to fill up space in grid bag layout
    private JPanel getEmptyRow() {
        JPanel empty = new JPanel();
        empty.setBackground(APP_COLOUR);
        empty.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50));
        return empty;
    }

    // MODIFIES: average, daily, weekly, monthly
    // EFFECTS: calculates average success rates, storing the results in average, daily, weekly, and monthly
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

    // MODIFIES: average, daily, weekly, monthly
    // EFFECTS: calculates average success rates, storing results in average, daily, weekly, and monthly
    //          if the number of habits is 0, success rate is 0
    private void calculateAverage(int dailyCount, int weeklyCount, int monthlyCount,
                                  double[] average, double[] daily, double[] weekly, double[] monthly) {
        average[0] = habitManager.getHabits().size() == 0 ? 0 :
                Math.round(average[0] / habitManager.getHabits().size());
        daily[0] = dailyCount == 0 ? 0 : Math.round(daily[0] / dailyCount);
        weekly[0] = weeklyCount == 0 ? 0 : (Math.round(weekly[0] / weeklyCount));
        monthly[0] = monthlyCount == 0 ? 0 : Math.round(monthly[0] / monthlyCount);
    }

    // MODIFIES: this
    // EFFECTS: setups all habits average success rate label
    private void setupAverageSuccessRateLabel(double average) {
        averageSuccessRate = new JLabel("        All Habits Average Success Rate: " + (int) average + "%");
        averageSuccessRate.setFont(MEDIUM_FONT);
        averageSuccessRate.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups daily average success rate label
    private void setupDailyAverageSuccessRateLabel(double daily) {
        dailyAverageSuccessRate = new JLabel("Daily Habits Average Success Rate: " + (int) daily + "%");
        dailyAverageSuccessRate.setFont(MEDIUM_FONT);
        dailyAverageSuccessRate.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups weekly average success rate label
    private void setupWeeklyAverageSuccessRateLabel(double weekly) {
        weeklyAverageSuccessRate = new JLabel("Weekly Habits Average Success Rate: " + (int) weekly + "%");
        weeklyAverageSuccessRate.setFont(MEDIUM_FONT);
        weeklyAverageSuccessRate.setForeground(FONT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: setups monthly average success rate label
    private void setupMonthlyAverageSuccessRateLabel(double monthly) {
        monthlySuccessRate = new JLabel("Monthly Habits Average Success Rate: " + (int) monthly + "%");
        monthlySuccessRate.setFont(MEDIUM_FONT);
        monthlySuccessRate.setForeground(FONT_COLOUR);
    }

    // EFFECTS: returns grid bag constraints of left aligned elements
    private GridBagConstraints getLeftConstraints(int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = y;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        return constraints;
    }

    // EFFECTS: returns grid bag constraints of center aligned elements
    private GridBagConstraints getCenterConstraints(int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = y;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: this
    // EFFECTS: setups empty row and adds it to mainPanel
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