package ui.card;

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

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Represents the JPanel for the create habit screen
public class CreateHabitUI extends JPanel {
    private JTextField habitName;
    private JTextArea habitDescription;
    private JComboBox<String> periodBox;
    private JComboBox<String> frequencyBox;
    private JComboBox<String> notificationBox;
    private JButton createHabitButton;

    private HabitManager habitManager;
    private HabitManagerUI habitManagerUI;

    // EFFECTS: constructs JPanel for create habit screen
    public CreateHabitUI(HabitManager habitManager, HabitManagerUI habitManagerUI) {
        this.habitManager = habitManager;
        this.habitManagerUI = habitManagerUI;
        setupPanel();
    }

    // MODIFIES: this
    // EFFECTS: setups create habit screen panel
    private void setupPanel() {
        setBackground(APP_COLOUR);
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setupHabitName();
        setupHabitDescription();
        setupPeriodBox();
        setupFrequencyBox();
        setupNotificationBox();
        setupCreateHabitButton();
    }

    // MODIFIES: this
    // EFFECTS: setups habitName text field, adds to create habit panel
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
        add(habitName, getCreateHabitConstraints(0));
    }

    // MODIFIES: this
    // EFFECTS: setups listeners for name text field
    private void setupNameListener() {
        setupNameFocusListener();
        setupNameDocumentListener();
    }

    // MODIFIES: this
    // EFFECTS: setups focus listeners for name text field
    private void setupNameFocusListener() {
        habitName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                invokeLater(() -> {
                    if (habitName.getText().equals("Enter Habit Name")) {
                        habitName.selectAll();
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent e) {
                invokeLater(() -> {
                    if (habitName.getText().isBlank()) {
                        habitName.setText("Enter Habit Name");
                    }
                });
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups document listeners for name text field
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

    // MODIFIES: this
    // EFFECTS: if text length > MAX_HABIT_NAME_LENGTH, set habit name text field to substring
    //          of length MAX_HABIT_NAME_LENGTH,
    //          if text is blank, replace text with "Enter Habit Name" and select all
    private void checkNameText() {
        invokeLater(() -> {
            if (habitName.getText().length() > MAX_HABIT_NAME_LENGTH) {
                habitName.setText(habitName.getText().substring(0, MAX_HABIT_NAME_LENGTH));
            }
            if (habitName.getText().isBlank()) {
                habitName.setText("Enter Habit Name");
                habitName.selectAll();
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups habit description text area
    private void setupHabitDescription() {
        JPanel habitDescriptionPanel = new JPanel();
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
        habitDescriptionPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, TEXT_FIELD_HEIGHT * 5));
        habitDescription.setText("Description (optional)");
        habitDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupDescriptionListener();
        habitDescriptionPanel.add(habitDescription);
        add(habitDescriptionPanel, getCreateHabitConstraints(1));
    }

    // MODIFIES: this
    // EFFECTS: setups description text area listeners
    private void setupDescriptionListener() {
        setupDescriptionFocusListener();
        setupDescriptionDocumentListener();
    }

    // MODIFIES: this
    // EFFECTS: adds focus listener to description text area
    private void setupDescriptionFocusListener() {
        habitDescription.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                invokeLater(() -> {
                    if (habitDescription.getText().equals("Description (optional)")) {
                        habitDescription.selectAll();
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent e) {
                invokeLater(() -> {
                    if (habitDescription.getText().isBlank()) {
                        habitDescription.setText("Description (optional)");
                    }
                });
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: adds document listener to description text area
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

    // MODIFIES: this
    // EFFECTS: if length > MAX_DESCRIPTION_LENGTH, set text area text to the substring of
    //          length MAX_DESCRIPTION_LENGTH,
    //          if text area text is blank, set text to "Description (optional)" and select all
    private void checkDescriptionText() {
        invokeLater(() -> {
            if (habitDescription.getText().length() > MAX_DESCRIPTION_LENGTH) {
                habitDescription.setText(habitDescription.getText().substring(0, MAX_DESCRIPTION_LENGTH));
            }
            if (habitDescription.getText().isBlank()) {
                habitDescription.setText("Description (optional)");
                habitDescription.selectAll();
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups period selection combo box
    private void setupPeriodBox() {
        String[] periods = {"Period", "Daily", "Weekly", "Monthly"};
        periodBox = new JComboBox<>(periods);
        periodBox.setFont(MEDIUM_FONT);
        periodBox.setBackground(APP_COLOUR);
        periodBox.setForeground(FONT_COLOUR);
        periodBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupPeriodListener();
        add(periodBox, getCreateHabitConstraints(2));
    }

    // MODIFIES: this
    // EFFECTS: adds listener to period combo box
    private void setupPeriodListener() {
        periodBox.addActionListener(e -> invokeLater(() -> {
            if (periodBox.getSelectedIndex() != 0) {
                checkValidHabit();
            } else {
                createHabitButton.setEnabled(false);
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: setups frequency selection combo box
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
        add(frequencyBox, getCreateHabitConstraints(3));
    }

    // MODIFIES: this
    // EFFECTS: adds listener to frequency combo box
    private void setupFrequencyListener() {
        frequencyBox.addActionListener(e -> invokeLater(() -> {
            if (frequencyBox.getSelectedIndex() != 0) {
                checkValidHabit();
            } else {
                createHabitButton.setEnabled(false);
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: setups notification selection combo box
    private void setupNotificationBox() {
        notificationBox = new JComboBox<>(new String[]{"Enable notifications?", "Yes", "No"});
        notificationBox.setFont(MEDIUM_FONT);
        notificationBox.setBackground(APP_COLOUR);
        notificationBox.setForeground(FONT_COLOUR);
        notificationBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupNotificationListener();
        add(notificationBox, getCreateHabitConstraints(4));
    }

    // MODIFIES: this
    // EFFECTS: adds listener to notification combo box
    private void setupNotificationListener() {
        notificationBox.addActionListener(e -> invokeLater(() -> {
            if (notificationBox.getSelectedIndex() != 0) {
                checkValidHabit();
            } else {
                createHabitButton.setEnabled(false);
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: if none of the combo boxes have selected index of 0, enable the create habit button
    private void checkValidHabit() {
        if (periodBox.getSelectedIndex() != 0 && frequencyBox.getSelectedIndex() != 0
                && notificationBox.getSelectedIndex() != 0) {
            createHabitButton.setEnabled(true);
        }
    }

    // MODIFIES: this
    // EFFECTS: setups create habit button
    private void setupCreateHabitButton() {
        createHabitButton = new JButton("Create Habit");
        makeButton(createHabitButton, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, BIG_FONT);
        createHabitButton.setEnabled(false);
        setupCreateHabitListener();
        add(createHabitButton, getCreateHabitConstraints(5));
    }

    // MODIFIES: this
    // EFFECTS: adds listener to the create habit button
    private void setupCreateHabitListener() {
        createHabitButton.addActionListener(e -> invokeLater(() -> {
            String name = habitName.getText();
            String description = habitDescription.getText();
            Period period = getPeriod();
            int frequency = frequencyBox.getSelectedIndex();
            boolean notifyEnabled = notificationBox.getSelectedIndex() == 1;
            Habit habit = new Habit(name, description, period, frequency, notifyEnabled, Clock.systemDefaultZone());
            habitManager.addHabit(habit);
            HabitManagerUI.changeMade();
            habitManagerUI.toHabitList();
        }));
    }

    // EFFECTS: gets the period based on the selected index of periodBox
    private Period getPeriod() {
        switch (periodBox.getSelectedIndex()) {
            case 1:
                return Period.DAILY;
            case 2:
                return Period.WEEKLY;
            default:
                return Period.MONTHLY;
        }
    }

    // EFFECTS: returns grid bag constraints of component at given index
    private GridBagConstraints getCreateHabitConstraints(int index) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = index;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }
}
