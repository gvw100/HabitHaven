package ui;

import model.Habit;
import model.HabitManager;
import model.Period;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.Clock;

import static ui.Constants.*;

public class CreateHabitUI extends JPanel {
    private JTextField habitName;
    private JPanel habitDescriptionPanel;
    private JTextArea habitDescription;
    private JComboBox<String> periodBox;
    private JComboBox<String> frequencyBox;
    private JComboBox<String> notificationBox;
    private JButton createHabitButton;

    private HabitManager habitManager;
    private HabitManagerUI habitManagerUI;

    public CreateHabitUI(HabitManager habitManager, HabitManagerUI habitManagerUI) {
        this.habitManager = habitManager;
        this.habitManagerUI = habitManagerUI;
        setupCreateHabit();
    }

    private void setupCreateHabit() {
        setupPanel();
    }

    private void setupPanel() {
        setBackground(APP_COLOUR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setupHabitName();
        setupHabitDescription();
        setupPeriodBox();
        setupFrequencyBox();
        setupNotificationBox();
        setupCreateHabitButton();
    }

    private void setupHabitName() {
        habitName = new JTextField();
        habitName.setFont(HUGE_FONT);
        habitName.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, TEXT_FIELD_HEIGHT * 2));
        habitName.setCaretColor(FONT_COLOUR);
        habitName.setBackground(APP_COLOUR);
        habitName.setForeground(FONT_COLOUR);
        habitName.setAlignmentX(Component.CENTER_ALIGNMENT);
        habitName.setText("Enter Habit Name");
        setupNameListener();
        add(habitName);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
    }
    
    private void setupNameListener() {
        setupNameFocusListener();
        setupNameDocumentListener();
    }

    private void setupNameFocusListener() {
        habitName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (habitName.getText().equals("Enter Habit Name")) {
                    habitName.selectAll();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (habitName.getText().length() == 0) {
                    habitName.setText("Enter Habit Name");
                }
            }
        });
    }

    private void setupNameDocumentListener() {
        habitName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkNameText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkNameText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkNameText();
            }
        });
    }

    private void checkNameText() {
        Runnable runnable = () -> {
            if (habitName.getText().length() > MAX_HABIT_NAME_LENGTH) {
                habitName.setText(habitName.getText().substring(0, MAX_HABIT_NAME_LENGTH));
            }
            if (habitName.getText().length() == 0) {
                habitName.setText("Enter Habit Name");
                habitName.selectAll();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    private void setupHabitDescription() {
        habitDescriptionPanel = new JPanel();
        habitDescriptionPanel.setLayout(new GridLayout(1, 1));
        habitDescriptionPanel.setLayout(new BoxLayout(habitDescriptionPanel, BoxLayout.Y_AXIS));
        habitDescriptionPanel.setBackground(APP_COLOUR);
        habitDescription = new JTextArea();
        habitDescription.setFont(SMALL_FONT);
        habitDescription.setLineWrap(true);
        habitDescription.setWrapStyleWord(true);
        habitDescription.setBackground(APP_COLOUR);
        habitDescription.setForeground(FONT_COLOUR);
        habitDescription.setBorder(BorderFactory.createLineBorder(FONT_COLOUR));
        habitDescription.setCaretColor(FONT_COLOUR);
        habitDescriptionPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, TEXT_FIELD_HEIGHT * 3));
        habitDescription.setText("Description (optional)");
        habitDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupDescriptionListener();
        habitDescriptionPanel.add(habitDescription);
        add(habitDescriptionPanel);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    private void setupDescriptionListener() {
        setupDescriptionFocusListener();
        setupDescriptionDocumentListener();
    }

    private void setupDescriptionFocusListener() {
        habitDescription.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (habitDescription.getText().equals("Description (optional)")) {
                    habitDescription.selectAll();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (habitDescription.getText().length() == 0) {
                    habitDescription.setText("Description (optional)");
                }
            }
        });
    }

    private void setupDescriptionDocumentListener() {
        habitDescription.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkDescriptionText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkDescriptionText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkDescriptionText();
            }
        });
    }

    private void checkDescriptionText() {
        Runnable runnable = () -> {
            if (habitDescription.getText().length() > MAX_DESCRIPTION_LENGTH) {
                habitDescription.setText(habitDescription.getText().substring(0, MAX_DESCRIPTION_LENGTH));
            }
            if (habitDescription.getText().length() == 0) {
                habitDescription.setText("Description (optional)");
                habitDescription.selectAll();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    private void setupPeriodBox() {
        String[] periods = {"Period", "Daily", "Weekly", "Monthly"};
        periodBox = new JComboBox<>(periods);
        periodBox.setFont(MEDIUM_FONT);
        periodBox.setBackground(APP_COLOUR);
        periodBox.setForeground(FONT_COLOUR);
        periodBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupPeriodListener();
        add(periodBox);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    private void setupPeriodListener() {
        periodBox.addActionListener(e -> {
            if (periodBox.getSelectedIndex() != 0) {
                checkValidHabit();
            } else {
                createHabitButton.setEnabled(false);
            }
        });
    }

    private void setupFrequencyBox() {
        String[] frequencyOptions = new String[MAX_FREQUENCY + 1];
        frequencyOptions[0] = "Frequency";
        for (int i = 0; i < MAX_FREQUENCY; i++) {
            frequencyOptions[i + 1] = Integer.toString(i + 1);
        }
        frequencyBox = new JComboBox<>(frequencyOptions);
        frequencyBox.setFont(MEDIUM_FONT);
        frequencyBox.setBackground(APP_COLOUR);
        frequencyBox.setForeground(FONT_COLOUR);
        frequencyBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupFrequencyListener();
        add(frequencyBox);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    private void setupFrequencyListener() {
        frequencyBox.addActionListener(e -> {
            if (frequencyBox.getSelectedIndex() != 0) {
                checkValidHabit();
            } else {
                createHabitButton.setEnabled(false);
            }
        });
    }

    private void setupNotificationBox() {
        notificationBox = new JComboBox<>(new String[]{"Enable notifications?", "Yes", "No"});
        notificationBox.setFont(MEDIUM_FONT);
        notificationBox.setBackground(APP_COLOUR);
        notificationBox.setForeground(FONT_COLOUR);
        notificationBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupNotificationListener();
        add(notificationBox);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    private void setupNotificationListener() {
        notificationBox.addActionListener(e -> {
            if (notificationBox.getSelectedIndex() != 0) {
                checkValidHabit();
            } else {
                createHabitButton.setEnabled(false);
            }
        });
    }
    
    private void checkValidHabit() {
        if (periodBox.getSelectedIndex() != 0 && frequencyBox.getSelectedIndex() != 0
                && notificationBox.getSelectedIndex() != 0) {
            createHabitButton.setEnabled(true);
        }
    }

    private void setupCreateHabitButton() {
        createHabitButton = new JButton("Create Habit");
        makeButton(createHabitButton, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, BIG_FONT);
        createHabitButton.setEnabled(false);
        setupCreateHabitListener();
        add(createHabitButton);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
    }

    private void setupCreateHabitListener() {
        createHabitButton.addActionListener(e -> {
            String name = habitName.getText();
            String description = habitDescription.getText();
            Period period = Period.valueOf(((String) periodBox.getSelectedItem()).toUpperCase());
            int frequency = Integer.parseInt((String) frequencyBox.getSelectedItem());
            boolean notifyEnabled = notificationBox.getSelectedIndex() == 1;
            Habit habit = new Habit(name, description, period, frequency, notifyEnabled, Clock.systemDefaultZone());
            habitManager.addHabit(habit);
            HabitManagerUI.setIsSaved(false);
            habitManagerUI.toHabitList();
        });
    }
}
