package ui.card.habit.reminder;

import javafx.util.Pair;
import model.Habit;
import model.reminder.MonthlyReminder;
import ui.card.HabitManagerUI;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Represents the JPanel for the notifications tab for a monthly habit
public class MonthlyRemindersUI extends HabitRemindersUI {
    private JPanel presetBegMidEnd;
    private JButton presetBegMidEndSubmit;
    private JPanel presetDayRange;
    private JComboBox<String> presetChoiceBox;

    // EFFECTS: constructs a monthly reminder panel
    public MonthlyRemindersUI(Habit habit) {
        super(habit);
    }

    // MODIFIES: this
    // EFFECTS: setups components in preset tab
    @Override
    protected void setupPresetComponents() {
        setupPresetLabel("Set up to 31 notifications per month!");
        setupPresetChoiceBox();
        setupPresetBegMidEnd();
        setupPresetDayRange();
    }

    // MODIFIES: this
    // EFFECTS: setups preset choice combo box
    private void setupPresetChoiceBox() {
        String[] presetOptions =
                new String[]{"Select a Preset", "Beginning, Middle, or End of Month", "Specific Day Range"};
        presetChoiceBox = new JComboBox<>(presetOptions);
        presetChoiceBox.setFont(MEDIUM_FONT);
        presetChoiceBox.setForeground(FONT_COLOUR);
        presetChoiceBox.setBackground(APP_COLOUR);
        setupPresetChoiceListener();
        presetPanel.add(presetChoiceBox, getPresetChoiceConstraints());
    }

    // MODIFIES: this
    // EFFECTS: adds preset choice listener to presetChoiceBox
    private void setupPresetChoiceListener() {
        // MODIFIES: this
        // EFFECTS: displays preset form depending on selected option in combo box
        presetChoiceBox.addActionListener(e -> invokeLater(this::displayPresetForm));
    }

    // MODIFIES: this
    // EFFECTS: displays preset form depending on selected option in combo box
    //          if first preset selected, presetBegMidEnd is visible,
    //          if second preset selected, presetDayRange is visible,
    //          if neither preset is selected, both are not visible
    private void displayPresetForm() {
        switch (presetChoiceBox.getSelectedIndex()) {
            case 1:
                presetBegMidEnd.setVisible(true);
                presetDayRange.setVisible(false);
                break;
            case 2:
                presetBegMidEnd.setVisible(false);
                presetDayRange.setVisible(true);
                break;
            default:
                presetBegMidEnd.setVisible(false);
                presetDayRange.setVisible(false);
        }
    }

    // MODIFIES: this
    // EFFECTS: setups presetBegMidEnd panel
    private void setupPresetBegMidEnd() {
        presetBegMidEnd = new JPanel();
        presetBegMidEnd.setLayout(new GridBagLayout());
        presetBegMidEnd.setBackground(APP_COLOUR);
        setupBegMidEndLabel();
        setupPresetCheckboxes();
        setupBegMidEndRow();
        setupBegMidEndSubmit();
        presetBegMidEnd.setVisible(false);
        presetPanel.add(presetBegMidEnd, getPresetConstraints(5));
    }

