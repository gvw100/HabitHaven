package ui.card;

import model.Habit;
import model.reminder.WeeklyReminder;

import javax.swing.*;
import java.awt.*;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Represents the JPanel for the notifications tab for a weekly habit
public class WeeklyRemindersUI extends HabitRemindersUI {
    private JComboBox<String> presetChoiceBox;
    private JPanel presetEveryDay;
    private JPanel presetWeekdays;
    private JPanel presetWeekends;

    // EFFECTS: Constructs a weekly reminder panel
    public WeeklyRemindersUI(Habit habit) {
        super(habit);
    }

    // MODIFIES: this
    // EFFECTS: setups components in preset tab
    @Override
    protected void setupPresetComponents() {
        setupPresetLabel("Set up to 15 notifications per week!");
        setupPresetChoiceBox();
        setupPresetEveryDay();
        setupPresetWeekdays();
        setupPresetWeekends();
    }

    // MODIFIES: this
    // EFFECTS: setups presetChoiceBox and adds it to presetPanel
    private void setupPresetChoiceBox() {
        String[] presetOptions = {"Which days would you like to be notified?",
                "Every Day", "Every Weekday", "Every Weekend"};
        presetChoiceBox = new JComboBox<>(presetOptions);
        presetChoiceBox.setFont(MEDIUM_FONT);
        presetChoiceBox.setBackground(APP_COLOUR);
        presetChoiceBox.setForeground(FONT_COLOUR);
        setupPresetChoiceListener();
        presetPanel.add(presetChoiceBox, getPresetChoiceConstraints());
    }

    // MODIFIES: this
    // EFFECTS: adds listener to presetChoiceBox
    private void setupPresetChoiceListener() {
        // MODIFIES: this
        // EFFECTS: displays preset form
        presetChoiceBox.addActionListener((e) -> invokeLater(this::displayPresetForm));
    }

    // MODIFIES: this
    // EFFECTS: if "Every Day" is selected, show presetEveryDay,
    //          if "Every Weekday" is selected, show presetWeekdays,
    //          if "Every Weekend" is selected, show presetWeekends,
    //          otherwise, hide all preset panels
    private void displayPresetForm() {
        switch (presetChoiceBox.getSelectedIndex()) {
            case 1:
                presetEveryDay.setVisible(true);
                presetWeekdays.setVisible(false);
                presetWeekends.setVisible(false);
                break;
            case 2:
                presetEveryDay.setVisible(false);
                presetWeekdays.setVisible(true);
                presetWeekends.setVisible(false);
                break;
            case 3:
                presetEveryDay.setVisible(false);
                presetWeekdays.setVisible(false);
                presetWeekends.setVisible(true);
                break;
            default:
                presetEveryDay.setVisible(false);
                presetWeekdays.setVisible(false);
                presetWeekends.setVisible(false);
        }
    }

    // MODIFIES: this
    // EFFECTS: setups presetEveryDay panel and adds to presetPanel
    private void setupPresetEveryDay() {
        presetEveryDay = new JPanel();
        presetEveryDay.setLayout(new GridBagLayout());
        presetEveryDay.setBackground(APP_COLOUR);
        JComboBox<String> frequency = new JComboBox<>(new String[]{"Number of Notifications per Day", "1", "2"});
        frequency.setFont(MEDIUM_FONT);
        frequency.setBackground(APP_COLOUR);
        frequency.setForeground(FONT_COLOUR);
        setupPresetEveryDayBoxListener(frequency);
        presetEveryDay.add(frequency, getPresetBoxConstraints());
        presetEveryDay.setVisible(false);
        presetPanel.add(presetEveryDay, getPresetFormConstraints());
    }

