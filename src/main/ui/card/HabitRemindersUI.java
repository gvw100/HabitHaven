package ui.card;

import model.Habit;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

public abstract class HabitRemindersUI extends JPanel {
    protected JScrollPane parentPreset;
    protected JPanel presetPanel;
    protected JScrollPane parentCustomization;
    protected JPanel customizationPanel;
    protected JScrollPane parentReminderList;
    protected JPanel reminderListPanel;
    protected JTabbedPane tabbedPane;
    protected Habit habit;

    public HabitRemindersUI(Habit habit) {
        this.habit = habit;
        UIManager.put("TabbedPane.selected", APP_COLOUR.brighter().brighter().brighter());
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new GridLayout(1, 1));
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setBackground(APP_COLOUR);
        setupTabbedPane();
        setupTabs();
        add(tabbedPane);
    }

    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane.setBackground(APP_COLOUR);
        tabbedPane.setForeground(FONT_COLOUR);
        tabbedPane.setFont(MEDIUM_FONT);
    }

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

    private void setupCustomizationFrequencyListener(JComboBox<String> customizationFrequency) {
        customizationFrequency.addActionListener(e -> invokeLater(() -> {
            int index = customizationFrequency.getSelectedIndex();
            updateCustomizationComponents(index);
            if (index != 0) {
                generateCustomizationForm(index);
            }
        }));
    }

    protected abstract JPanel setupCustomizationRow(int index);

    protected abstract boolean setCustomReminders(int frequency);

    private void generateCustomizationForm(int frequency) {
        customizationPanel.remove(5);
        for (int i = 0; i < frequency; i++) {
            JPanel row = setupCustomizationRow(i);
            customizationPanel.add(row, getCustomizationRowConstraints(i + 5));
        }
        JButton submit = new JButton("Set Notifications");
        makeButton(submit, WINDOW_WIDTH - SIDE_BAR_WIDTH, 50, MEDIUM_FONT);
        setupCustomizationButtonListener(submit, frequency);
        customizationPanel.add(submit, getSubmitButtonConstraints());
        addEmptySpace(customizationPanel);
    }

    private void setupCustomizationButtonListener(JButton submit, int frequency) {
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

    protected String getMinuteString(JSpinner minutes) {
        int min = (int) minutes.getValue();
        if (min < 10) {
            return "0" + min;
        } else {
            return minutes.getValue().toString();
        }
    }

    private GridBagConstraints getCustomizationRowConstraints(int index) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = index;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

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

    protected abstract void setupPresetComponents();

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

    private void getFrequencyOptions(String[] frequencyOptions, int max) {
        frequencyOptions[0] = "Number of Notifications";
        for (int i = 0; i < max; i++) {
            frequencyOptions[i + 1] = Integer.toString(i + 1);
        }
    }

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

    protected abstract void setupCustomizationComponents(int startingIndex);

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

    private void setupReminderListPanel() {
        reminderListPanel = new JPanel();
        reminderListPanel.setLayout(new GridBagLayout());
        reminderListPanel.setBackground(APP_COLOUR);
        JPanel reminderListTitle = getReminderListTitle();
        reminderListPanel.add(reminderListTitle, getReminderListTitleConstraints());
        if (!habit.isNotifyEnabled()) {
            reminderListPanel.add(setupNoRemindersPanel(), getNoRemindersConstraints());
        } else {
            setupReminderListComponents();
        }
        addEmptySpace(reminderListPanel);
        parentReminderList.setViewportView(reminderListPanel);
    }

    protected abstract void setupReminderListComponents();

    private GridBagConstraints getReminderListTitleConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

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

    private void setupToggleButtonListener(JToggleButton toggleButton) {
        toggleButton.addItemListener(e -> invokeLater(this::toggleNotifications));
    }

    private void toggleNotifications() {
        HabitManagerUI.changeMade();
        habit.setNotifyEnabled(!habit.isNotifyEnabled());
        updateRemindersUI();
    }

    private JButton getDefaultButton() {
        JButton setDefaults = new JButton("Revert to Default Notifications");
        makeButton(setDefaults, WINDOW_WIDTH - SIDE_BAR_WIDTH, 50, MEDIUM_FONT);
        setDefaults.setForeground(FONT_COLOUR);
        setDefaults.setEnabled(!habit.getHabitReminder().isDefault());
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

    private void addEmptySpace(JPanel panel) {
        JPanel empty = new JPanel();
        empty.setBackground(APP_COLOUR);
        empty.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50));
        GridBagConstraints constraints = getEmptySpaceConstraints();
        panel.add(empty, constraints);
    }

    private GridBagConstraints getEmptySpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weighty = 1;
        constraints.weightx = 1;
        return constraints;
    }

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

    protected GridBagConstraints getPresetRowConstraints(int index) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = index;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    protected void setupSpinner(JSpinner spinner) {
        spinner.setFont(MEDIUM_FONT);
        spinner.getEditor().getComponent(0).setBackground(APP_COLOUR);
        spinner.getEditor().getComponent(0).setForeground(FONT_COLOUR);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(true);
        ((NumberFormatter) ((JSpinner.NumberEditor)
                spinner.getEditor()).getTextField().getFormatter()).setAllowsInvalid(false);
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        setupSpinnerListeners(textField, spinner);
    }

    private void setupSpinnerListeners(JFormattedTextField textField, JSpinner spinner) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                invokeLater(() -> ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().selectAll());
            }
        });
    }

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

    protected void updateRemindersUI() {
        updatePresetComponents();
        updateCustomizationComponents();
        updateReminderListComponents();
        repaint();
        revalidate();
    }

    private void updatePresetComponents() {
        presetPanel.removeAll();
        setupPresetPanel();
    }

    private void updateCustomizationComponents() {
        customizationPanel.removeAll();
        setupCustomizationPanel(0);
    }

    private void updateCustomizationComponents(int startingIndex) {
        customizationPanel.removeAll();
        setupCustomizationPanel(startingIndex);
    }

    private void updateReminderListComponents() {
        reminderListPanel.removeAll();
        setupReminderListPanel();
    }
}