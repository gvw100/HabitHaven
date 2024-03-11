package ui.card;

import javafx.util.Pair;
import model.Habit;
import model.HabitManager;
import model.achievement.Achievement;
import model.achievement.AchievementManager;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import persistence.JsonWriter;
import ui.AchievementToast;
import ui.HabitApp;
import ui.UpdateHabits;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static ui.Constants.*;

public class HabitManagerUI extends JPanel {
    private JFrame parent;
    private JLayeredPane layeredPane;
    private JPanel sidebar;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel habitsPanel;
    private JScrollPane scrollPane;
    private AchievementToast achievementToast;

    private static boolean isSaved;
    private static HabitManager habitManager;

    public HabitManagerUI(boolean isLoaded, JFrame frame, HabitManager habitManager) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        HabitManagerUI.habitManager = habitManager;
        this.achievementToast = new AchievementToast(HabitManager.isAchievementToastsEnabled());
        this.layeredPane = new JLayeredPane();
        this.parent = frame;
        add(layeredPane);
        HabitManagerUI.isSaved = isLoaded;
        if (isLoaded) {
            updateAllHabits();
        }
        scheduleHabitUpdates();
        setupPanel();
        setWindowListener();
    }

    public static void setHabitManager(HabitManager habitManager) {
        HabitManagerUI.habitManager = habitManager;
    }

    public static void changeMade() {
        if (HabitManager.isAutoSave()) {
            nonSideBarSaveHabits(habitManager);
        } else {
            setIsSaved(false);
        }
    }

    public static void setIsSaved(boolean isSaved) {
        HabitManagerUI.isSaved = isSaved;
    }

    private void setWindowListener() {
        parent.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                invokeLater(() -> {
                    if (!HabitManager.isAutoSave() && !isSaved) {
                        int result = JOptionPane.showConfirmDialog(null,
                                "Would you like to save your habits before exiting?", "Save Habits?",
                                JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.CLOSED_OPTION) {
                            return;
                        } else if (result == JOptionPane.YES_OPTION) {
                            nonSideBarSaveHabits(habitManager);
                        }
                    }
                    if (HabitManager.isHideOnClose()) {
                        parent.setVisible(false);
                        HabitApp.setAppIsOpen(false);
                    } else {
                        System.exit(0);
                    }
                });
            }
        });
    }

    private void setupPanel() {
        setLayout(new GridLayout(1, 1));
        JPanel wholePanel = new JPanel();
        wholePanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        wholePanel.setLayout(new BorderLayout());
        wholePanel.setBackground(APP_COLOUR);
        layeredPane.add(wholePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(achievementToast, JLayeredPane.PALETTE_LAYER);
        wholePanel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        achievementToast.setBounds(0, 0, WINDOW_WIDTH, 100);
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        setupSidebar();
        setupHabitsUI();
        wholePanel.add(sidebar, BorderLayout.LINE_START);
        wholePanel.add(mainPanel, BorderLayout.CENTER);
    }

    // Inspiration taken from: https://www.youtube.com/watch?v=Wlbk47TltNY
    private void setupSidebar() {
        setupSidebarGradient();
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        sidebar.setBackground(SIDEBAR_COLOUR);
        sidebar.setPreferredSize(new Dimension(SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        JLabel logo = new JLabel(TRANSPARENT_ICON);
        sidebar.add(logo);
        JPanel createHabit = setupSidebarOption("Create Habit",
                new ImageIcon(ADD_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        JPanel habitList = setupSidebarOption("Habit List", LIST_ICON);
        JPanel lifetimeStats = setupSidebarOption("Lifetime Stats", STATS_ICON);
        JPanel save = setupSidebarOption("Save to File", SAVE_ICON);
        JPanel settings = setupSidebarOption("Settings", SETTINGS_ICON);
        JPanel credits = setupSidebarOption("Credits", CREDITS_ICON);
        setupSidebarOptionListeners(createHabit, habitList, lifetimeStats, save, settings, credits);
        sidebar.add(createHabit);
        sidebar.add(habitList);
        sidebar.add(lifetimeStats);
        sidebar.add(save);
        sidebar.add(settings);
        sidebar.add(credits);
    }

    private void setupSidebarGradient() {
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, (float) 0.55 * h, SIDEBAR_COLOUR, 0, h,
                        SIDEBAR_COLOUR.brighter().brighter().brighter());
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
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
                invokeLater(() -> toHabitList());
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
                invokeLater(() -> manualSaveHabits(save, (JLabel) save.getComponent(0)));
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

    private static void nonSideBarSaveHabits(HabitManager habitManager) {
        JsonWriter jsonWriter = new JsonWriter(HABIT_MANAGER_STORE);
        try {
            jsonWriter.open();
            jsonWriter.write(habitManager);
            jsonWriter.close();
            isSaved = true;
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + HABIT_MANAGER_STORE);
        }
    }

    private void setupCreateHabitListener(JPanel createHabit) {
        createHabit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> createHabit());
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
                invokeLater(() -> {
                    LifetimeStatisticsUI lifetimeStatisticsUI = new LifetimeStatisticsUI(habitManager);
                    mainPanel.add(lifetimeStatisticsUI, "lifetimeStatistics");
                    cardLayout.show(mainPanel, "lifetimeStatistics");
                });
            }
        });
    }

    private void setupSettingsListener(JPanel settings) {
        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> {
                    SettingsUI settingsUI = new SettingsUI(habitManager, parent, achievementToast);
                    mainPanel.add(settingsUI, "settings");
                    cardLayout.show(mainPanel, "settings");
                });
            }
        });
    }

    private void setupCreditsListener(JPanel credits) {
        credits.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO
            }
        });
    }

    private JPanel setupSidebarOption(String text, ImageIcon icon) {
        JPanel option = new JPanel();
        option.setLayout(new GridLayout(1, 1));
        option.setBackground(SIDEBAR_COLOUR);
        option.setPreferredSize(new Dimension(SIDE_BAR_WIDTH, 50));
        JLabel textLabel = new JLabel(text, icon, JLabel.LEFT);
        textLabel.setFont(MEDIUM_FONT);
        textLabel.setForeground(FONT_COLOUR);
        textLabel.setIconTextGap(20);
        option.add(textLabel);
        setupSidebarListener(option);
        return option;
    }

    private void setupSidebarListener(JPanel option) {
        option.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> option.setBackground(SIDEBAR_COLOUR.brighter().brighter()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> {
                    option.setBackground(SIDEBAR_COLOUR);
                    option.setForeground(FONT_COLOUR);
                });
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
                habit.isPeriodComplete() ? SUCCESS_GREEN : APP_COLOUR);
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
                invokeLater(() -> {
                    String message = habit.isNotifyEnabled() ? "Disable notifications?" : "Enable notifications?";
                    if (JOptionPane.showConfirmDialog(null, message, "Notifications", JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION) {
                        habit.setNotifyEnabled(!habit.isNotifyEnabled());
                        changeMade();
                        updateHabitList();
                    }
                });
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> notifications.setIcon(habit.isNotifyEnabled() ? BELL_ON_HOVER : BELL_OFF_HOVER));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> notifications.setIcon(habit.isNotifyEnabled() ? BELL_ON : BELL_OFF));
            }
        });
    }

    private JPanel setupDeletePanel(Habit habit) {
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new FlowLayout());
        deletePanel.setBackground(habit.isPeriodComplete() ? SUCCESS_GREEN : APP_COLOUR);
        deletePanel.add(setupDeleteLabel(habit));
        deletePanel.setPreferredSize(new Dimension(30, 50));
        return deletePanel;
    }

    private JLabel setupDeleteLabel(Habit habit) {
        JLabel delete = new JLabel(DELETE_ICON);
        delete.setFont(MEDIUM_FONT);
        delete.setPreferredSize(new Dimension(30, 30));
        setupDeleteListener(delete, habit);
        return delete;
    }

    private void setupDeleteListener(JLabel delete, Habit habit) {
        delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> {
                    if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this habit?",
                            "Delete Habit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        habitManager.deleteHabit(habit);
                        changeMade();
                        updateHabitList();
                    }
                });
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> delete.setIcon(DELETE_ICON_HOVER));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> delete.setIcon(DELETE_ICON));
            }
        });
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
        addTitleRow();
        addHeadingRow();
    }

    private void addTitleRow() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        JLabel title = new JLabel(HabitManager.getUsername() + "'s Habits");
        title.setFont(HUGE_FONT);
        title.setForeground(FONT_COLOUR);
        titlePanel.add(title);
        titlePanel.setBackground(APP_COLOUR);
        GridBagConstraints titleConstraints = getTitleConstraints();
        habitsPanel.add(titlePanel, titleConstraints);
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
                invokeLater(() -> createHabit());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> add.setIcon(ADD_ICON_HOVER));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> add.setIcon(ADD_ICON));
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
        panel.setBackground(isPeriodComplete ? SUCCESS_GREEN : APP_COLOUR);
        panel.setPreferredSize(new Dimension((WINDOW_WIDTH - SIDE_BAR_WIDTH) / 6, 50));
        JLabel label = new JLabel(text);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        label.setBackground(isPeriodComplete ? SUCCESS_GREEN : APP_COLOUR);
        panel.add(label);
        return panel;
    }

    // EFFECTS: schedules habit updates to occur daily at midnight
    private void scheduleHabitUpdates() {
        Runnable updateAllHabits = () -> {
            updateAllHabits();
            invokeLater(this::updateHabitList);
        };
        JobDataMap data = new JobDataMap();
        data.put("updateHabits", updateAllHabits);
        JobDetail job = newJob(UpdateHabits.class)
                .withIdentity("updateHabits", "updateHabitsGroup")
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
    // EFFECTS: updates all habits in habit manager based on current time, updates achievements, displays toast if
    //          any new achievements are achieved, calls changeMade if change was made to habitManager
    private void updateAllHabits() {
        boolean changeMade = false;
        for (Habit habit : habitManager.getHabits()) {
            List<Achievement> current = habit.getAchievements();
            if (habit.updateHabit()) {
                changeMade = true;
            }
            for (Achievement achievement :
                    AchievementManager.getNewlyAchieved(current, habit.getHabitStats(), habit.getPeriod())) {
                achievementToast.add(new Pair<>(habit.getName(), achievement));
            }
        }
        if (changeMade) {
            changeMade();
        }
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
            invokeLater(() -> {
                HabitUI habitUI = new HabitUI(habit, achievementToast);
                mainPanel.add(habitUI, "habit");
                cardLayout.show(mainPanel, "habit");
            });
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            invokeLater(() -> {
                for (JPanel panel : panels) {
                    panel.setBackground(
                            habit.isPeriodComplete() ? SUCCESS_GREEN_LIGHT :
                                    APP_COLOUR_LIGHT);
                }
            });
        }

        @Override
        public void mouseExited(MouseEvent e) {
            invokeLater(() -> {
                for (JPanel panel : panels) {
                    panel.setBackground(habit.isPeriodComplete() ? SUCCESS_GREEN : APP_COLOUR);
                }
            });
        }
    }
}