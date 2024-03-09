package ui.card;

import model.HabitManager;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

public class SettingsUI extends JPanel {
    private HabitManager habitManager;
    private JPanel mainPanel;
    private JFrame parent;

    public SettingsUI(HabitManager habitManager, JFrame parent) {
        this.habitManager = habitManager;
        setupPanel();
        this.parent = parent;
    }

    private void setupPanel() {
        setLayout(new GridLayout(1, 1));
        setBackground(APP_COLOUR);
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(APP_COLOUR);
        JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        setupComponents();
        scrollPane.setViewportView(mainPanel);
        add(scrollPane);
    }

    private void setupComponents() {
        setupSettingsLabel();
        setupChangeUserNameButton();
        setupToggleAutoSaveButton();
        setupTurnOffNotificationsButton();
        setupExportButton();
        setupImportButton();
        setupDeleteAllHabitsButton();
        setupEmptySpace();
    }

    private void setupSettingsLabel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new FlowLayout());
        settingsPanel.setBackground(APP_COLOUR);
        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setAlignmentX(CENTER_ALIGNMENT);
        settingsLabel.setFont(HUGE_FONT);
        settingsLabel.setForeground(FONT_COLOUR);
        settingsPanel.add(settingsLabel);
        mainPanel.add(settingsPanel, getTitleConstraints());
    }

    private void setupChangeUserNameButton() {
        JButton changeUserName = new JButton("Change User Name");
        makeButton(changeUserName, WINDOW_WIDTH - SIDE_BAR_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        changeUserName.addActionListener(e -> invokeLater(() -> {
            String newUserName = JOptionPane.showInputDialog("Change User Name");
            if (newUserName != null) {
                if (newUserName.isBlank()) {
                    JOptionPane.showMessageDialog(null, "User name cannot be blank");
                } else if (newUserName.length() > MAX_NAME_LENGTH) {
                    JOptionPane.showMessageDialog(
                            null, "User name cannot be longer than " + MAX_NAME_LENGTH + " characters");
                } else {
                    HabitManager.setUsername(newUserName);
                    HabitManagerUI.changeMade();
                    JOptionPane.showMessageDialog(null, "User name changed to " + newUserName);
                }
            }
        }));
        mainPanel.add(changeUserName, getSettingsConstraints(1));
    }

    private void setupToggleAutoSaveButton() {
        boolean isAutoSave = HabitManager.isIsAutoSave();
        String text = isAutoSave ? "Turn Off Auto Save" : "Turn On Auto Save";
        JToggleButton toggleAutoSave = new JToggleButton(text, isAutoSave ? SAVE_OFF_ICON : SAVE_ICON, isAutoSave);
        toggleAutoSave.setBackground(APP_COLOUR);
        toggleAutoSave.setForeground(FONT_COLOUR);
        toggleAutoSave.setFont(MEDIUM_FONT);
        toggleAutoSave.setContentAreaFilled(false);
        toggleAutoSave.setFocusable(false);
        toggleAutoSave.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, LARGE_BUTTON_HEIGHT));
        toggleAutoSave.setMaximumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, LARGE_BUTTON_HEIGHT));
        toggleAutoSave.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, LARGE_BUTTON_HEIGHT));
        toggleAutoSave.setAlignmentX(CENTER_ALIGNMENT);
        setupAutoSaveListener(toggleAutoSave);
        mainPanel.add(toggleAutoSave, getSettingsConstraints(2));
    }

    private void setupAutoSaveListener(JToggleButton autoSave) {
        autoSave.addItemListener(e -> invokeLater(() -> toggleAutoSaveButton(autoSave)));
    }

    private void toggleAutoSaveButton(JToggleButton autoSave) {
        HabitManager.setIsAutoSave(!HabitManager.isIsAutoSave());
        HabitManagerUI.changeMade();
        autoSave.setText(HabitManager.isIsAutoSave() ? "Turn Off Auto Save" : "Turn On Auto Save");
        autoSave.setIcon(HabitManager.isIsAutoSave() ? SAVE_OFF_ICON : SAVE_ICON);
    }

    private void setupTurnOffNotificationsButton() {
        JButton turnOffNotifications = new JButton("Turn Off All Notifications", BELL_OFF);
        makeButton(turnOffNotifications, WINDOW_WIDTH - SIDE_BAR_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        turnOffNotifications.addActionListener(e -> invokeLater(() -> {
            int response = JOptionPane.showConfirmDialog(
                    null, "Are you sure you want to turn off all notifications?", "Turn Off Notifications",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                habitManager.turnOffReminders();
                HabitManagerUI.changeMade();
                JOptionPane.showMessageDialog(null, "All notifications turned off");
            }
        }));
        mainPanel.add(turnOffNotifications, getSettingsConstraints(3));
    }

    private void setupExportButton() {
        JButton export = new JButton("Export to File");
        makeButton(export, WINDOW_WIDTH - SIDE_BAR_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        JFileChooser fileChooser = setupExportChooser();
        export.addActionListener(e -> invokeLater(() -> {
            int response = fileChooser.showSaveDialog(parent);
            if (response == JFileChooser.APPROVE_OPTION) {
                exportToFile(fileChooser.getSelectedFile().toPath());
                JOptionPane.showMessageDialog(null, "Exported Successfully!");
            }
        }));
        mainPanel.add(export, getSettingsConstraints(4));
    }

    private JFileChooser setupExportChooser() {
        JFileChooser chooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File file = new File(getSelectedFile().toString() + ".json");
                if (file.exists() && JOptionPane.showConfirmDialog(null,
                        "File already exists. Do you want to overwrite it?",
                        "Export to File", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
                super.approveSelection();
            }
        };
        chooser.setDialogTitle("Export to File");
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        chooser.setAcceptAllFileFilterUsed(false);
        return chooser;
    }

    private void exportToFile(Path path) {
        JsonWriter writer = new JsonWriter(path.toString() + ".json");
        try {
            writer.open();
            writer.write(habitManager);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting file");
        }
    }

    private void setupImportButton() {
        JButton importButton = new JButton("Import from File");
        makeButton(importButton, WINDOW_WIDTH - SIDE_BAR_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        JFileChooser fileChooser = setupImportChooser();
        importButton.addActionListener(e -> invokeLater(() -> {
            if (JOptionPane.showConfirmDialog(
                    null, "Importing will overwrite all your habits. Are you sure you want to continue?",
                    "Import from File", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            int response = fileChooser.showOpenDialog(parent);
            if (response == JFileChooser.APPROVE_OPTION) {
                importFromFile(fileChooser.getSelectedFile().toPath());
                JOptionPane.showMessageDialog(null, "Imported Successfully!");
            }
        }));
        mainPanel.add(importButton, getSettingsConstraints(5));
    }

    private JFileChooser setupImportChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        chooser.setAcceptAllFileFilterUsed(false);
        return chooser;
    }

    private void importFromFile(Path path) {
        deleteAllHabits();
        JsonReader reader = new JsonReader(path.toString());
        try {
            HabitManager importedHabitManager = reader.read();
            HabitManagerUI.setHabitManager(importedHabitManager);
            HabitManagerUI.changeMade();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error importing file");
        }
    }

    private void setupDeleteAllHabitsButton() {
        JButton deleteAllHabits = new JButton("Delete All Habits", DELETE_ICON_HOVER);
        makeButton(deleteAllHabits, WINDOW_WIDTH - SIDE_BAR_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        deleteAllHabits.addActionListener(e -> invokeLater(() -> {
            int response = JOptionPane.showConfirmDialog(
                    null, "Are you sure you want to delete all habits?", "Delete All Habits",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                deleteAllHabits();
                HabitManagerUI.changeMade();
                JOptionPane.showMessageDialog(null, "All habits deleted successfully");
            }
        }));
        mainPanel.add(deleteAllHabits, getSettingsConstraints(6));
    }

    private void deleteAllHabits() {
        int size = habitManager.getSize();
        for (int i = 0; i < size; i++) {
            habitManager.deleteHabit(habitManager.getHabits().get(0));
        }
    }

    private void setupEmptySpace() {
        JPanel emptySpace = new JPanel();
        emptySpace.setBackground(APP_COLOUR);
        emptySpace.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, PADDING));
        mainPanel.add(emptySpace, getEmptySpaceConstraints());
    }

    private GridBagConstraints getTitleConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, PADDING * 2, 0);
        return constraints;
    }

    private GridBagConstraints getSettingsConstraints(int index) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = index;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    private GridBagConstraints getEmptySpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 1;
        return constraints;
    }
}