    // EFFECTS: returns preset box constraints
    private GridBagConstraints getPresetBoxConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // EFFECTS: returns preset form constraints
    private GridBagConstraints getPresetFormConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: frequencyBox
    // EFFECTS: adds listener to frequencyBox
    private void setupPresetEveryDayBoxListener(JComboBox<String> frequencyBox) {
        // MODIFIES: this
        // EFFECTS: updates presetEveryDayUI,
        //          if a valid frequency is selected, then generate a preset form and submit button
        frequencyBox.addActionListener((e) -> invokeLater(() -> {
            int frequency = frequencyBox.getSelectedIndex();
            if (frequency != 0) {
                updatePresetEveryDayUI(frequency);
                generatePresetForm(presetEveryDay, frequency);
                generatePresetEveryDaySubmitButton();
            } else {
                updatePresetEveryDayUI(frequency);
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: sets up preset every day submit button and adds to presetEveryDay panel
    private void generatePresetEveryDaySubmitButton() {
        JButton submitButton = getPresetSubmitButton();
        // MODIFIES: this
        // EFFECTS: attempts to setPresetEveryDay reminders, notifies user whether reminder generation was successful,
        //          if successful, then update reminders UI
        submitButton.addActionListener((e) -> invokeLater(() -> {
            if (setPresetEveryDay()) {
                HabitManagerUI.changeMade();
                JOptionPane.showMessageDialog(null, "Notifications have been set for this habit.",
                        "Notifications Set", JOptionPane.INFORMATION_MESSAGE);
                updateRemindersUI();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Notifications. Please try again.",
                        "Invalid Time", JOptionPane.ERROR_MESSAGE);
            }
        }));
        presetEveryDay.add(submitButton, getSubmitButtonConstraints());
    }

    // MODIFIES: this
    // EFFECTS: attempts to set preset every day reminders, returns whether reminders were set successfully
    private boolean setPresetEveryDay() {
        Set<LocalTime> times = new HashSet<>();
        if (!populateTimes(times, presetEveryDay)) {
            return false;
        }
        Set<LocalDateTime> reminders = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            DayOfWeek dayOfWeek = DayOfWeek.of(i + 1);
            for (LocalTime time : times) {
                reminders.add(WeeklyReminder.makeWeeklyReminder(dayOfWeek, time, Clock.systemDefaultZone()));
            }
        }
        habit.getHabitReminder().setCustomReminders(reminders);
        return true;
    }

    // MODIFIES: times
    // EFFECTS: populates times with user inputted times, returns false if there are duplicate times
    private boolean populateTimes(Set<LocalTime> times, JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (!(component instanceof JPanel)) {
                continue;
            }
            JPanel row = (JPanel) component;
            JSpinner hours = (JSpinner) row.getComponent(2);
            JSpinner minutes = (JSpinner) row.getComponent(4);
            JComboBox<String> amPm = (JComboBox<String>) row.getComponent(6);
            commitSpinners(hours, minutes);
            String minString = getMinuteString(minutes);
            LocalTime time = LocalTime.parse(hours.getValue() + ":" + minString + " " + amPm.getSelectedItem(),
                    DateTimeFormatter.ofPattern("h:mm a"));
            if (!times.add(time)) {
                return false;
            }
        }
        return true;
    }

    // MODIFIES: panel
    // EFFECTS: generates presetForm and adds each row to panel
    private void generatePresetForm(JPanel panel, int frequency) {
        for (int i = 0; i < frequency; i++) {
            JPanel row = setupPresetRow(i);
            panel.add(row, getTimeInputRowConstraints(i + 1));
        }
    }

    // EFFECTS: setups a preset row at index i and returns it
    private JPanel setupPresetRow(int i) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(APP_COLOUR);
        JLabel label = new JLabel("Notification " + (i + 1));
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
        JComboBox<String> amPm = new JComboBox<>(new String[] {"AM", "PM"});
        amPm.setFont(MEDIUM_FONT);
        amPm.setBackground(APP_COLOUR);
        amPm.setForeground(FONT_COLOUR);
        row.add(amPm);
        return row;
    }

    // MODIFIES: this
    // EFFECTS: updates presetEveryDay panel with frequency box set to given index
    private void updatePresetEveryDayUI(int index) {
        presetEveryDay.removeAll();
        JComboBox<String> frequency = new JComboBox<>(new String[] {"Number of Notifications per Day", "1", "2"});
        frequency.setFont(MEDIUM_FONT);
        frequency.setBackground(APP_COLOUR);
        frequency.setForeground(FONT_COLOUR);
        frequency.setSelectedIndex(index);
        setupPresetEveryDayBoxListener(frequency);
        presetEveryDay.add(frequency, getPresetBoxConstraints());
    }

    // MODIFIES: this
    // EFFECTS: setups presetWeekdays panel and adds it to presetPanel
    private void setupPresetWeekdays() {
        presetWeekdays = new JPanel();
        presetWeekdays.setLayout(new GridBagLayout());
        presetWeekdays.setBackground(APP_COLOUR);
        JComboBox<String> frequency = new JComboBox<>(new String[]
                {"Number of Notifications per Weekday", "1", "2", "3"});
        frequency.setFont(MEDIUM_FONT);
        frequency.setBackground(APP_COLOUR);
        frequency.setForeground(FONT_COLOUR);
        setupPresetWeekdaysBoxListener(frequency);
        presetWeekdays.add(frequency, getPresetBoxConstraints());
        presetWeekdays.setVisible(false);
        presetPanel.add(presetWeekdays, getPresetFormConstraints());
    }

    // MODIFIES: frequencyBox
    // EFFECTS: adds listener to frequencyBox
    private void setupPresetWeekdaysBoxListener(JComboBox<String> frequencyBox) {
        // MODIFIES: this
        // EFFECTS: updates presetWeekdaysUI,
        //          if a valid frequency is selected, generate preset form and preset weekdays submit button
        frequencyBox.addActionListener((e) -> invokeLater(() -> {
            int frequency = frequencyBox.getSelectedIndex();
            if (frequency != 0) {
                updatePresetWeekdaysUI(frequency);
                generatePresetForm(presetWeekdays, frequency);
                generatePresetWeekdaysSubmitButton();
            } else {
                updatePresetWeekdaysUI(frequency);
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: sets up preset weekday submit button and adds it to presetWeekdays panel
    private void generatePresetWeekdaysSubmitButton() {
        JButton submitButton = getPresetSubmitButton();
        // MODIFIES: this
        // EFFECTS: attempts to set presetWeekdays reminders and notifies user
        //          on whether reminders were set successfully,
        //          if successful, update reminders UI
        submitButton.addActionListener((e) -> invokeLater(() -> {
            if (setPresetWeekdays()) {
                HabitManagerUI.changeMade();
                JOptionPane.showMessageDialog(null, "Notifications have been set for this habit.",
                        "Notifications Set", JOptionPane.INFORMATION_MESSAGE);
                updateRemindersUI();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Notifications. Please try again.",
                        "Invalid Time", JOptionPane.ERROR_MESSAGE);
            }
        }));
        presetWeekdays.add(submitButton, getSubmitButtonConstraints());
    }

    // MODIFIES: this
    // EFFECTS: attempts to set presetWeekdays reminders, returns whether reminders were set successfully
    private boolean setPresetWeekdays() {
        Set<LocalTime> times = new HashSet<>();
        if (!populateTimes(times, presetWeekdays)) {
            return false;
        }
        Set<LocalDateTime> reminders = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            DayOfWeek dayOfWeek = DayOfWeek.of(i + 1);
            for (LocalTime time : times) {
                reminders.add(WeeklyReminder.makeWeeklyReminder(dayOfWeek, time, Clock.systemDefaultZone()));
            }
        }
        habit.getHabitReminder().setCustomReminders(reminders);
        return true;
    }

    // MODIFIES: this
    // EFFECTS: updates presetWeekdaysUI with frequency box set to the given index
    private void updatePresetWeekdaysUI(int index) {
        presetWeekdays.removeAll();
        JComboBox<String> frequency = new JComboBox<>(new String[]
                {"Number of Notifications per Weekday", "1", "2", "3"});
        frequency.setFont(MEDIUM_FONT);
        frequency.setBackground(APP_COLOUR);
        frequency.setForeground(FONT_COLOUR);
        frequency.setSelectedIndex(index);
        setupPresetWeekdaysBoxListener(frequency);
        presetWeekdays.add(frequency, getPresetBoxConstraints());
    }

    // MODIFIES: this
    // EFFECTS: setups presetWeekends panel and adds it to presetPanel
    private void setupPresetWeekends() {
        presetWeekends = new JPanel();
        presetWeekends.setLayout(new GridBagLayout());
        presetWeekends.setBackground(APP_COLOUR);
        JComboBox<String> frequency = new JComboBox<>(new String[]
                {"Number of Notifications per Day of Weekend", "1", "2", "3", "4", "5", "6", "7"});
        frequency.setFont(MEDIUM_FONT);
        frequency.setBackground(APP_COLOUR);
        frequency.setForeground(FONT_COLOUR);
        setupPresetWeekendsBoxListener(frequency);
        presetWeekends.add(frequency, getPresetBoxConstraints());
        presetWeekends.setVisible(false);
        presetPanel.add(presetWeekends, getPresetFormConstraints());
    }

    // MODIFIES: frequencyBox
    // EFFECTS: adds listener to frequencyBox
    private void setupPresetWeekendsBoxListener(JComboBox<String> frequencyBox) {
        // MODIFIES: this
        // EFFECTS: update preset weekends UI
        //          if valid frequency is selected,
        //          generatePresetForm and generate preset weekends submit button
        frequencyBox.addActionListener((e) -> invokeLater(() -> {
            int frequency = frequencyBox.getSelectedIndex();
            if (frequency != 0) {
                updatePresetWeekendsUI(frequency);
                generatePresetForm(presetWeekends, frequency);
                generatePresetWeekendsSubmitButton();
            } else {
                updatePresetWeekendsUI(frequency);
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: setups and adds submit button to presetWeekends panel
    private void generatePresetWeekendsSubmitButton() {
        JButton submitButton = getPresetSubmitButton();
        // MODIFIES: this
        // EFFECTS: attempts to set presetWeekends reminders, notifies
        //          user whether reminders were set successfully,
        //          if successful, update reminders UI
        submitButton.addActionListener((e) -> invokeLater(() -> {
            if (setPresetWeekends()) {
                HabitManagerUI.changeMade();
                JOptionPane.showMessageDialog(null, "Notifications have been set for this habit.",
                        "Notifications Set", JOptionPane.INFORMATION_MESSAGE);
                updateRemindersUI();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Notifications. Please try again.",
                        "Invalid Notifications", JOptionPane.ERROR_MESSAGE);
            }
        }));
        presetWeekends.add(submitButton, getSubmitButtonConstraints());
    }

    // MODIFIES: this
    // EFFECTS: attempts to set presetWeekends reminders, returns whether reminders were set successfully
    private boolean setPresetWeekends() {
        Set<LocalTime> times = new HashSet<>();
        if (!populateTimes(times, presetWeekends)) {
            return false;
        }
        DayOfWeek saturday = DayOfWeek.SATURDAY;
        DayOfWeek sunday = DayOfWeek.SUNDAY;
        Set<LocalDateTime> reminders = new HashSet<>();
        for (LocalTime time : times) {
            reminders.add(WeeklyReminder.makeWeeklyReminder(saturday, time, Clock.systemDefaultZone()));
            reminders.add(WeeklyReminder.makeWeeklyReminder(sunday, time, Clock.systemDefaultZone()));
        }
        habit.getHabitReminder().setCustomReminders(reminders);
        return true;
    }

    // MODIFIES: this
    // EFFECTS: updates presetWeekendsUI, frequency box set to given index
    private void updatePresetWeekendsUI(int index) {
        presetWeekends.removeAll();
        JComboBox<String> frequency = new JComboBox<>(new String[] {"Number of Notifications per Day of Weekend",
                "1", "2", "3", "4", "5", "6", "7"});
        frequency.setFont(MEDIUM_FONT);
        frequency.setBackground(APP_COLOUR);
        frequency.setForeground(FONT_COLOUR);
        frequency.setSelectedIndex(index);
        setupPresetWeekendsBoxListener(frequency);
        presetWeekends.add(frequency, getPresetBoxConstraints());
    }

    // EFFECTS: returns a preset submit button
    private JButton getPresetSubmitButton() {
        JButton button = new JButton("Submit");
        makeButton(button, WINDOW_WIDTH - SIDE_BAR_WIDTH, 50, MEDIUM_FONT);
        return button;
    }

    // MODIFIES: this
    // EFFECTS: setups components in customization tab
    @Override
    protected void setupCustomizationComponents(int startingIndex) {
        setupCustomizationFrequency(MAX_FREQUENCY,
                "Choose specific days and times for your notifications!", startingIndex);
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
            JComboBox<String> day = (JComboBox<String>) row.getComponent(2);
            JSpinner hours = (JSpinner) row.getComponent(4);
            JSpinner minutes = (JSpinner) row.getComponent(6);
            commitSpinners(hours, minutes);
            JComboBox<String> amPm = (JComboBox<String>) row.getComponent(8);
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.getSelectedItem().toString().toUpperCase());
            String minString = getMinuteString(minutes);
            LocalTime time = LocalTime.parse(hours.getValue() + ":" + minString + " " + amPm.getSelectedItem(),
                    DateTimeFormatter.ofPattern("h:mm a"));
            if (!reminders.add(WeeklyReminder.makeWeeklyReminder(dayOfWeek, time, Clock.systemDefaultZone()))) {
                return false;
            }
        }
        habit.getHabitReminder().setCustomReminders(reminders);
        return true;
    }

    // EFFECTS: returns a row in the customization tab, representing a specific time and/or day
    @Override
    protected JPanel setupCustomizationRow(int index) {
        JPanel row = customizationRowInit(index);
        JComboBox<String> day = new JComboBox<>(new String[] {"Sunday", "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday"});
        day.setFont(MEDIUM_FONT);
        day.setBackground(APP_COLOUR);
        day.setForeground(FONT_COLOUR);
        row.add(day);
        row.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        JSpinner hours = new JSpinner(new SpinnerNumberModel(9, 1, 12, 1));
        setupSpinner(hours);
        row.add(hours);
        row.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        JSpinner minutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        setupSpinner(minutes);
        row.add(minutes);
        row.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        JComboBox<String> amPm = new JComboBox<>(new String[] {"AM", "PM"});
        amPm.setFont(MEDIUM_FONT);
        amPm.setBackground(APP_COLOUR);
        amPm.setForeground(FONT_COLOUR);
        row.add(amPm);
        return row;
    }

    // EFFECTS: returns a customizationRow JPanel at index i that has been initialized
    private JPanel customizationRowInit(int i) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(APP_COLOUR);
        JLabel label = new JLabel("Reminder " + (i + 1));
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        row.add(label);
        row.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        return row;
    }

    // MODIFIES: this
    // EFFECTS: setups components in reminder list tab
    @Override
    protected void setupReminderListComponents() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE h:mm a");
        List<LocalDateTime> reminders = new ArrayList<>(habit.getHabitReminder().getReminders());
        Collections.sort(reminders);
        for (LocalDateTime reminder : reminders) {
            setupReminderListRow(reminder.format(formatter));
        }
    }
}