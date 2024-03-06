package ui.card;

import model.HabitManager;

import javax.swing.*;

import java.awt.*;

import static ui.Constants.*;

public class SettingsUI extends JPanel {
    private JLabel settingsLabel;
    private JButton changeUserName;
    private JButton turnOffNotifications;
    private JButton deleteAllHabits;
    private HabitManager habitManager;

    public SettingsUI(HabitManager habitManager) {
        this.habitManager = habitManager;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(APP_COLOUR);
        setupComponents();
    }

    private void setupComponents() {
        setupSettingsLabel();
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        setupChangeUserNameButton();
        add(Box.createRigidArea(new Dimension(0, PADDING)));
        setupTurnOffNotificationsButton();
        add(Box.createRigidArea(new Dimension(0, PADDING)));
        setupDeleteAllHabitsButton();
        add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    private void setupSettingsLabel() {
        settingsLabel = new JLabel("Settings");
        settingsLabel.setAlignmentX(CENTER_ALIGNMENT);
        settingsLabel.setFont(HUGE_FONT);
        settingsLabel.setForeground(FONT_COLOUR);
        add(settingsLabel);
    }

    private void setupChangeUserNameButton() {
        changeUserName = new JButton("Change User Name");
        makeButton(changeUserName, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        changeUserName.addActionListener(e -> {
            String newUserName = JOptionPane.showInputDialog("Change User Name");
            if (newUserName != null) {
                if (newUserName.isBlank()) {
                    JOptionPane.showMessageDialog(null, "User name cannot be blank");
                } else if (newUserName.length() > MAX_NAME_LENGTH) {
                    JOptionPane.showMessageDialog(
                            null, "User name cannot be longer than " + MAX_NAME_LENGTH + " characters");
                } else {
                    HabitManager.setUsername(newUserName);
                    HabitManagerUI.setIsSaved(false);
                    JOptionPane.showMessageDialog(null, "User name changed to " + newUserName);
                }
            }
        });
        add(changeUserName);
    }

    private void setupTurnOffNotificationsButton() {
        turnOffNotifications = new JButton("Turn Off All Notifications");
        makeButton(turnOffNotifications, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        turnOffNotifications.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    null, "Are you sure you want to turn off all notifications?", "Turn Off Notifications",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                habitManager.turnOffReminders();
                HabitManagerUI.setIsSaved(false);
                JOptionPane.showMessageDialog(null, "All notifications turned off");
            }
        });
        add(turnOffNotifications);
    }

    private void setupDeleteAllHabitsButton() {
        deleteAllHabits = new JButton("Delete All Habits");
        makeButton(deleteAllHabits, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        deleteAllHabits.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    null, "Are you sure you want to delete all habits?", "Delete All Habits",
                        JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                int size = habitManager.getSize();
                for (int i = 0; i < size; i++) {
                    habitManager.deleteHabit(habitManager.getHabits().get(0));
                }
                HabitManagerUI.setIsSaved(false);
                JOptionPane.showMessageDialog(null, "All habits deleted successfully");
            }
        });
        add(deleteAllHabits);
    }
}