    // MODIFIES: this
    // EFFECTS: adds begMidEndRow to presetBedMidEnd
    private void setupBegMidEndRow() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(APP_COLOUR);
        JLabel label = new JLabel("Notification ");
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
        presetBegMidEnd.add(row, getPresetConstraints(2));
    }

    // MODIFIES: this
    // EFFECTS: setups begMidEnd submit button
    private void setupBegMidEndSubmit() {
        presetBegMidEndSubmit = new JButton("Set Notifications");
        makeButton(presetBegMidEndSubmit, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        presetBegMidEndSubmit.setEnabled(false);
        setupBegMidEndSubmitListener();
        presetBegMidEnd.add(presetBegMidEndSubmit, getSubmitButtonConstraints());
    }

    // MODIFIES: this
    // EFFECTS: setups begMidEnd submit button listener
    private void setupBegMidEndSubmitListener() {
        // MODIFIES: this
        // EFFECTS: sets monthly reminders based on begMidEnd selection
        presetBegMidEndSubmit.addActionListener(e -> invokeLater(this::setBegMidEndReminders));
    }

    // MODIFIES: this
    // EFFECTS: sets monthly reminders based on begMidEndSelection, then updates reminders UI
    private void setBegMidEndReminders() {
        JPanel checkboxes = (JPanel) presetBegMidEnd.getComponent(1);
        JPanel row = (JPanel) presetBegMidEnd.getComponent(2);
        LocalTime time = getBegMidTime(row);
        boolean firstDay = ((JCheckBox) checkboxes.getComponent(0)).isSelected();
        boolean middleDay = ((JCheckBox) checkboxes.getComponent(1)).isSelected();
        boolean lastDay = ((JCheckBox) checkboxes.getComponent(2)).isSelected();
        Set<Pair<Integer, LocalTime>> reminders = new HashSet<>();
        if (firstDay) {
            reminders.add(new Pair<>(1, time));
        }
        if (middleDay) {
            reminders.add(new Pair<>(15, time));
        }
        if (lastDay) {
            reminders.add(new Pair<>(31, time));
        }
        ((MonthlyReminder) habit.getHabitReminder()).setCustomMonthlyReminders(reminders);
        HabitManagerUI.changeMade();
        JOptionPane.showMessageDialog(null, "Notifications set successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        updateRemindersUI();
    }

    // EFFECTS: returns the inputted time based on row
    private LocalTime getBegMidTime(JPanel row) {
        JSpinner hours = (JSpinner) row.getComponent(2);
        JSpinner minutes = (JSpinner) row.getComponent(4);
        commitSpinners(hours, minutes);
        int hour = (int) hours.getValue();
        int minute = (int) minutes.getValue();
        String amPm = (String) ((JComboBox<String>) row.getComponent(6)).getSelectedItem();
        String stringMin;
        if (minute < 10) {
            stringMin = "0" + minute;
        } else {
            stringMin = Integer.toString(minute);
        }
        return LocalTime.parse(hour + ":" + stringMin + " " + amPm, DateTimeFormatter.ofPattern("h:mm a"));
    }

    // MODIFIES: this
    // EFFECTS: setups begMidEnd preset label and adds to presetBegMidEnd
    private void setupBegMidEndLabel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(APP_COLOUR);
        JLabel label = new JLabel("Choose which days of the month to receive notifications!");
        label.setFont(BIG_FONT);
        label.setForeground(FONT_COLOUR);
        panel.add(label);
        presetBegMidEnd.add(panel, getPresetConstraints(0));
    }

    // MODIFIES: this
    // EFFECTS: setups begMidEnd checkboxes and adds them to presetBegMidEnd
    private void setupPresetCheckboxes() {
        JPanel checkboxes = new JPanel();
        checkboxes.setLayout(new FlowLayout(FlowLayout.CENTER, PADDING * 2, 0));
        checkboxes.setBackground(APP_COLOUR);
        JCheckBox beginning = getCheckbox("First Day of Month");
        JCheckBox middle = getCheckbox("Middle of Month");
        JCheckBox end = getCheckbox("Last Day of Month");
        setupCheckboxListeners(beginning, middle, end);
        checkboxes.add(beginning);
        checkboxes.add(middle);
        checkboxes.add(end);
        presetBegMidEnd.add(checkboxes, getPresetConstraints(1));
    }

    // EFFECTS: returns a checkbox with the given text
    private JCheckBox getCheckbox(String string) {
        JCheckBox checkbox = new JCheckBox(string);
        checkbox.setFont(MEDIUM_FONT);
        checkbox.setForeground(FONT_COLOUR);
        checkbox.setBackground(APP_COLOUR);
        checkbox.setMinimumSize(new Dimension(200, 50));
        checkbox.setPreferredSize(new Dimension(200, 50));
        checkbox.setMaximumSize(new Dimension(200, 50));
        return checkbox;
    }

    // MODIFIES: beginning, middle, end
    // EFFECTS: adds listeners to beginning, middle, and end
    private void setupCheckboxListeners(JCheckBox beginning, JCheckBox middle, JCheckBox end) {
        // MODIFIES: this
        // EFFECTS: enabled submit button if at least one of checkboxes is selected
        ItemListener listener = e -> invokeLater(() -> checkAtLeastOneSelected(beginning, middle, end));
        beginning.addItemListener(listener);
        middle.addItemListener(listener);
        end.addItemListener(listener);
    }

    // MODIFIES: this
    // EFFECTS: enabled submit button if at least one of checkboxes is selected
    private void checkAtLeastOneSelected(JCheckBox beginning, JCheckBox middle, JCheckBox end) {
        presetBegMidEndSubmit.setEnabled(beginning.isSelected() || middle.isSelected() || end.isSelected());
    }

    // EFFECTS: returns grid bag constraints of preset component at given index
    private GridBagConstraints getPresetConstraints(int index) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = index;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: this
    // EFFECTS: setups day range preset panel
    private void setupPresetDayRange() {
        presetDayRange = new JPanel();
        presetDayRange.setLayout(new GridBagLayout());
        presetDayRange.setBackground(APP_COLOUR);
        setupDayRangeLabel();
        setupDayRangeSpinnerRow();
        setupDayRangeTimeRow();
        setupDayRangeSubmit();
        presetDayRange.setVisible(false);
        presetPanel.add(presetDayRange, getPresetConstraints(5));
    }

    // MODIFIES: this
    // EFFECTS: setups day range submit button
    private void setupDayRangeSubmit() {
        JButton submit = new JButton("Set Notifications");
        makeButton(submit, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        setupDayRangeSubmitListener(submit);
        presetDayRange.add(submit, getSubmitButtonConstraints());
    }

    // MODIFIES: submit
    // EFFECTS: adds listener to day range button
    private void setupDayRangeSubmitListener(JButton submit) {
        // MODIFIES: this
        // EFFECTS: notify user whether preset reminders were set successfully, if successful update reminders UI
        submit.addActionListener(e -> invokeLater(() -> {
            if (setDayRangeReminders()) {
                HabitManagerUI.changeMade();
                JOptionPane.showMessageDialog(null, "Notifications set successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                updateRemindersUI();
            } else {
                JOptionPane.showMessageDialog(null, "Start Day Must Be Before End Day!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: sets day range reminders based on user input, returns whether reminders are valid
    private boolean setDayRangeReminders() {
        JSpinner startDay = (JSpinner) ((JPanel) presetDayRange.getComponent(1)).getComponent(1);
        JSpinner endDay = (JSpinner) ((JPanel) presetDayRange.getComponent(1)).getComponent(3);
        commitSpinners(startDay, endDay);
        int start = (int) startDay.getValue();
        int end = (int) endDay.getValue();
        JPanel row = (JPanel) presetDayRange.getComponent(2);
        JSpinner hours = (JSpinner) row.getComponent(2);
        JSpinner minutes = (JSpinner) row.getComponent(4);
        commitSpinners(hours, minutes);
        JComboBox<String> amPm = (JComboBox<String>) row.getComponent(6);
        String minString = getMinuteString(minutes);
        LocalTime time = LocalTime.parse(hours.getValue() + ":" + minString + " " + amPm.getSelectedItem(),
                DateTimeFormatter.ofPattern("h:mm a"));
        return setDayRangePreset(start, end, time);
    }

    // MODIFIES: this
    // EFFECTS: sets day range preset reminders, returns whether day range is valid - only valid if start <= end
    private boolean setDayRangePreset(int start, int end, LocalTime time) {
        if (start > end) {
            return false;
        }
        if (start == end) {
            ((MonthlyReminder) habit.getHabitReminder()).setCustomMonthlyReminders(
                    Set.of(new Pair<>(start, time)));
            return true;
        }
        Set<Pair<Integer, LocalTime>> reminders = new HashSet<>();
        for (int i = start; i <= end; i++) {
            reminders.add(new Pair<>(i, time));
        }
        ((MonthlyReminder) habit.getHabitReminder()).setCustomMonthlyReminders(reminders);
        return true;
    }

    // MODIFIES: this
    // EFFECTS: setups day range label and adds it to presetDayRange panel
    private void setupDayRangeLabel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(APP_COLOUR);
        JLabel label = new JLabel("Choose a range of days to receive notifications!");
        label.setFont(BIG_FONT);
        label.setForeground(FONT_COLOUR);
        panel.add(label);
        presetDayRange.add(panel, getPresetConstraints(0));
    }

    // MODIFIES: this
    // EFFECTS: setups day range spinner row and adds it to presetDayRange panel
    private void setupDayRangeSpinnerRow() {
        JPanel spinner = new JPanel();
        spinner.setLayout(new FlowLayout(FlowLayout.CENTER, PADDING, 0));
        spinner.setBackground(APP_COLOUR);
        JLabel start = setupLabel("Start Day: ");
        JSpinner startDay = getDayOfMonthSpinner();
        startDay.setValue(1);
        JLabel end = setupLabel("End Day: ");
        JSpinner endDay = getDayOfMonthSpinner();
        endDay.setValue(31);
        resizeSpinners(startDay, endDay);
        spinner.add(start);
        spinner.add(startDay);
        spinner.add(end);
        spinner.add(endDay);
        presetDayRange.add(spinner, getPresetConstraints(1));
    }

    // MODIFIES: startDay, endDay
    // EFFECTS: resizes spinners to width of 300 and height of 30
    private void resizeSpinners(JSpinner startDay, JSpinner endDay) {
        startDay.setMinimumSize(new Dimension(300, 30));
        startDay.setPreferredSize(new Dimension(300, 30));
        startDay.setMaximumSize(new Dimension(300, 30));
        endDay.setMinimumSize(new Dimension(300, 30));
        endDay.setPreferredSize(new Dimension(300, 30));
        endDay.setMaximumSize(new Dimension(300, 30));
    }

    // EFFECTS: returns a label containing the given text
    private JLabel setupLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        return label;
    }

    // MODIFIES: this
    // EFFECTS: setups day range time row and adds it to presetDayRange panel
    private void setupDayRangeTimeRow() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(APP_COLOUR);
        JLabel label = new JLabel("Notification ");
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
        presetDayRange.add(row, getPresetConstraints(2));
    }

    // MODIFIES: this
    // EFFECTS: setups components in customization tab
    @Override
    protected void setupCustomizationComponents(int startingIndex) {
        String label = "Choose specific days and times for your notifications!";
        setupCustomizationFrequency(MONTH_MAX_DAYS, label, startingIndex);
    }

    // EFFECTS: returns a row in the customization tab, representing a specific time and/or day
    @Override
    protected JPanel setupCustomizationRow(int index) {
        JPanel row = rowInit(index);
        JSpinner dayOfMonth = getDayOfMonthSpinner();
        setupSpinner(dayOfMonth);
        row.add(dayOfMonth);
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

    // EFFECTS: returns JPanel containing customization title label, with an additional supplementary label
    @Override
    protected JPanel getCustomizationLabelPanel(String string) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(APP_COLOUR);
        JLabel label = new JLabel(string);
        label.setFont(BIG_FONT);
        label.setForeground(FONT_COLOUR);
        panel.add(label, getCustomizationLabelConstraints(0));
        JLabel supplementary = new JLabel("(Provide the day of month number)");
        supplementary.setFont(MEDIUM_FONT);
        supplementary.setForeground(FONT_COLOUR);
        panel.add(supplementary, getCustomizationLabelConstraints(1));
        return panel;
    }

    // EFFECTS: returns grid bag constraints for a customization label at the given index
    private GridBagConstraints getCustomizationLabelConstraints(int index) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = index;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        return constraints;
    }

    // EFFECTS: returns a customization that has been initialized
    private JPanel rowInit(int index) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(APP_COLOUR);
        JLabel label = new JLabel("Reminder " + (index + 1));
        label.setForeground(FONT_COLOUR);
        label.setFont(MEDIUM_FONT);
        row.add(label);
        row.add(Box.createRigidArea(new Dimension(PADDING, 0)));
        return row;
    }

    // EFFECTS: returns a day of month selection JSpinner
    private JSpinner getDayOfMonthSpinner() {
        JSpinner dayOfMonth = new JSpinner(new SpinnerNumberModel(1, 1, MONTH_MAX_DAYS, 1));
        setupSpinner(dayOfMonth);
        dayOfMonth.setFont(MEDIUM_FONT);
        dayOfMonth.setForeground(FONT_COLOUR);
        dayOfMonth.setBackground(APP_COLOUR);
        return dayOfMonth;
    }

    // MODIFIES: customizationPanel
    // EFFECTS: sets custom reminders based on the content in each customization row, returns whether
    //          user inputted reminders are valid
    //          if valid, set custom reminders to the current habit
    @Override
    protected boolean setCustomReminders(int frequency) {
        Set<Pair<Integer, LocalTime>> pairs = new HashSet<>();
        for (int i = 0; i < frequency; i++) {
            JPanel row = (JPanel) customizationPanel.getComponent(i + 5);
            JSpinner dayOfMonth = (JSpinner) row.getComponent(2);
            JSpinner hours = (JSpinner) row.getComponent(4);
            JSpinner minutes = (JSpinner) row.getComponent(6);
            commitSpinners(dayOfMonth, hours, minutes);
            JComboBox<String> amPm = (JComboBox<String>) row.getComponent(8);
            String minString = getMinuteString(minutes);
            LocalTime time = LocalTime.parse(hours.getValue() + ":" + minString + " " + amPm.getSelectedItem(),
                    DateTimeFormatter.ofPattern("h:mm a"));
            Pair<Integer, LocalTime> pair = new Pair<>((int) dayOfMonth.getValue(), time);
            if (!pairs.add(pair)) {
                return false;
            }
        }
        ((MonthlyReminder)habit.getHabitReminder()).setCustomMonthlyReminders(pairs);
        return true;
    }

    // MODIFIES: dayOfMonth, hours, minutes
    // EFFECTS: commits the given spinners
    private void commitSpinners(JSpinner dayOfMonth, JSpinner hours, JSpinner minutes) {
        try {
            dayOfMonth.commitEdit();
            hours.commitEdit();
            minutes.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: setups components in reminder list tab
    @Override
    protected void setupReminderListComponents() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d h:mm a");
        List<LocalDateTime> reminders = new ArrayList<>(habit.getHabitReminder().getReminders());
        Collections.sort(reminders);
        for (LocalDateTime reminder : reminders) {
            setupReminderListRow(reminder.format(formatter));
        }
    }
}