package ui.card.habit;

import model.Habit;
import model.achievement.Achievement;
import model.achievement.AchievementManager;

import javax.swing.*;

import java.awt.*;
import java.util.List;

import static model.achievement.AchievementManager.*;
import static ui.Constants.*;
import static ui.Constants.WINDOW_HEIGHT;

// Represents the JPanel for the achievements tab in a habit
public class AchievementsUI extends JPanel {
    private Habit habit;
    private JTabbedPane tabbedPane;
    private JScrollPane parentBronze;
    private JScrollPane parentSilver;
    private JScrollPane parentGold;
    private JScrollPane parentPlatinum;
    private JPanel childBronze;
    private JPanel childSilver;
    private JPanel childGold;
    private JPanel childPlatinum;

    // EFFECTS: constructs the JPanel for achievements tab
    public AchievementsUI(Habit habit) {
        this.habit = habit;
        UIManager.put("TabbedPane.selected", APP_COLOUR.brighter().brighter().brighter());
        setupPanel();
    }

    // MODIFIES: this
    // EFFECTS: setups achievements tab
    private void setupPanel() {
        setBackground(APP_COLOUR);
        setLayout(new GridLayout(1, 1));
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane.setBackground(APP_COLOUR);
        tabbedPane.setForeground(FONT_COLOUR);
        tabbedPane.setFont(MEDIUM_FONT);
        tabbedPane.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setupTabs();
        add(tabbedPane);
    }

    // MODIFIES: this
    // EFFECTS: setups achievements sub-tabs
    private void setupTabs() {
        setupBronzeTier();
        setupSilverTier();
        setupGoldTier();
        setupPlatinumTier();
        tabbedPane.addTab("Bronze", BRONZE_ICON, parentBronze, "Bronze Tier Achievements");
        tabbedPane.addTab("Silver", SILVER_ICON, parentSilver, "Silver Tier Achievements");
        tabbedPane.addTab("Gold", GOLD_ICON, parentGold, "Gold Tier Achievements");
        tabbedPane.addTab("Platinum", PLATINUM_ICON, parentPlatinum, "Platinum Tier Achievements");
    }

    // MODIFIES: this
    // EFFECTS: setups bronze tier tab
    private void setupBronzeTier() {
        parentBronze = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parentBronze.getVerticalScrollBar().setUnitIncrement(10);
        childBronze = new JPanel();
        childBronze.setBackground(APP_COLOUR);
        childBronze.setLayout(new GridBagLayout());
        setupTitlePanel(childBronze, "Bronze Tier Achievements");
        setupGrids(childBronze, BRONZE_ICON, getBronzeAchievements(habit.getPeriod()));
        setupEmptyRow(childBronze);
        parentBronze.setViewportView(childBronze);
    }

    // MODIFIES: this
    // EFFECTS: setups silver tier tab
    private void setupSilverTier() {
        parentSilver = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parentSilver.getVerticalScrollBar().setUnitIncrement(10);
        childSilver = new JPanel();
        childSilver.setBackground(APP_COLOUR);
        childSilver.setLayout(new GridBagLayout());
        setupTitlePanel(childSilver, "Silver Tier Achievements");
        setupGrids(childSilver, SILVER_ICON, getSilverAchievements(habit.getPeriod()));
        setupEmptyRow(childSilver);
        parentSilver.setViewportView(childSilver);
    }

    // MODIFIES: this
    // EFFECTS: setups gold tier tab
    private void setupGoldTier() {
        parentGold = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parentGold.getVerticalScrollBar().setUnitIncrement(10);
        childGold = new JPanel();
        childGold.setBackground(APP_COLOUR);
        childGold.setLayout(new GridBagLayout());
        setupTitlePanel(childGold, "Gold Tier Achievements");
        setupGrids(childGold, GOLD_ICON, getGoldAchievements(habit.getPeriod()));
        setupEmptyRow(childGold);
        parentGold.setViewportView(childGold);
    }

    // MODIFIES: this
    // EFFECTS: setups platinum tier tab
    private void setupPlatinumTier() {
        parentPlatinum = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parentPlatinum.getVerticalScrollBar().setUnitIncrement(10);
        childPlatinum = new JPanel();
        childPlatinum.setBackground(APP_COLOUR);
        childPlatinum.setLayout(new GridBagLayout());
        setupTitlePanel(childPlatinum, "Platinum Tier Achievements");
        setupGrids(childPlatinum, PLATINUM_ICON, getPlatinumAchievements(habit.getPeriod()));
        setupEmptyRow(childPlatinum);
        parentPlatinum.setViewportView(childPlatinum);
    }

    // MODIFIES: panel
    // EFFECTS: setups title panel and adds it to given JPanel
    private void setupTitlePanel(JPanel panel, String title) {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(APP_COLOUR);
        titlePanel.setLayout(new FlowLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(BIG_FONT);
        titleLabel.setForeground(FONT_COLOUR);
        titlePanel.add(titleLabel);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titlePanel, constraints);
    }

