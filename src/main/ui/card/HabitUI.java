package ui.card;

import javafx.util.Pair;
import model.Habit;
import model.Period;
import model.achievement.Achievement;
import model.achievement.AchievementManager;
import ui.AchievementToast;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Represents the JPanel for a specific habit, consists of habit tab, statistics tab, notifications tab, and
// achievements tab
public class HabitUI extends JPanel {
    private Habit habit;
    private AchievementToast achievementToast;
    private JTabbedPane tabbedPane;
    private JPanel habitPanel;
    private JPanel contentsPanel;
    private HabitStatisticsUI habitStatsPanel;
    private HabitRemindersUI habitRemindersPanel;
    private AchievementsUI achievementsPanel;

    private JTextField habitName;
    private JPanel habitDescription;
    private JTextArea habitDescriptionArea;
    private JPanel habitSuccesses;
    private JLabel habitNumSuccess;
    private JButton incrementSuccess;
    private JButton decrementSuccess;
    private JPanel frequencyPanel;
    private JLabel habitFrequency;
    private JButton changeFrequency;
    private JPanel periodPanel;
    private JLabel habitPeriod;
    private JButton changePeriod;


    // EFFECTS: initializes habit JPanel
    public HabitUI(Habit habit, AchievementToast achievementToast) {
        this.habit = habit;
        this.achievementToast = achievementToast;
        UIManager.put("TabbedPane.selected", APP_COLOUR.brighter().brighter().brighter());
        tabbedPane = new JTabbedPane();
        setupPanels();
    }

    // MODIFIES: this
    // EFFECTS: setups panels in the Habit JPanel
    private void setupPanels() {
        setBackground(APP_COLOUR);
        setLayout(new GridLayout(1, 1));
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane.setBackground(APP_COLOUR);
        tabbedPane.setForeground(FONT_COLOUR);
        tabbedPane.setFont(MEDIUM_FONT);
        tabbedPane.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setupHabitPanel();
        setupHabitStatsPanel();
        setupHabitRemindersPanel();
        setupAchievementsPanel();
        add(tabbedPane);
    }

    // MODIFIES: this
    // EFFECTS: setups habit tab of the tabbed pane
    private void setupHabitPanel() {
        habitPanel = new JPanel();
        habitPanel.setLayout(new BoxLayout(habitPanel, BoxLayout.Y_AXIS));
        habitPanel.setBackground(APP_COLOUR);
        habitPanel.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setupHabitName();
        setupHabitDescription();
        setupHabitSuccessFrequencyPeriod();
        habitPanel.add(contentsPanel);
        tabbedPane.addTab("Habit", HABIT_ICON, habitPanel, "View your habit");
    }

