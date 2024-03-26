package ui.card;

import model.Habit;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Abstract class to represent a general habit reminder customization JPanel
public abstract class HabitRemindersUI extends JPanel {
    protected JScrollPane parentPreset;
    protected JPanel presetPanel;
    protected JScrollPane parentCustomization;
    protected JPanel customizationPanel;
    protected JScrollPane parentReminderList;
    protected JPanel reminderListPanel;
    protected JTabbedPane tabbedPane;
    protected Habit habit;

    // EFFECTS: constructs a habit reminder panel
    public HabitRemindersUI(Habit habit) {
        this.habit = habit;
        UIManager.put("TabbedPane.selected", APP_COLOUR.brighter().brighter().brighter());
        setupPanel();
    }

    // MODIFIES: this
    // EFFECTS: setups habit reminder panel
    private void setupPanel() {
        setLayout(new GridLayout(1, 1));
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setBackground(APP_COLOUR);
        setupTabbedPane();
        setupTabs();
        add(tabbedPane);
    }

    // MODIFIES: this
    // EFFECTS: setups tabbed pane
    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane.setBackground(APP_COLOUR);
        tabbedPane.setForeground(FONT_COLOUR);
        tabbedPane.setFont(MEDIUM_FONT);
    }

    // MODIFIES: this
    // EFFECTS: setups individual tabs in the tabbed pane
    private void setupTabs() {
        parentPreset = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        parentPreset.getVerticalScrollBar().setUnitIncrement(10);
        parentCustomization = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        parentCustomization.getVerticalScrollBar().setUnitIncrement(10);
        parentReminderList = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        parentReminderList.getVerticalScrollBar().setUnitIncrement(10);
        setupPresetPanel();
        setupCustomizationPanel(0);
        setupReminderListPanel();
        tabbedPane.addTab("Preset", null, parentPreset, "Choose Preset Notifications");
        tabbedPane.addTab("Customize", null, parentCustomization, "Customize Your Notifications");
        tabbedPane.addTab("Notification List", LIST_ICON, parentReminderList, "View Your Notifications");
    }

    // MODIFIES: this
    // EFFECTS: setups preset tab
    private void setupPresetPanel() {
        presetPanel = new JPanel();
        presetPanel.setLayout(new GridBagLayout());
        presetPanel.setBackground(APP_COLOUR);
        JPanel presetTitle = getPresetTitle();
        presetPanel.add(presetTitle, getTitleConstraints());
        JToggleButton presetToggleButton = getToggleButton();
        presetPanel.add(presetToggleButton, getToggleButtonConstraints());
        if (habit.isNotifyEnabled()) {
            JButton presetDefaultButton = getDefaultButton();
            presetPanel.add(presetDefaultButton, getDefaultButtonConstraints());
            setupPresetComponents();
        }
        addEmptySpace(presetPanel);
        parentPreset.setViewportView(presetPanel);
    }

    // MODIFIES: this
    // EFFECTS: setups preset label of preset tab based on given string
    protected void setupPresetLabel(String string) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(APP_COLOUR);
        JLabel presetLabel = new JLabel(string);
        presetLabel.setFont(BIG_FONT);
        presetLabel.setForeground(FONT_COLOUR);
        panel.add(presetLabel);
        presetPanel.add(panel, getPresetLabelConstraints());
    }

    // MODIFIES: this
    // EFFECTS: setups customization label and customization frequency combo box
    protected void setupCustomizationFrequency(int maxFrequency, String labelText, int startingIndex) {
        JPanel customizationLabel = getCustomizationLabelPanel(labelText);
        customizationPanel.add(customizationLabel, getCustomizationLabelConstraints());
        String[] frequencyOptions = new String[maxFrequency + 1];
        getFrequencyOptions(frequencyOptions, maxFrequency);
        JComboBox<String> customizationFrequency = new JComboBox<>(frequencyOptions);
        customizationFrequency.setFont(MEDIUM_FONT);
        customizationFrequency.setBackground(APP_COLOUR);
        customizationFrequency.setForeground(FONT_COLOUR);
        customizationFrequency.setSelectedIndex(startingIndex);
        setupCustomizationFrequencyListener(customizationFrequency);
        customizationPanel.add(customizationFrequency, getCustomizationFrequencyConstraints());
    }

    // EFFECTS: returns grid bag constraints for frequency combo box
    private GridBagConstraints getCustomizationFrequencyConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: customizationFrequency
    // EFFECTS: setups listener to the customization tab frequency combo box
    private void setupCustomizationFrequencyListener(JComboBox<String> customizationFrequency) {
        // MODIFIES: this
        // EFFECTS: updates customization components and generates customization form with index number of rows
        customizationFrequency.addActionListener(e -> invokeLater(() -> {
            int index = customizationFrequency.getSelectedIndex();
            updateCustomizationComponents(index);
            if (index != 0) {
                generateCustomizationForm(index);
            }
        }));
    }

    // EFFECTS: returns a row in the customization tab, representing a specific time and/or day
    protected abstract JPanel setupCustomizationRow(int index);

    // MODIFIES: customizationPanel
    // EFFECTS: sets custom reminders based on the content in each customization row, returns whether
    //          user inputted reminders are valid
    //          if valid, set custom reminders to the current habit
    protected abstract boolean setCustomReminders(int frequency);

    // MODIFIES: this
    // EFFECTS: generates customization form based on given frequency
    private void generateCustomizationForm(int frequency) {
        customizationPanel.remove(5);
        for (int i = 0; i < frequency; i++) {
            JPanel row = setupCustomizationRow(i);
            customizationPanel.add(row, getTimeInputRowConstraints(i + 5));
        }
        JButton submit = new JButton("Set Notifications");
        makeButton(submit, WINDOW_WIDTH - SIDE_BAR_WIDTH, 50, MEDIUM_FONT);
        setupCustomizationButtonListener(submit, frequency);
        customizationPanel.add(submit, getSubmitButtonConstraints());
        addEmptySpace(customizationPanel);
    }

    // MODIFIES: submit
    // EFFECTS: setups listener to the customization submit button
    private void setupCustomizationButtonListener(JButton submit, int frequency) {
        // MODIFIES: this
        // EFFECTS: notify user whether custom reminders are set successfully
        //          if successful, update reminders UI
        submit.addActionListener((e) -> invokeLater(() -> {
            if (setCustomReminders(frequency)) {
                HabitManagerUI.changeMade();
                JOptionPane.showMessageDialog(null, "Notifications have been set for this habit.",
                        "Notifications Set", JOptionPane.INFORMATION_MESSAGE);
                updateRemindersUI();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Notifications. Please try again.",
                        "Invalid Time", JOptionPane.ERROR_MESSAGE);
            }
        }));
    }

    // EFFECTS: returns the minutes in the given JSpinner as a String as exactly two digits (formatted like time)
    protected String getMinuteString(JSpinner minutes) {
        int min = (int) minutes.getValue();
        if (min < 10) {
            return "0" + min;
        } else {
            return minutes.getValue().toString();
        }
    }

    // EFFECTS: returns grid bag constraints of a time input row given a row index
    protected GridBagConstraints getTimeInputRowConstraints(int index) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = index;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // EFFECTS: returns grid bag constraints of preset label
    private GridBagConstraints getPresetLabelConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: this
    // EFFECTS: setups components in preset tab
    protected abstract void setupPresetComponents();

    // EFFECTS: returns JPanel containing title label of preset tab
    private JPanel getPresetTitle() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.setBackground(APP_COLOUR);
        JLabel title = new JLabel("Preset Notifications");
        title.setFont(HUGE_FONT);
        title.setForeground(FONT_COLOUR);
        titlePanel.add(title);
        return titlePanel;
    }

    // EFFECTS: returns grid bag constraints of a row in the reminder list tab
    private GridBagConstraints getReminderListRowConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // EFFECTS: returns JPanel containing customization title label
    protected JPanel getCustomizationLabelPanel(String string) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(APP_COLOUR);
        JLabel label = new JLabel(string);
        label.setFont(BIG_FONT);
        label.setForeground(FONT_COLOUR);
        panel.add(label);
        return panel;
    }

    // MODIFIES: frequencyOptions
    // EFFECTS: setups array of frequencyOptions based on given maximum
    private void getFrequencyOptions(String[] frequencyOptions, int max) {
        frequencyOptions[0] = "Number of Notifications";
        for (int i = 0; i < max; i++) {
            frequencyOptions[i + 1] = Integer.toString(i + 1);
        }
    }

    // EFFECTS: returns grid bag constraints for the preset choice combo box for weekly and monthly habits
    protected GridBagConstraints getPresetChoiceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // EFFECTS: setups row in reminder list tab
    protected void setupReminderListRow(String reminder) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout());
        row.setBackground(APP_COLOUR);
        JLabel label = new JLabel(reminder);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        row.add(label);
        reminderListPanel.add(row, getReminderListRowConstraints());
    }

    // EFFECTS: returns grid bag constraints of customization label of customization tab
    private GridBagConstraints getCustomizationLabelConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // EFFECTS: returns grid bag constraints of title label in each tab
    private GridBagConstraints getTitleConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: this
    // EFFECTS: setups customization tab
    private void setupCustomizationPanel(int startingIndex) {
        customizationPanel = new JPanel();
        customizationPanel.setLayout(new GridBagLayout());
        customizationPanel.setBackground(APP_COLOUR);
        JPanel customizationTitle = setupCustomizationTitle();
        customizationPanel.add(customizationTitle, getTitleConstraints());
        JToggleButton customizationToggleButton = getToggleButton();
        customizationPanel.add(customizationToggleButton, getToggleButtonConstraints());
        if (habit.isNotifyEnabled()) {
            customizationPanel.add(getDefaultButton(), getDefaultButtonConstraints());
            setupCustomizationComponents(startingIndex);
        }
        addEmptySpace(customizationPanel);
        parentCustomization.setViewportView(customizationPanel);
    }

    // EFFECTS: returns grid bag constraints of revert to default button
    private GridBagConstraints getDefaultButtonConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: this
    // EFFECTS: setups components in customization tab
    protected abstract void setupCustomizationComponents(int startingIndex);

    // EFFECTS: returns grid bag constraints of toggle on/off button
    private GridBagConstraints getToggleButtonConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: this
    // EFFECTS: setups reminder list tab
    private void setupReminderListPanel() {
        reminderListPanel = new JPanel();
        reminderListPanel.setLayout(new GridBagLayout());
        reminderListPanel.setBackground(APP_COLOUR);
        JPanel reminderListTitle = getReminderListTitle();
        reminderListPanel.add(reminderListTitle, getTitleConstraints());
        if (!habit.isNotifyEnabled()) {
            reminderListPanel.add(setupNoRemindersPanel(), getNoRemindersConstraints());
        } else {
            setupReminderListComponents();
        }
        addEmptySpace(reminderListPanel);
        parentReminderList.setViewportView(reminderListPanel);
    }

    // MODIFIES: this
    // EFFECTS: setups components in reminder list tab
    protected abstract void setupReminderListComponents();

    // EFFECTS: returns panel containing label informing user that notifications are off
    private JPanel setupNoRemindersPanel() {
        JPanel noRemindersPanel = new JPanel();
        noRemindersPanel.setLayout(new FlowLayout());
        noRemindersPanel.setBackground(APP_COLOUR);
        JLabel noReminders = new JLabel("Notifications are turned off for this habit");
        noReminders.setFont(BIG_FONT);
        noReminders.setForeground(FONT_COLOUR);
        noRemindersPanel.add(noReminders);
        return noRemindersPanel;
    }

    // EFFECTS: returns grid bag constraints of no reminders panel
    private GridBagConstraints getNoRemindersConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // EFFECTS: returns JPanel containing reminder list title label
    private JPanel getReminderListTitle() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.setBackground(APP_COLOUR);
        JLabel title = new JLabel("Your Notifications");
        title.setFont(HUGE_FONT);
        title.setForeground(FONT_COLOUR);
        titlePanel.add(title);
        return titlePanel;
    }

    // EFFECTS: returns toggle button for toggling notifications on/off
    private JToggleButton getToggleButton() {
        boolean isNotifyEnabled = habit.isNotifyEnabled();
        String message = isNotifyEnabled ? "Turn off notifications for this habit" :
                "Turn on notifications for this habit";
        JToggleButton toggleButton = new JToggleButton(message, isNotifyEnabled ? BELL_OFF : BELL_ON, isNotifyEnabled);
        toggleButton.setFont(BIG_FONT);
        toggleButton.setAlignmentX(CENTER_ALIGNMENT);
        toggleButton.setBackground(APP_COLOUR);
        toggleButton.setForeground(FONT_COLOUR);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setForeground(FONT_COLOUR);
        toggleButton.setFocusable(false);
        toggleButton.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50));
        toggleButton.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50));
        toggleButton.setMaximumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50));
        setupToggleButtonListener(toggleButton);
        return toggleButton;
    }

    // MODIFIES: toggleButton
    // EFFECTS: setups toggle button listener
    private void setupToggleButtonListener(JToggleButton toggleButton) {
        // MODIFIES: this
        // EFFECTS: toggles notifications
        toggleButton.addItemListener(e -> invokeLater(this::toggleNotifications));
    }

    // MODIFIES: this
    // EFFECTS: toggles notifications on/off
    private void toggleNotifications() {
        HabitManagerUI.changeMade();
        habit.toggleNotifyEnabled();
        updateRemindersUI();
    }

    // EFFECTS: returns revert to default button
    private JButton getDefaultButton() {
        JButton setDefaults = new JButton("Revert to Default Notifications");
        makeButton(setDefaults, WINDOW_WIDTH - SIDE_BAR_WIDTH, 50, MEDIUM_FONT);
        setDefaults.setForeground(FONT_COLOUR);
        setDefaults.setEnabled(!habit.getHabitReminder().isDefault());
        // MODIFIES: this
        // EFFECTS: reverts to default notifications, disabled default button
        setDefaults.addActionListener(e -> invokeLater(() -> {
            HabitManagerUI.changeMade();
            habit.getHabitReminder().setDefaultReminders();
            JOptionPane.showMessageDialog(null, "Default notifications have been set for this habit.",
                    "Default Notifications Set", JOptionPane.INFORMATION_MESSAGE);
            setEnabled(false);
            updateRemindersUI();
        }));
        return setDefaults;
    }

    // EFFECTS: adds empty space panel to bottom of given JPanel
    private void addEmptySpace(JPanel panel) {
        JPanel empty = new JPanel();
        empty.setBackground(APP_COLOUR);
        empty.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50));
        GridBagConstraints constraints = getEmptySpaceConstraints();
        panel.add(empty, constraints);
    }

    // EFFECTS: returns grid bag constraints of empty space,
    //          ensures that title label is anchored to the top of panel
    private GridBagConstraints getEmptySpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weighty = 1;
        constraints.weightx = 1;
        return constraints;
    }

    // EFFECTS: returns grid bag constraints of submit button
    protected GridBagConstraints getSubmitButtonConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // MODIFIES: spinner
    // EFFECTS: given a spinner, initializes properties of spinner, allowing
    //          text field to be editable, disallowing invalid inputs
    protected void setupSpinner(JSpinner spinner) {
        spinner.setFont(MEDIUM_FONT);
        spinner.getEditor().getComponent(0).setBackground(APP_COLOUR);
        spinner.getEditor().getComponent(0).setForeground(FONT_COLOUR);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(true);
        ((NumberFormatter) ((JSpinner.NumberEditor)
                spinner.getEditor()).getTextField().getFormatter()).setAllowsInvalid(false);
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        setupSpinnerListeners(textField);
    }

    // MODIFIES: textField
    // EFFECTS: given a JSpinner text field, selects all once focus is gained
    private void setupSpinnerListeners(JFormattedTextField textField) {
        // MODIFIES: textField
        // EFFECTS: selects all in the textField
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                invokeLater(textField::selectAll);
            }
        });
    }

    // EFFECTS: returns the customization tab title JPanel
    private JPanel setupCustomizationTitle() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.setBackground(APP_COLOUR);
        JLabel title = new JLabel("Customize Notifications");
        title.setFont(HUGE_FONT);
        title.setForeground(FONT_COLOUR);
        titlePanel.add(title);
        return titlePanel;
    }

    // EFFECTS: updates entire habit reminder JPanel
    protected void updateRemindersUI() {
        updatePresetComponents();
        updateCustomizationComponents();
        updateReminderListComponents();
        repaint();
        revalidate();
    }

    // EFFECTS: updates preset tab
    private void updatePresetComponents() {
        presetPanel.removeAll();
        setupPresetPanel();
    }

    // EFFECTS: updates customization tab
    private void updateCustomizationComponents() {
        customizationPanel.removeAll();
        setupCustomizationPanel(0);
    }

    // EFFECTS: updates customization tab, but with customization combo box set to startingIndex
    private void updateCustomizationComponents(int startingIndex) {
        customizationPanel.removeAll();
        setupCustomizationPanel(startingIndex);
    }

    // EFFECTS: updates reminder list tab
    private void updateReminderListComponents() {
        reminderListPanel.removeAll();
        setupReminderListPanel();
    }

    // MODIFIES: first, second
    // EFFECTS: commits edits in both spinners
    protected void commitSpinners(JSpinner first, JSpinner second) {
        try {
            first.commitEdit();
            second.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}