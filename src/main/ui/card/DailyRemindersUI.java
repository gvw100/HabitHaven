package ui.card;

import javafx.util.Pair;
import model.Habit;

import javax.swing.*;

import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Represents the JPanel for the notifications tab for a daily habit
public class DailyRemindersUI extends HabitRemindersUI {
    private JPanel presetRow;
    private JComboBox<String> presetFrequency;
    private JSpinner presetStartHours;
    private JSpinner presetStartMinutes;
    private JComboBox<String> presetStartAmPm;
    private JSpinner presetEndHours;
    private JSpinner presetEndMinutes;
    private JComboBox<String> presetEndAmPm;
    private JButton presetSubmitButton;

    // EFFECTS: constructs a daily reminder panel
    public DailyRemindersUI(Habit habit) {
        super(habit);
    }

    // MODIFIES: this
    // EFFECTS: setups components in customization tab
    @Override
    protected void setupCustomizationComponents(int startingIndex) {
        setupCustomizationFrequency(MAX_FREQUENCY, "Choose specific times for your notifications!", startingIndex);
    }

    // MODIFIES: customizationPanel
    // EFFECTS: sets custom reminders based on the content in each customization row, returns whether
    //          user inputted reminders are valid
    //          if valid, set custom reminders to the current habit
    @Override
    protected boolean setCustomReminders(int frequency) {
        Set<LocalDateTime> reminders = new HashSet<>();
        for (int i = 0; i < frequency; i++) {
            JPanel row = (JPanel) customizationPanel.getComponent(i + 5);
            JSpinner hours = (JSpinner) row.getComponent(2);
            JSpinner minutes = (JSpinner) row.getComponent(4);
            commitSpinners(hours, minutes);
            JComboBox<String> amPm = (JComboBox<String>) row.getComponent(6);
            String minString = getMinuteString(minutes);
            LocalTime time = LocalTime.parse(hours.getValue() + ":" + minString + " " + amPm.getSelectedItem(),
                    DateTimeFormatter.ofPattern("h:mm a"));
            LocalDateTime reminder = LocalDateTime.of(LocalDate.now(), time);
            if (!reminders.add(reminder)) {
                return false;
            }
        }
        habit.getHabitReminder().setCustomReminders(reminders);
        return true;
    }

