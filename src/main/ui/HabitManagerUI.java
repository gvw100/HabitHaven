package ui;

import model.Habit;
import model.HabitManager;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static ui.Constants.*;

public class HabitManagerUI extends JPanel {
    private JPanel sidebar;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel habitsPanel;
    private JScrollPane scrollPane;

    private static final String HABIT_MANAGER_STORE = "./data/habitManager.json";
    private static boolean isSaved;
    private HabitManager habitManager;

    public HabitManagerUI(boolean isSaved, JFrame frame) {
        setIsSaved(isSaved);
        if (isSaved) {
            loadHabitManager();
        } else {
            habitManager = new HabitManager();
        }
        scheduleHabitUpdates();
        setupPanel();
        setupWindowListener(frame);
    }

    public static void setIsSaved(boolean isSaved) {
        HabitManagerUI.isSaved = isSaved;
    }

    private void setupWindowListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (!isSaved) {
                    int result = JOptionPane.showConfirmDialog(frame,
                            "Would you like to save your habits before exiting?", "Save Habits?",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        exitSaveHabits();
                    }
                }
                HabitApp.setAppIsOpen(false);
            }
        });
    }

    private void setupPanel() {
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        setBackground(APP_COLOUR);
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        setupSidebar();
        setupHabitsUI();
        add(sidebar, BorderLayout.LINE_START);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new FlowLayout());
        sidebar.setBackground(SIDEBAR_COLOUR);
        sidebar.setPreferredSize(new Dimension(SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        JLabel logo = new JLabel(TRANSPARENT_ICON);
        sidebar.add(logo);
        JPanel createHabit = setupSidebarOption("Create Habit");
        JPanel habitList = setupSidebarOption("Habit List");
        JPanel lifetimeStats = setupSidebarOption("Lifetime Stats");
        JPanel save = setupSidebarOption("Save to File");
        JPanel settings = setupSidebarOption("Settings");
        JPanel credits = setupSidebarOption("Credits");
        setupSidebarOptionListeners(createHabit, habitList, lifetimeStats, save, settings, credits);
        sidebar.add(createHabit);
        sidebar.add(habitList);
        sidebar.add(lifetimeStats);
        sidebar.add(save);
        sidebar.add(settings);
        sidebar.add(credits);
    }

    private void setupSidebarOptionListeners(JPanel createHabit, JPanel habitList, JPanel lifetimeStats,
                                             JPanel save, JPanel settings, JPanel credits) {
        setupCreateHabitListener(createHabit);
        setupHabitListListener(habitList);
        setupLifetimeStatsListener(lifetimeStats);
        setupSaveListener(save);
        setupSettingsListener(settings);
        setupCreditsListener(credits);
    }

    private void setupHabitListListener(JPanel habitList) {
        habitList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toHabitList();
            }
        });
    }

    public void toHabitList() {
        updateHabitList();
        cardLayout.show(mainPanel, "habits");
    }

    private void setupSaveListener(JPanel save) {
        save.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                manualSaveHabits(save, (JLabel) save.getComponent(0));
            }
        });
    }

    private void manualSaveHabits(JPanel save, JLabel saveText) {
        saveText.setText("Saving...");
        save.update(save.getGraphics());
        JsonWriter jsonWriter = new JsonWriter(HABIT_MANAGER_STORE);
        try {
            jsonWriter.open();
            jsonWriter.write(habitManager);
            jsonWriter.close();
            saveText.setText("Saved!");
            save.update(save.getGraphics());
            Thread.sleep(1000);
            saveText.setText("Save to File");
            save.update(save.getGraphics());
            isSaved = true;
        } catch (FileNotFoundException e1) {
            System.out.println("Unable to write to file: " + HABIT_MANAGER_STORE);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }

    private void exitSaveHabits() {
        JsonWriter jsonWriter = new JsonWriter(HABIT_MANAGER_STORE);
        try {
            jsonWriter.open();
            jsonWriter.write(habitManager);
            jsonWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + HABIT_MANAGER_STORE);
        }
    }

    private void setupCreateHabitListener(JPanel createHabit) {
        createHabit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                createHabit();
            }
        });
    }

    private void createHabit() {
        CreateHabitUI createHabitUI = new CreateHabitUI(habitManager, this);
        mainPanel.add(createHabitUI, "createHabit");
        cardLayout.show(mainPanel, "createHabit");
    }

    private void setupLifetimeStatsListener(JPanel lifetimeStats) {
        lifetimeStats.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });
    }

    private void setupSettingsListener(JPanel settings) {
        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });
    }

    private void setupCreditsListener(JPanel credits) {
        credits.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });
    }

    private JPanel setupSidebarOption(String text) {
        JPanel option = new JPanel();
        option.setLayout(new GridLayout(1, 1));
        option.setBackground(SIDEBAR_COLOUR);
        option.setPreferredSize(new Dimension(SIDE_BAR_WIDTH, 50));
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(MEDIUM_FONT);
        textLabel.setForeground(FONT_COLOUR);
        option.add(textLabel);
        setupSidebarListener(option);
        return option;
    }

    private void setupSidebarListener(JPanel option) {
        option.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                option.setBackground(SIDEBAR_COLOUR.brighter().brighter());
                option.setForeground(Color.lightGray);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                option.setBackground(SIDEBAR_COLOUR);
                option.setForeground(FONT_COLOUR);
            }
        });
    }

    private void updateHabitList() {
        habitsPanel.removeAll();
        setupRows();
        updateUI();
    }

    private void setupHabitsUI() {
        mainPanel.setLayout(cardLayout);
        habitsPanel = new JPanel();
        habitsPanel.setLayout(new GridBagLayout());
        setupRows();
        scrollPane.setViewportView(habitsPanel);
        mainPanel.add(scrollPane, "habits");
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        mainPanel.setBackground(APP_COLOUR);
        habitsPanel.setBackground(APP_COLOUR);
        cardLayout.show(mainPanel, "habits");
    }

    private void setupRows() {
        List<Habit> habits = habitManager.getHabits();
        setupFirstRows();
        for (Habit habit : habits) {
            setupRow(habit);
        }
        if (habits.size() == 0) {
            JPanel noHabits = new JPanel();
            noHabits.setLayout(new FlowLayout());
            JLabel label = setupLabel("No habits yet!");
            label.setFont(BIG_FONT);
            noHabits.add(label);
            noHabits.setBackground(APP_COLOUR);
            noHabits.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 100));
            GridBagConstraints constraints = getNoHabitsConstraints();
            habitsPanel.add(noHabits, constraints);
        }
        JPanel emptySpace = makeGap(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50);
        emptySpace.setBackground(APP_COLOUR);
        GridBagConstraints constraints = getEmptySpaceConstraints();
        habitsPanel.add(emptySpace, constraints);
    }

    private GridBagConstraints getEmptySpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 1;
        return constraints;
    }

    private GridBagConstraints getNoHabitsConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    private void setupRow(Habit habit) {
        JPanel empty = makeGap(WINDOW_WIDTH - SIDE_BAR_WIDTH, 1);
        GridBagConstraints fillingConstraints = getHabitRowFillingConstraints();
        habitsPanel.add(empty, fillingConstraints);
        JPanel namePanel = setupHabitPanel(habit.getName(), habit.isPeriodComplete());
        JPanel completedPanel =
                setupHabitPanel(habit.getNumSuccess() + " / " + habit.getFrequency(), habit.isPeriodComplete());
        JPanel periodPanel = setupHabitPanel(getPeriodString(habit), habit.isPeriodComplete());
        JPanel notificationsPanel = setupNotificationsPanel(habit);
        JPanel deletePanel = setupDeletePanel(habit);
        HabitRowListener listener = new HabitRowListener(new JPanel[]
                {namePanel, completedPanel, periodPanel, notificationsPanel, deletePanel}, habit);
        addCustomListener(namePanel, completedPanel, periodPanel, notificationsPanel, deletePanel, listener);
        habitsPanel.add(namePanel, getHabitElementConstraints(0));
        habitsPanel.add(completedPanel, getHabitElementConstraints(1));
        habitsPanel.add(periodPanel, getHabitElementConstraints(2));
        habitsPanel.add(notificationsPanel, getHabitElementConstraints(3));
        habitsPanel.add(deletePanel, getHabitElementConstraints(4));
    }

    private void addCustomListener(JPanel namePanel, JPanel completedPanel, JPanel periodPanel,
                                   JPanel notificationsPanel, JPanel deletePanel, HabitRowListener listener) {
        namePanel.addMouseListener(listener);
        completedPanel.addMouseListener(listener);
        periodPanel.addMouseListener(listener);
        notificationsPanel.addMouseListener(listener);
        deletePanel.addMouseListener(listener);
    }

    private GridBagConstraints getHabitRowFillingConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    private GridBagConstraints getHabitElementConstraints(int x) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.ipady = 20;
        return constraints;
    }

    private String getPeriodString(Habit habit) {
        String lower = habit.getPeriod().toString().toLowerCase();
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }

    private JPanel setupNotificationsPanel(Habit habit) {
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new FlowLayout());
        notificationsPanel.setBackground(
                habit.isPeriodComplete() ? Color.GREEN.darker().darker().darker() : APP_COLOUR);
        notificationsPanel.add(setupNotificationsLabel(habit));
        notificationsPanel.setPreferredSize(new Dimension(30, 50));
        return notificationsPanel;
    }

    private JLabel setupNotificationsLabel(Habit habit) {
        JLabel notifications = new JLabel(habit.isNotifyEnabled() ? BELL_ON : BELL_OFF);
        notifications.setFont(MEDIUM_FONT);
        notifications.setPreferredSize(new Dimension(30, 30));
        setupNotificationsListener(notifications, habit);
        return notifications;
    }

    private void setupNotificationsListener(JLabel notifications, Habit habit) {
        notifications.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String message = habit.isNotifyEnabled() ? "Disable notifications?" : "Enable notifications?";
                if (JOptionPane.showConfirmDialog(null, message, "Notifications", JOptionPane.YES_NO_OPTION)
                        == JOptionPane.YES_OPTION) {
                    habit.setNotifyEnabled(!habit.isNotifyEnabled());
                    setIsSaved(false);
                    updateHabitList();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                ImageIcon icon = habit.isNotifyEnabled() ? BELL_ON : BELL_OFF;
                ImageIcon resized = new ImageIcon(icon.getImage().getScaledInstance(
                        30, 30, Image.SCALE_SMOOTH));
                notifications.setIcon(new ImageIcon(resized.getImage()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                notifications.setIcon(habit.isNotifyEnabled() ? BELL_ON : BELL_OFF);
            }
        });
    }

    private JPanel setupDeletePanel(Habit habit) {
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new FlowLayout());
        deletePanel.setBackground(habit.isPeriodComplete() ? Color.GREEN.darker().darker().darker() : APP_COLOUR);
        deletePanel.add(setupDeleteLabel(habit));
        deletePanel.setPreferredSize(new Dimension(30, 50));
        return deletePanel;
    }

    private JLabel setupDeleteLabel(Habit habit) {
        JLabel delete = new JLabel(DELETE_ICON);
        delete.setFont(MEDIUM_FONT);
        delete.setPreferredSize(new Dimension(30, 30));
        delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this habit?",
                        "Delete Habit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    habitManager.deleteHabit(habit);
                    setIsSaved(false);
                    updateHabitList();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                delete.setIcon(DELETE_ICON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                delete.setIcon(DELETE_ICON);
            }
        });
        return delete;
    }

    private JPanel makeGap(int width, int height) {
        JPanel empty = new JPanel();
        empty.setBackground(FONT_COLOUR);
        empty.setMaximumSize(new Dimension(width, height));
        empty.setMinimumSize(new Dimension(width, height));
        empty.setPreferredSize(new Dimension(width, height));
        return empty;
    }

    private void setupFirstRows() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        JLabel title = new JLabel(HabitManager.getUsername() + "'s Habits");
        title.setFont(BIG_FONT);
        title.setForeground(FONT_COLOUR);
        titlePanel.add(title);
        titlePanel.setBackground(APP_COLOUR);
        GridBagConstraints titleConstraints = getTitleConstraints();
        habitsPanel.add(titlePanel, titleConstraints);
        addHeadingRow();
    }

    private void addHeadingRow() {
        GridBagConstraints nameConstraints = getHeadingElementConstraints(0);
        GridBagConstraints completedConstraints = getHeadingElementConstraints(1);
        GridBagConstraints periodConstraints = getHeadingElementConstraints(2);
        GridBagConstraints notificationConstraints = getHeadingElementConstraints(3);
        GridBagConstraints addConstraints = getHeadingElementConstraints(4);
        JPanel namePanel = setupHeadingElementPanel();
        JPanel completedPanel = setupHeadingElementPanel();
        JPanel periodPanel = setupHeadingElementPanel();
        JPanel notificationsPanel = setupHeadingElementPanel();
        namePanel.add(setupLabel("Habit Name"));
        completedPanel.add(setupLabel("Progress"));
        periodPanel.add(setupLabel("Period"));
        notificationsPanel.add(setupLabel("Notifications"));

        habitsPanel.add(namePanel, nameConstraints);
        habitsPanel.add(completedPanel, completedConstraints);
        habitsPanel.add(periodPanel, periodConstraints);
        habitsPanel.add(notificationsPanel, notificationConstraints);
        habitsPanel.add(setupAddPanel(), addConstraints);
    }

    private JPanel setupHeadingElementPanel() {
        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new FlowLayout());
        headingPanel.setBackground(APP_COLOUR);
        return headingPanel;
    }

    private GridBagConstraints getTitleConstraints() {
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.gridy = 0;
        titleConstraints.gridwidth = 5;
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        return titleConstraints;
    }

    private GridBagConstraints getHeadingElementConstraints(int x) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    private JPanel setupAddPanel() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new FlowLayout());
        addPanel.setBackground(APP_COLOUR);
        addPanel.setPreferredSize(new Dimension(65, 65));
        addPanel.add(setupAddLabel());
        return addPanel;
    }

    private JLabel setupAddLabel() {
        JLabel add = new JLabel(ADD_ICON);
        add.setFont(MEDIUM_FONT);
        add.setPreferredSize(new Dimension(50, 50));
        add.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                createHabit();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                add.setIcon(ADD_ICON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                add.setIcon(ADD_ICON);
            }
        });
        return add;
    }

    private JLabel setupLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        return label;
    }

    private JPanel setupHabitPanel(String text, boolean isPeriodComplete) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(isPeriodComplete ? Color.GREEN.darker().darker().darker() : APP_COLOUR);
        panel.setPreferredSize(new Dimension((WINDOW_WIDTH - SIDE_BAR_WIDTH) / 6, 50));
        JLabel label = new JLabel(text);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        label.setBackground(isPeriodComplete ? Color.GREEN.darker().darker().darker() : APP_COLOUR);
        panel.add(label);
        return panel;
    }

    // EFFECTS: schedules habit updates to occur daily at midnight
    private void scheduleHabitUpdates() {
        Runnable updateAllHabits = () -> {
            updateAllHabits();
            updateHabitList();
        };
        JobDataMap data = new JobDataMap();
        data.put("updateHabits", updateAllHabits);
        JobDetail job = newJob(UpdateHabits.class)
                .usingJobData(data)
                .build();
        Trigger trigger = newTrigger()
                .withSchedule(dailyAtHourAndMinute(0, 0)
                        .withMisfireHandlingInstructionFireAndProceed())
                .forJob(job)
                .build();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates all habits in habit manager based on current time
    private void updateAllHabits() {
        for (Habit habit : habitManager.getHabits()) {
            habit.updateHabit();
        }
    }

    // MODIFIES: this
    // EFFECTS: loads user data from file, updates all habits, schedules habit updates, and displays menu
    private void loadHabitManager() {
        JsonReader jsonReader = new JsonReader(HABIT_MANAGER_STORE);
        try {
            habitManager = jsonReader.read();
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + HABIT_MANAGER_STORE);
            System.exit(-1);
        }
        updateAllHabits();
        scheduleHabitUpdates();
    }

    private class HabitRowListener extends MouseAdapter {
        JPanel[] panels;
        Habit habit;

        public HabitRowListener(JPanel[] panels, Habit habit) {
            this.panels = panels;
            this.habit = habit;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            HabitUI habitUI = new HabitUI(habit);
            mainPanel.add(habitUI, "habit");
            cardLayout.show(mainPanel, "habit");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            for (JPanel panel : panels) {
                panel.setBackground(
                        habit.isPeriodComplete() ? Color.GREEN.darker().darker() :
                                APP_COLOUR.brighter().brighter().brighter());
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            for (JPanel panel : panels) {
                panel.setBackground(habit.isPeriodComplete() ? Color.GREEN.darker().darker().darker() : APP_COLOUR);
            }
        }
    }
}