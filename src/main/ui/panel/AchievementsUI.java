package ui.panel;

import model.Habit;

import javax.swing.*;

import java.awt.*;

import static model.achievement.AchievementManager.getAchieved;
import static ui.Constants.*;
import static ui.Constants.WINDOW_HEIGHT;

public class AchievementsUI extends JPanel {
    private Habit habit;
    private JTabbedPane tabbedPane;

    public AchievementsUI(Habit habit) {
        this.habit = habit;
        UIManager.put("TabbedPane.selected", APP_COLOUR.brighter().brighter().brighter());
        setupPanel();
    }

    private void setupPanel() {
        tabbedPane = new JTabbedPane();
        setBackground(APP_COLOUR);
        setLayout(new GridLayout(1, 1));
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane.setBackground(APP_COLOUR);
        tabbedPane.setForeground(FONT_COLOUR);
        tabbedPane.setFont(MEDIUM_FONT);
        tabbedPane.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setupPanels();
        add(tabbedPane);
    }

    private void setupPanels() {
        tabbedPane.addTab("Bronze", null, setupBronzeTier(), "Bronze Tier Achievements");
        tabbedPane.addTab("Silver", null, setupSilverTier(), "Silver Tier Achievements");
        tabbedPane.addTab("Gold", null, setupGoldTier(), "Gold Tier Achievements");
        tabbedPane.addTab("Platinum", null, setupPlatinumTier(), "Platinum Tier Achievements");
    }

    private JPanel setupBronzeTier() {
        return null;
    }

    private JPanel setupSilverTier() {
        return null;
    }

    private JPanel setupGoldTier() {
        return null;
    }

    private JPanel setupPlatinumTier() {
        return null;
    }

    public void updateAchievementsUI() {
        habit.setAchievements(getAchieved(habit.getHabitStats(), habit.getPeriod()));
    }
}