    // EFFECTS: returns customization row with spinners and combo boxes to select time
    @Override
    protected JPanel setupCustomizationRow(int index) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(APP_COLOUR);
        JLabel label = new JLabel("Reminder " + (index + 1));
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        row.add(label);
        row.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        JSpinner hours = new JSpinner(new SpinnerNumberModel(9, 1, 12, 1));
        setupSpinner(hours);
        row.add(hours);
        row.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        JSpinner minutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        setupSpinner(minutes);
        row.add(minutes);
        row.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        JComboBox<String> amPm = new JComboBox<>(new String[]{"AM", "PM"});
        amPm.setFont(MEDIUM_FONT);
        amPm.setBackground(APP_COLOUR);
        amPm.setForeground(FONT_COLOUR);
        row.add(amPm);
        return row;
    }

    // MODIFIES: this
    // EFFECTS: setups components in reminder list tab
    @Override
    protected void setupReminderListComponents() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        List<LocalDateTime> reminders = new ArrayList<>(habit.getHabitReminder().getReminders());
        Collections.sort(reminders);
        for (LocalDateTime reminder : reminders) {
            setupReminderListRow(reminder.format(formatter));
        }
    }

    // MODIFIES: this
    // EFFECTS: setups components in preset tab
    @Override
    protected void setupPresetComponents() {
        setupPresetLabel("Set up to 15 notifications between 2 times of your choice!");
        setupPresetFrequency();
        setupSpinners();
        setupPresetButton();
    }

    // MODIFIES: this
    // EFFECTS: setups preset frequency combo box
    private void setupPresetFrequency() {
        String[] frequencyOptions = new String[MAX_FREQUENCY + 1];
        frequencyOptions[0] = "Number of Notifications";
        for (int i = 0; i < MAX_FREQUENCY; i++) {
            frequencyOptions[i + 1] = Integer.toString(i + 1);
        }
        presetFrequency = new JComboBox<>(frequencyOptions);
        presetFrequency.setFont(MEDIUM_FONT);
        presetFrequency.setBackground(APP_COLOUR);
        presetFrequency.setForeground(FONT_COLOUR);
        setupPresetFrequencyListener();
        presetPanel.add(presetFrequency, getPresetFrequencyConstraints());
    }

    // EFFECTS: returns grid bag constraints for preset frequency combo box
    private GridBagConstraints getPresetFrequencyConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: this
    // EFFECTS: adds listener to preset frequency combo box, submit button is enabled if index != 0
    private void setupPresetFrequencyListener() {
        presetFrequency.addActionListener(e ->
                invokeLater(() -> presetSubmitButton.setEnabled(presetFrequency.getSelectedIndex() != 0)));
    }

    // MODIFIES: this
    // EFFECTS: setups spinners to select start time and end time
    private void setupSpinners() {
        presetRow = new JPanel();
        presetRow.setLayout(new BoxLayout(presetRow, BoxLayout.X_AXIS));
        presetRow.setBackground(APP_COLOUR);
        setupStartSpinner();
        setupEndSpinner();
        presetPanel.add(presetRow, getTimeInputRowConstraints(5));
    }

    // MODIFIES: this
    // EFFECTS: setups start time spinners
    private void setupStartSpinner() {
        presetStartHours = new JSpinner(new SpinnerNumberModel(9, 1, 12, 1));
        setupPresetSpinnerLabel("Start Time: ");
        setupSpinner(presetStartHours);
        presetRow.add(presetStartHours);
        presetRow.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        presetStartMinutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        setupSpinner(presetStartMinutes);
        presetRow.add(presetStartMinutes);
        presetRow.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        presetStartAmPm = new JComboBox<>(new String[]{"AM", "PM"});
        presetStartAmPm.setFont(MEDIUM_FONT);
        presetStartAmPm.setBackground(APP_COLOUR);
        presetStartAmPm.setForeground(FONT_COLOUR);
        presetRow.add(presetStartAmPm);
        presetRow.add(Box.createRigidArea(new Dimension(PADDING, 0)));
    }

    // MODIFIES: this
    // EFFECTS: setups preset spinner label and adds it to presetRow
    private void setupPresetSpinnerLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        presetRow.add(label);
        presetRow.add(Box.createRigidArea(new Dimension(PADDING, 0)));
    }

    // MODIFIES: this
    // EFFECTS: setups end time spinner
    private void setupEndSpinner() {
        presetEndHours = new JSpinner(new SpinnerNumberModel(9, 1, 12, 1));
        setupPresetSpinnerLabel("End Time: ");
        setupSpinner(presetEndHours);
        presetRow.add(presetEndHours);
        presetRow.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        presetEndMinutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        setupSpinner(presetEndMinutes);
        presetRow.add(presetEndMinutes);
        presetRow.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        presetEndAmPm = new JComboBox<>(new String[]{"AM", "PM"});
        presetEndAmPm.setFont(MEDIUM_FONT);
        presetEndAmPm.setBackground(APP_COLOUR);
        presetEndAmPm.setForeground(FONT_COLOUR);
        presetEndAmPm.setSelectedIndex(1);
        presetRow.add(presetEndAmPm);
    }

    // MODIFIES: this
    // EFFECTS: setups preset submit button
    private void setupPresetButton() {
        presetSubmitButton = new JButton("Set Notifications");
        makeButton(presetSubmitButton, WINDOW_WIDTH - SIDE_BAR_WIDTH, 50, MEDIUM_FONT);
        presetSubmitButton.setEnabled(false);
        presetSubmitButton.addActionListener(e -> invokeLater(() -> {
            commitPresetSpinners();
            if (generateReminders()) {
                HabitManagerUI.changeMade();
                JOptionPane.showMessageDialog(this, "Notifications have been generated!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                updateRemindersUI();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid preset. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }));
        presetPanel.add(presetSubmitButton, getSubmitButtonConstraints());
    }

    // MODIFIES: this
    // EFFECTS: commits changes to all preset spinners
    private void commitPresetSpinners() {
        try {
            presetStartHours.commitEdit();
            presetStartMinutes.commitEdit();
            presetEndHours.commitEdit();
            presetEndMinutes.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: generates reminders based on start time, end time, and frequency, returns whether
    //          reminder generation was successful
    private boolean generateReminders() {
        Set<LocalDateTime> reminders = new HashSet<>();
        Pair<LocalTime, LocalTime> startEnd = getStartEnd();
        LocalTime start = startEnd.getKey();
        LocalTime end = startEnd.getValue();
        if (!end.isAfter(start)) {
            return false;
        }
        int frequency = presetFrequency.getSelectedIndex();
        int lengthSeconds = end.toSecondOfDay() - start.toSecondOfDay();
        double intervalSeconds = (double) lengthSeconds / (double) frequency;
        int hours = (int) Math.floor((intervalSeconds / 3600));
        int minutes = (int) Math.round(((intervalSeconds - (hours * 3600)) / 60));
        if (hours == 0 && minutes == 0) {
            return false;
        }
        LocalDateTime reminderDateTime = LocalDateTime.of(LocalDate.now(), start);
        for (int i = 0; i < frequency; i++) {
            reminders.add(reminderDateTime);
            reminderDateTime = reminderDateTime.plusHours(hours).plusMinutes(minutes);
        }
        habit.getHabitReminder().setCustomReminders(reminders);
        return true;
    }

    // EFFECTS: returns a pair of start and end times inputted by user
    private Pair<LocalTime, LocalTime> getStartEnd() {
        String startMin;
        String endMin;
        int startMinutes = (int) presetStartMinutes.getValue();
        int endMinutes = (int) presetEndMinutes.getValue();
        if (startMinutes < 10) {
            startMin = "0" + startMinutes;
        } else {
            startMin = presetStartMinutes.getValue().toString();
        }
        if (endMinutes < 10) {
            endMin = "0" + endMinutes;
        } else {
            endMin = presetEndMinutes.getValue().toString();
        }
        LocalTime start = LocalTime.parse(presetStartHours.getValue() + ":" + startMin
                + " " + presetStartAmPm.getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a"));
        LocalTime end = LocalTime.parse(presetEndHours.getValue() + ":" + endMin
                + " " + presetEndAmPm.getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a"));
        return new Pair<>(start, end);
    }
}