    // MODIFIES: panel
    // EFFECTS: setups achievement grids in a given sub-tab
    private void setupGrids(JPanel panel, ImageIcon icon, List<Achievement> achievements) {
        int i = 0;
        for (Achievement achievement : achievements) {
            setupGrid(panel, achievement, icon, i);
            i++;
        }
    }

    // MODIFIES: panel
    // EFFECTS: setups achievement grid in a given sub-tab with index i
    private void setupGrid(JPanel panel, Achievement achievement, ImageIcon icon, int i) {
        JPanel achievementPanel = new JPanel();
        achievementPanel.setBackground(
                AchievementManager.isAchieved(habit.getHabitStats(), achievement) ? SUCCESS_GREEN : APP_COLOUR);
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
        JLabel achievementLabel = new JLabel(achievement.getName(), icon, JLabel.LEFT);
        achievementLabel.setFont(MEDIUM_FONT);
        achievementLabel.setForeground(FONT_COLOUR);
        achievementLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel descriptionLabel = new JLabel(achievement.getDescription());
        descriptionLabel.setFont(SMALL_FONT);
        descriptionLabel.setForeground(FONT_COLOUR);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel progressLabel = new JLabel(getProgressString(achievement));
        progressLabel.setFont(MEDIUM_FONT);
        progressLabel.setForeground(FONT_COLOUR);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        addComponents(achievementPanel, achievementLabel, descriptionLabel, progressLabel);
        GridBagConstraints constraints = getAchievementConstraint(i);
        panel.add(achievementPanel, constraints);
    }

    // MODIFIES: achievementPanel
    // EFFECTS: adds components to achievementPanel
    private void addComponents(JPanel achievementPanel, JLabel achievementLabel,
                               JLabel descriptionLabel, JLabel progressLabel) {
        achievementPanel.add(achievementLabel);
        achievementPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        achievementPanel.add(descriptionLabel);
        achievementPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        achievementPanel.add(progressLabel);
        achievementPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        achievementPanel.setBorder(BorderFactory.createLineBorder(FONT_COLOUR, 2));
    }

    // EFFECTS: returns grid bag constraints for grid with given index
    private GridBagConstraints getAchievementConstraint(int index) {
        int row = (int) Math.floor((double) index / 3) + 1;
        int column = index % 3;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        return constraints;
    }

    // EFFECTS: returns progress string of an achievement
    private String getProgressString(Achievement achievement) {
        int num;
        if (AchievementManager.isAchieved(habit.getHabitStats(), achievement)) {
            num = achievement.getTarget();
        } else {
            switch (achievement.getType()) {
                case STREAK:
                    num = habit.getHabitStats().getBestStreak();
                    break;
                case SINGULAR_SUCCESSES:
                    num = habit.getHabitStats().getTotalNumSuccess();
                    break;
                case PERIODIC_SUCCESSES:
                    num = habit.getHabitStats().getNumPeriodSuccess();
                    break;
                default:
                    num = habit.getHabitStats().getNumPeriod();
            }
        }
        return num + " / " + achievement.getTarget();
    }

    // MODIFIES: panel
    // EFFECTS: adds empty row to given panel
    private void setupEmptyRow(JPanel panel) {
        JPanel empty = new JPanel();
        empty.setBackground(APP_COLOUR);
        empty.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(empty, constraints);
    }

    // MODIFIES: this
    // EFFECTS: updates all sub-tabs in achievements tab
    public void updateAchievementsUI() {
        updateBronze();
        updateSilver();
        updateGold();
        updatePlatinum();
    }

    // MODIFIES: this
    // EFFECTS: updates bronze sub-tab
    private void updateBronze() {
        childBronze.removeAll();
        setupTitlePanel(childBronze, "Bronze Tier Achievements");
        setupGrids(childBronze, BRONZE_ICON, getBronzeAchievements(habit.getPeriod()));
        setupEmptyRow(childBronze);
    }

    // MODIFIES: this
    // EFFECTS: updates silver sub-tab
    private void updateSilver() {
        childSilver.removeAll();
        setupTitlePanel(childSilver, "Silver Tier Achievements");
        setupGrids(childSilver, SILVER_ICON, getSilverAchievements(habit.getPeriod()));
        setupEmptyRow(childSilver);
    }

    // MODIFIES: this
    // EFFECTS: updates gold sub-tab
    private void updateGold() {
        childGold.removeAll();
        setupTitlePanel(childGold, "Gold Tier Achievements");
        setupGrids(childGold, GOLD_ICON, getGoldAchievements(habit.getPeriod()));
        setupEmptyRow(childGold);
    }

    // MODIFIES: this
    // EFFECTS: updates platinum sub-tab
    private void updatePlatinum() {
        childPlatinum.removeAll();
        setupTitlePanel(childPlatinum, "Platinum Tier Achievements");
        setupGrids(childPlatinum, PLATINUM_ICON, getPlatinumAchievements(habit.getPeriod()));
        setupEmptyRow(childPlatinum);
    }
}