    // MODIFIES: this
    // EFFECTS: setups habit name field in the habit tab
    private void setupHabitName() {
        setupNameField();
        setupNameDocumentListener();
        setupNameFocusListener();
        habitPanel.add(habitName);
        habitPanel.add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    // MODIFIES: this
    // EFFECTS: setups document listener for the name text field
    private void setupNameDocumentListener() {
        habitName.getDocument().addDocumentListener(new DocumentListener() {
            // MODIFIES: this
            // EFFECTS: sets new habit name
            @Override
            public void changedUpdate(DocumentEvent e) {
                setNewName();
            }

            // MODIFIES: this
            // EFFECTS: sets new habit name
            @Override
            public void removeUpdate(DocumentEvent e) {
                setNewName();
            }

            // MODIFIES: this
            // EFFECTS: sets new habit name
            @Override
            public void insertUpdate(DocumentEvent e) {
                setNewName();
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups focus listener for the name text field
    private void setupNameFocusListener() {
        habitName.addFocusListener(new FocusAdapter() {
            // MODIFIES: this
            // EFFECTS: if textField contains filler text, then select all
            @Override
            public void focusGained(FocusEvent e) {
                invokeLater(() -> {
                    if (habitName.getText().equals("New Habit")) {
                        habitName.selectAll();
                    }
                });
            }

            // MODIFIES: this
            // EFFECTS: if textField is blank, then set text to filler text
            @Override
            public void focusLost(FocusEvent e) {
                invokeLater(() -> {
                    if (habitName.getText().isBlank()) {
                        habitName.setText("New Habit");
                    }
                });
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups up name text field
    private void setupNameField() {
        habitName = new JTextField(habit.getName());
        habitName.setFont(HUGE_FONT);
        habitName.setEditable(true);
        habitName.setBackground(APP_COLOUR);
        habitName.setForeground(FONT_COLOUR);
        habitName.setCaretColor(FONT_COLOUR);
        habitName.setHorizontalAlignment(JTextField.CENTER);
        habitName.setAlignmentX(Component.CENTER_ALIGNMENT);
        habitName.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, TEXT_FIELD_HEIGHT * 2));
    }

    // MODIFIES: this
    // EFFECTS: if inputted text length > MAX_HABIT_NAME_LENGTH, set text to the appropriate substring
    //          of length MAX_HABIT_NAME_LENGTH,
    //          if inputted text is blank, then set text to "New Habit" and select all,
    //          sets habitName to the processed inputted text
    private void setNewName() {
        invokeLater(() -> {
            if (habitName.getText().length() > MAX_HABIT_NAME_LENGTH) {
                habitName.setText(habitName.getText().substring(0, MAX_HABIT_NAME_LENGTH));
            }
            if (habitName.getText().isBlank()) {
                habitName.setText("New Habit");
                habitName.selectAll();
            }
            habit.setName(habitName.getText());
            HabitManagerUI.changeMade();
            updateOtherPanels();
        });
    }

    // MODIFIES: this
    // EFFECTS: setups habit description text area
    private void setupHabitDescription() {
        contentsPanel = new JPanel();
        contentsPanel.setLayout(new BoxLayout(contentsPanel, BoxLayout.Y_AXIS));
        contentsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentsPanel.setBackground(APP_COLOUR);
        initializeHabitDescription();
        setupDescriptionDocumentListener();
        setupDescriptionFocusListener();
        contentsPanel.add(habitDescription);
        contentsPanel.add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    // MODIFIES: this
    // EFFECTS: setups description document listener
    private void setupDescriptionDocumentListener() {
        habitDescriptionArea.getDocument().addDocumentListener(new DocumentListener() {
            // MODIFIES: this
            // EFFECTS: sets new habit description
            @Override
            public void changedUpdate(DocumentEvent e) {
                setNewDescription();
            }

            // MODIFIES: this
            // EFFECTS: sets new habit description
            @Override
            public void removeUpdate(DocumentEvent e) {
                setNewDescription();
            }

            // MODIFIES: this
            // EFFECTS: sets new habit description
            @Override
            public void insertUpdate(DocumentEvent e) {
                setNewDescription();
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups up description focus listener
    private void setupDescriptionFocusListener() {
        habitDescriptionArea.addFocusListener(new FocusAdapter() {
            // MODIFIES: this
            // EFFECTS: if habitDescriptionArea contains filler text, then select all
            @Override
            public void focusGained(FocusEvent e) {
                invokeLater(() -> {
                    if (habitDescriptionArea.getText().equals("Description (optional)")) {
                        habitDescriptionArea.selectAll();
                    }
                });
            }

            // MODIFIES: this
            // EFFECTS: if habitDescriptionArea is blank, replace with filler text
            @Override
            public void focusLost(FocusEvent e) {
                invokeLater(() -> {
                    if (habitDescriptionArea.getText().isBlank()) {
                        habitDescriptionArea.setText("Description (optional)");
                    }
                });
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: sets up habit description area properties
    private void initializeHabitDescription() {
        habitDescription = setupHorizontalPanel();
        JLabel descriptionLabel = getDescriptionLabel();
        habitDescription.add(descriptionLabel);
        habitDescriptionArea = habit.getDescription().length() == 0
                ? new JTextArea("Description (optional)") : new JTextArea(habit.getDescription());
        habitDescriptionArea.setFont(SMALL_FONT);
        habitDescriptionArea.setEditable(true);
        habitDescriptionArea.setBackground(APP_COLOUR);
        habitDescriptionArea.setForeground(FONT_COLOUR);
        habitDescriptionArea.setBorder(BorderFactory.createLineBorder(FONT_COLOUR));
        habitDescriptionArea.setCaretColor(FONT_COLOUR);
        habitDescriptionArea.setLineWrap(true);
        habitDescriptionArea.setWrapStyleWord(true);
        habitDescriptionArea.setAlignmentY(Component.TOP_ALIGNMENT);
        habitDescription.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, TEXT_FIELD_HEIGHT * 7));
        habitDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
        habitDescription.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        habitDescription.add(habitDescriptionArea);
    }

    // EFFECTS: returns description label
    private JLabel getDescriptionLabel() {
        JLabel descriptionLabel = setupLabel("  Description: ");
        descriptionLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        return descriptionLabel;
    }

    // MODIFIES: this
    // EFFECTS: sets the habit description to the value in the text area. Length restricted to MAX_DESCRIPTION_LENGTH
    //          and blank descriptions are not allowed
    private void setNewDescription() {
        invokeLater(() -> {
            if (habitDescriptionArea.getText().length() > MAX_DESCRIPTION_LENGTH) {
                habitDescriptionArea.setText(habitDescriptionArea.getText().substring(0, MAX_DESCRIPTION_LENGTH));
            }
            if (habitDescriptionArea.getText().isBlank()) {
                habitDescriptionArea.setText("Description (optional)");
                habitDescriptionArea.selectAll();
            }
            habit.setDescription(habitDescriptionArea.getText());
            HabitManagerUI.changeMade();
            updateOtherPanels();
        });
    }

    // MODIFIES: this
    // EFFECTS: setups numSuccess, frequency, and period panels
    private void setupHabitSuccessFrequencyPeriod() {
        setupNumSuccessField();
        setupIncrementDecrementButtons();
        setupFrequencyField();
        setupPeriodField();
        setupFrequencyPeriodButtons();
        setupSuccessPanel();
        setupFrequencyPanel();
        setupPeriodPanel();
        contentsPanel.add(habitSuccesses);
        contentsPanel.add(Box.createRigidArea(new Dimension(0, PADDING)));
        contentsPanel.add(frequencyPanel);
        contentsPanel.add(Box.createRigidArea(new Dimension(0, PADDING)));
        contentsPanel.add(periodPanel);
        contentsPanel.add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
    }

    // MODIFIES: this
    // EFFECTS: setups numSuccess panel
    private void setupSuccessPanel() {
        habitSuccesses = setupHorizontalPanel();
        habitSuccesses.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel successes = setupLabel("  Successes: ");
        habitSuccesses.add(successes);
        habitSuccesses.add(decrementSuccess);
        habitSuccesses.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        habitSuccesses.add(habitNumSuccess);
        habitSuccesses.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        habitSuccesses.add(incrementSuccess);
    }

    // MODIFIES: this
    // EFFECTS: setups frequency panel
    private void setupFrequencyPanel() {
        frequencyPanel = setupHorizontalPanel();
        frequencyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel frequency = setupLabel("  Frequency: ");
        frequencyPanel.add(frequency);
        frequencyPanel.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        frequencyPanel.add(habitFrequency);
        frequencyPanel.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        frequencyPanel.add(changeFrequency);
    }

    // MODIFIES: this
    // EFFECTS: setups period panel
    private void setupPeriodPanel() {
        JLabel period = setupLabel("  Period: ");
        periodPanel = setupHorizontalPanel();
        periodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        periodPanel.add(period);
        periodPanel.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        periodPanel.add(habitPeriod);
        periodPanel.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        periodPanel.add(changePeriod);
        periodPanel.add(Box.createRigidArea(new Dimension(PADDING, 0)));
    }

    // EFFECTS: returns a panel with a horizontal box layout
    private JPanel setupHorizontalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(APP_COLOUR);
        return panel;
    }

    // MODIFIES: this
    // EFFECTS: setups numSuccess label
    private void setupNumSuccessField() {
        habitNumSuccess = new JLabel(String.valueOf(habit.getNumSuccess()));
        habitNumSuccess.setFont(MEDIUM_FONT);
        habitNumSuccess.setBackground(APP_COLOUR);
        habitNumSuccess.setForeground(FONT_COLOUR);
        habitNumSuccess.setAlignmentY(Component.CENTER_ALIGNMENT);
    }

    // MODIFIES: this
    // EFFECTS: setups frequency label
    private void setupFrequencyField() {
        habitFrequency = new JLabel(String.valueOf(habit.getFrequency()));
        habitFrequency.setFont(MEDIUM_FONT);
        habitFrequency.setBackground(APP_COLOUR);
        habitFrequency.setForeground(FONT_COLOUR);
        habitFrequency.setHorizontalAlignment(JTextField.CENTER);
        habitFrequency.setPreferredSize(new Dimension(19, TEXT_FIELD_HEIGHT));
    }

    // MODIFIES: this
    // EFFECTS: setups increment and decrement buttons for numSuccess
    private void setupIncrementDecrementButtons() {
        incrementSuccess = new JButton("+");
        decrementSuccess = new JButton("-");
        makeButton(incrementSuccess, 50, 50, MEDIUM_FONT);
        makeButton(decrementSuccess, 50, 50, MEDIUM_FONT);
        setupIncrementListener();
        setupDecrementListener();
    }

    // MODIFIES: this
    // EFFECTS: setups listener for the increment button
    private void setupIncrementListener() {
        // MODIFIES: this
        // EFFECTS: checks whether any new achievements were earned,
        //          adds to achievementToast queue, updates other tabs
        incrementSuccess.addActionListener(e -> invokeLater(() -> {
            List<Achievement> current = habit.getAchievements();
            if (habit.finishHabit()) {
                habitNumSuccess.setText(String.valueOf(habit.getNumSuccess()));
                HabitManagerUI.changeMade();
                List<Achievement> newlyAchieved = AchievementManager.getNewlyAchieved(
                        current, habit.getHabitStats(), habit.getPeriod());
                for (Achievement achievement : newlyAchieved) {
                    achievementToast.add(new Pair<>(habit.getName(), achievement));
                }
                updateOtherPanels();
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: setups decrement button listener, updates numSuccess label and entire habit panel accordingly
    private void setupDecrementListener() {
        // MODIFIES: this
        // EFFECTS: decrement numSuccess, update numSuccess label and rest of habit panel
        decrementSuccess.addActionListener(e -> invokeLater(() -> {
            if (habit.undoFinishHabit()) {
                habitNumSuccess.setText(String.valueOf(habit.getNumSuccess()));
                HabitManagerUI.changeMade();
                updateOtherPanels();
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: setups change frequency and period buttons
    private void setupFrequencyPeriodButtons() {
        changeFrequency = new JButton("Change");
        changePeriod = new JButton("Change");
        makeButton(changeFrequency, 150, 50, MEDIUM_FONT);
        makeButton(changePeriod, 150, 50, MEDIUM_FONT);
        setupFrequencyPeriodListeners();
    }

    // MODIFIES: this
    // EFFECTS: setups listeners for the change period and change frequency buttons
    private void setupFrequencyPeriodListeners() {
        // MODIFIES: this
        // EFFECTS: confirms change frequency action and prompts user to choose new frequency
        changeFrequency.addActionListener(e -> invokeLater(() -> {
            String message = "Changing frequency will reset habit progress, statistics, and achievements."
                    + " Are you sure you want to continue?";
            if (JOptionPane.showConfirmDialog(null, message, "Change Frequency", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                changeFrequency();
            }
        }));
        // MODIFIES: this
        // EFFECTS: confirms change period action and prompts user to choose new period
        changePeriod.addActionListener(e -> invokeLater(() -> {
            String message = "Changing period will reset habit progress, statistics, and achievements."
                    + " Are you sure you want to continue?";
            if (JOptionPane.showConfirmDialog(null, message, "Change Period", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                changePeriod();
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: prompts user to choose a new frequency
    private void changeFrequency() {
        Integer[] options = new Integer[MAX_FREQUENCY];
        for (int i = 0; i < MAX_FREQUENCY; i++) {
            options[i] = i + 1;
        }
        int current = habit.getFrequency();
        Object choice = (JOptionPane.showInputDialog(null, "Choose a new frequency", "Change Frequency",
                JOptionPane.QUESTION_MESSAGE, null, options, options[current - 1]));
        if (choice != null) {
            if (habit.setFrequency((int) choice)) {
                HabitManagerUI.changeMade();
            }
            updateHabitUI();
        }
    }

    // MODIFIES: this
    // EFFECTS: prompts users to choose a new period
    private void changePeriod() {
        String[] options = {"Daily", "Weekly", "Monthly"};
        int current = habit.getPeriod().ordinal();
        Object choice = JOptionPane.showInputDialog(null, "Choose a new period", "Change Period",
                JOptionPane.QUESTION_MESSAGE, null, options, options[current]);
        if (choice != null) {
            if (habit.setPeriod(Period.valueOf((choice.toString().toUpperCase())))) {
                HabitManagerUI.changeMade();
                habitRemindersPanel = getHabitRemindersUI();
                tabbedPane.setComponentAt(2, habitRemindersPanel);
                updateHabitUI();
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the entire habit panel (including other tabs)
    private void updateHabitUI() {
        String lower = habit.getPeriod().toString().toLowerCase();
        String capital = lower.substring(0, 1).toUpperCase() + lower.substring(1);
        habitPeriod.setText(capital);
        habitNumSuccess.setText(String.valueOf(habit.getNumSuccess()));
        habitFrequency.setText(String.valueOf(habit.getFrequency()));
        updateOtherPanels();
    }

    // MODIFIES: this
    // EFFECTS: updates all other tabs in the habit panel
    private void updateOtherPanels() {
        habitStatsPanel.updateStatsUI();
        habitRemindersPanel.updateRemindersUI();
        achievementsPanel.updateAchievementsUI();
    }

    // MODIFIES: this
    // EFFECTS: setups period label
    private void setupPeriodField() {
        String lower = habit.getPeriod().toString().toLowerCase();
        String capital = lower.substring(0, 1).toUpperCase() + lower.substring(1);
        habitPeriod = new JLabel(capital);
        habitPeriod.setFont(MEDIUM_FONT);
        habitPeriod.setBackground(APP_COLOUR);
        habitPeriod.setForeground(FONT_COLOUR);
        habitPeriod.setHorizontalAlignment(JTextField.CENTER);
        habitPeriod.setPreferredSize(new Dimension(18, TEXT_FIELD_HEIGHT));
    }

    // MODIFIES: this
    // EFFECTS: setups a new label with the given text
    private JLabel setupLabel(String label) {
        JLabel newLabel = new JLabel(label);
        newLabel.setFont(MEDIUM_FONT);
        newLabel.setBackground(APP_COLOUR);
        newLabel.setForeground(FONT_COLOUR);
        return newLabel;
    }

    // MODIFIES: this
    // EFFECTS: setups habit statistics tab
    private void setupHabitStatsPanel() {
        habitStatsPanel = new HabitStatisticsUI(habit);
        tabbedPane.addTab("Statistics", STATS_ICON, habitStatsPanel, "View your statistics");
    }

    // MODIFIES: this
    // EFFECTS: setups habit reminders tab
    private void setupHabitRemindersPanel() {
        habitRemindersPanel = getHabitRemindersUI();
        tabbedPane.addTab("Notifications", BELL_ON, habitRemindersPanel, "Set your notifications");
    }

    // EFFECTS: gets the appropriate habit reminder panel depending on habit period
    private HabitRemindersUI getHabitRemindersUI() {
        switch (habit.getPeriod()) {
            case DAILY:
                return new DailyRemindersUI(habit);
            case WEEKLY:
                return new WeeklyRemindersUI(habit);
            default:
                return new MonthlyRemindersUI(habit);
        }
    }

    // MODIFIES: this
    // EFFECTS: setups achievement tab
    private void setupAchievementsPanel() {
        achievementsPanel = new AchievementsUI(habit);
        tabbedPane.addTab("Achievements", TROPHY_ICON, achievementsPanel, "View your achievements");
    }
}