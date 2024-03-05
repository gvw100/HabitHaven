package ui.panel;

import model.Habit;
import model.Period;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static ui.Constants.*;

public class HabitUI extends JPanel {
    private Habit habit;
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


    public HabitUI(Habit habit) {
        this.habit = habit;
        UIManager.put("TabbedPane.selected", APP_COLOUR.brighter().brighter().brighter());
        tabbedPane = new JTabbedPane();
        setupPanels();
    }

    private void setupPanels() {
        setBackground(APP_COLOUR);
        setLayout(new GridLayout(1, 1));
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
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

    private void setupHabitPanel() {
        habitPanel = new JPanel();
        habitPanel.setLayout(new BoxLayout(habitPanel, BoxLayout.Y_AXIS));
        habitPanel.setBackground(APP_COLOUR);
        habitPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setupHabitName();
        setupHabitDescription();
        setupHabitSuccessFrequencyPeriod();
        habitPanel.add(contentsPanel);
        tabbedPane.addTab("Habit", null, habitPanel, "View your habit");
    }

    private void setupHabitName() {
        setupNameField();
        setupNameDocumentListener();
        setupNameFocusListener();
        habitPanel.add(habitName);
        habitPanel.add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    private void setupNameDocumentListener() {
        habitName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                setNewName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setNewName();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                setNewName();
            }
        });
    }

    private void setupNameFocusListener() {
        habitName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (habitName.getText().equals("New Habit")) {
                    habitName.selectAll();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (habitName.getText().isBlank()) {
                    habitName.setText("New Habit");
                    habitName.selectAll();
                }
            }
        });
    }

    private void setupNameField() {
        habitName = new JTextField(habit.getName());
        habitName.setFont(HUGE_FONT);
        habitName.setEditable(true);
        habitName.setBackground(APP_COLOUR);
        habitName.setForeground(FONT_COLOUR);
        habitName.setCaretColor(FONT_COLOUR);
        habitName.setHorizontalAlignment(JTextField.CENTER);
        habitName.setAlignmentX(Component.CENTER_ALIGNMENT);
        habitName.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, TEXT_FIELD_HEIGHT * 2));
    }

    private void setNewName() {
        Runnable runnable = () -> {
            if (habitName.getText().length() > MAX_HABIT_NAME_LENGTH) {
                habitName.setText(habitName.getText().substring(0, MAX_HABIT_NAME_LENGTH));
            }
            if (habitName.getText().isBlank()) {
                habitName.setText("New Habit");
                habitName.selectAll();
            }
            habit.setName(habitName.getText());
            HabitManagerUI.setIsSaved(false);
            updateOtherPanels();
        };
        SwingUtilities.invokeLater(runnable);
    }

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

    private void setupDescriptionDocumentListener() {
        habitDescriptionArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                setNewDescription();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setNewDescription();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                setNewDescription();
            }
        });
    }

    private void setupDescriptionFocusListener() {
        habitDescriptionArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (habitDescriptionArea.getText().equals("Description (optional)")) {
                    habitDescriptionArea.selectAll();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (habitDescriptionArea.getText().isBlank()) {
                    habitDescriptionArea.setText("Description (optional)");
                    habitDescriptionArea.selectAll();
                }
            }
        });
    }

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
        habitDescription.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, TEXT_FIELD_HEIGHT * 7));
        habitDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
        habitDescription.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        habitDescription.add(habitDescriptionArea);
    }

    private JLabel getDescriptionLabel() {
        JLabel descriptionLabel = setupLabel("  Description: ");
        descriptionLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        return descriptionLabel;
    }

    private void setNewDescription() {
        Runnable runnable = () -> {
            if (habitDescriptionArea.getText().length() > MAX_DESCRIPTION_LENGTH) {
                habitDescriptionArea.setText(habitDescriptionArea.getText().substring(0, MAX_DESCRIPTION_LENGTH));
            }
            if (habitDescriptionArea.getText().isBlank()) {
                habitDescriptionArea.setText("Description (optional)");
                habitDescriptionArea.selectAll();
            }
            habit.setDescription(habitDescriptionArea.getText());
            HabitManagerUI.setIsSaved(false);
            updateOtherPanels();
        };
        SwingUtilities.invokeLater(runnable);
    }

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
        contentsPanel.add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

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

    private void setupPeriodPanel() {
        JLabel period = setupLabel("  Period: ");
        periodPanel = setupHorizontalPanel();
        periodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        periodPanel.add(period);
        periodPanel.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        periodPanel.add(habitPeriod);
        periodPanel.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        periodPanel.add(changePeriod);
    }

    private JPanel setupHorizontalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(APP_COLOUR);
        return panel;
    }

    private void setupNumSuccessField() {
        habitNumSuccess = new JLabel(String.valueOf(habit.getNumSuccess()));
        habitNumSuccess.setFont(MEDIUM_FONT);
        habitNumSuccess.setBackground(APP_COLOUR);
        habitNumSuccess.setForeground(FONT_COLOUR);
        habitNumSuccess.setAlignmentY(Component.CENTER_ALIGNMENT);
    }

    private void setupFrequencyField() {
        habitFrequency = new JLabel(String.valueOf(habit.getFrequency()));
        habitFrequency.setFont(MEDIUM_FONT);
        habitFrequency.setBackground(APP_COLOUR);
        habitFrequency.setForeground(FONT_COLOUR);
        habitFrequency.setHorizontalAlignment(JTextField.CENTER);
        habitFrequency.setPreferredSize(new Dimension(19, TEXT_FIELD_HEIGHT));
    }

    private void setupIncrementDecrementButtons() {
        incrementSuccess = new JButton("+");
        decrementSuccess = new JButton("-");
        makeButton(incrementSuccess, 50, 50, MEDIUM_FONT);
        makeButton(decrementSuccess, 50, 50, MEDIUM_FONT);
        incrementSuccess.addActionListener(e -> {
            if (habit.finishHabit()) {
                habitNumSuccess.setText(String.valueOf(habit.getNumSuccess()));
                HabitManagerUI.setIsSaved(false);
                updateOtherPanels();
            }
        });
        decrementSuccess.addActionListener(e -> {
            if (habit.undoFinishHabit()) {
                habitNumSuccess.setText(String.valueOf(habit.getNumSuccess()));
                HabitManagerUI.setIsSaved(false);
                updateOtherPanels();
            }
        });
    }

    private void setupFrequencyPeriodButtons() {
        changeFrequency = new JButton("Change");
        changePeriod = new JButton("Change");
        makeButton(changeFrequency, 150, 50, MEDIUM_FONT);
        makeButton(changePeriod, 150, 50, MEDIUM_FONT);
        changeFrequency.addActionListener(e -> {
            String message = "Changing frequency will reset habit progress, statistics, and achievements."
                    + " Are you sure you want to continue?";
            if (JOptionPane.showConfirmDialog(null, message, "Change Frequency", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                changeFrequency();
            }

        });
        changePeriod.addActionListener(e -> {
            String message = "Changing period will reset habit progress, statistics, and achievements."
                    + " Are you sure you want to continue?";
            if (JOptionPane.showConfirmDialog(null, message, "Change Period", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                changePeriod();
            }
        });
    }

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
                HabitManagerUI.setIsSaved(false);
            }
            updateHabitUI();
        }
    }

    private void changePeriod() {
        String[] options = {"Daily", "Weekly", "Monthly"};
        int current = habit.getPeriod().ordinal();
        Object choice = JOptionPane.showInputDialog(null, "Choose a new period", "Change Period",
                JOptionPane.QUESTION_MESSAGE, null, options, options[current]);
        if (choice != null) {
            if (habit.setPeriod(Period.valueOf((choice.toString().toUpperCase())))) {
                HabitManagerUI.setIsSaved(false);
            }
            updateHabitUI();
        }
    }

    private void updateHabitUI() {
        String lower = habit.getPeriod().toString().toLowerCase();
        String capital = lower.substring(0, 1).toUpperCase() + lower.substring(1);
        habitPeriod.setText(capital);
        habitNumSuccess.setText(String.valueOf(habit.getNumSuccess()));
        habitFrequency.setText(String.valueOf(habit.getFrequency()));
        updateOtherPanels();
    }

    private void updateOtherPanels() {
        habitStatsPanel.updateStatsUI();
        habitRemindersPanel.updateRemindersUI();
        achievementsPanel.updateAchievementsUI();
    }

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

    private JLabel setupLabel(String label) {
        JLabel newLabel = new JLabel(label);
        newLabel.setFont(MEDIUM_FONT);
        newLabel.setBackground(APP_COLOUR);
        newLabel.setForeground(FONT_COLOUR);
        return newLabel;
    }

    private void setupHabitStatsPanel() {
        habitStatsPanel = new HabitStatisticsUI(habit);
        tabbedPane.addTab("In-depth Statistics", null, habitStatsPanel, "View your statistics");
    }

    private void setupHabitRemindersPanel() {
        habitRemindersPanel = new HabitRemindersUI(habit);
        tabbedPane.addTab("Notifications", null, habitRemindersPanel, "Set your notifications");
    }

    private void setupAchievementsPanel() {
        achievementsPanel = new AchievementsUI(habit);
        tabbedPane.addTab("Achievements", null, achievementsPanel, "View your achievements");
    }
}