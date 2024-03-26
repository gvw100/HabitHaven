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

// Represents the main JPanel of the entire app, consisting of a sidebar and a card layout panel
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

    // EFFECTS: constructs a new HabitMangerUI panel
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

    // MODIFIES: this
    // EFFECTS: if isAutoSave(), then save habits to file, otherwise, set isSaved to false
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

    // MODIFIES: this
    // EFFECTS: adds window listener to parent frame, if !isAutoSave(), user is given choice to save or not,
    //          if isHideOnClose(), parent frame is hidden and appIsOpen is set to false,
    //          otherwise, the application exits
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

    // MODIFIES: this
    // EFFECTS: setups HabitManagerUI panel
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
    // MODIFIES: this
    // EFFECTS: setups sidebar panel
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

    // MODIFIES: this
    // EFFECTS: creates a gradient for the sidebar, ranging from slightly over half of the height of the sidebar,
    //          to the bottom of the sidebar
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

    // MODIFIES: this, createHabit, habitList, lifetimeStats, save, settings, credits
    // EFFECTS: setups listeners for each sidebar option
    private void setupSidebarOptionListeners(JPanel createHabit, JPanel habitList, JPanel lifetimeStats,
                                             JPanel save, JPanel settings, JPanel credits) {
        setupCreateHabitListener(createHabit);
        setupHabitListListener(habitList);
        setupLifetimeStatsListener(lifetimeStats);
        setupSaveListener(save);
        setupSettingsListener(settings, this);
        setupCreditsListener(credits);
    }

    // MODIFIES: habitList
    // EFFECTS: adds listener to  habit list sidebar option
    private void setupHabitListListener(JPanel habitList) {
        habitList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> toHabitList());
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: updates habit list and shows habit list panel
    public void toHabitList() {
        updateHabitList();
        cardLayout.show(mainPanel, "habits");
    }

    // MODIFIES: save
    // EFFECTS: adds listener to save sidebar option
    private void setupSaveListener(JPanel save) {
        save.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> manualSaveHabits(save, (JLabel) save.getComponent(0)));
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: save label changed to "Saving...", habitManager saved to file,
    //          save label changed to "Saved!" for 1 second, before reverting back to "Save to File"
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

    // MODIFIES: this
    // EFFECTS: saves habitManager to file
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

    // MODIFIES: createHabit
    // EFFECTS: adds listener to create habit sidebar option
    private void setupCreateHabitListener(JPanel createHabit) {
        createHabit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> toCreateHabit());
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups createHabitUI panel and switches card to createHabitUI
    private void toCreateHabit() {
        CreateHabitUI createHabitUI = new CreateHabitUI(habitManager, this);
        mainPanel.add(createHabitUI, "createHabit");
        cardLayout.show(mainPanel, "createHabit");
    }

    // MODIFIES: lifetimeStats
    // EFFECTS: adds listener to lifetime stats sidebar option
    private void setupLifetimeStatsListener(JPanel lifetimeStats) {
        lifetimeStats.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> toLifetimeStats());
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups lifetimeStatisticsUI panel and switches card to lifetimeStatisticsUI
    private void toLifetimeStats() {
        LifetimeStatisticsUI lifetimeStatisticsUI = new LifetimeStatisticsUI(habitManager);
        mainPanel.add(lifetimeStatisticsUI, "lifetimeStatistics");
        cardLayout.show(mainPanel, "lifetimeStatistics");
    }

    // MODIFIES: settings
    // EFFECTS: adds listener to settings sidebar option
    private void setupSettingsListener(JPanel settings, HabitManagerUI habitManagerUI) {
        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> toSettings(habitManagerUI));
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups settingsUI panel and switches card to settingsUI panel
    private void toSettings(HabitManagerUI habitManagerUI) {
        SettingsUI settingsUI = new SettingsUI(habitManager, parent, achievementToast, habitManagerUI);
        mainPanel.add(settingsUI, "settings");
        cardLayout.show(mainPanel, "settings");
    }

    // MODIFIES: credits
    // EFFECTS: adds listener to credits sidebar option
    private void setupCreditsListener(JPanel credits) {
        credits.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toCredits();
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: setups creditsUI panel and switches card to creditsUI panel
    private void toCredits() {
        CreditsUI creditsUI = new CreditsUI();
        mainPanel.add(creditsUI, "credits");
        cardLayout.show(mainPanel, "credits");
    }

    // EFFECTS: returns sidebar option panel with given text and icon
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

    // MODIFIES: option
    // EFFECTS: setups sidebar listener to the given sidebar option, hovering over makes background brighter
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

    // MODIFIES: this
    // EFFECTS: updates habit list panel
    private void updateHabitList() {
        habitsPanel.removeAll();
        setupRows();
        updateUI();
    }

    // MODIFIES: this
    // EFFECTS: setups habit list panel
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

    // MODIFIES: this
    // EFFECTS: adds habit rows to habit list panel
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

    // EFFECTS: returns grid bag constraints for empty space to fill the bottom of grid bag layout
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

    // EFFECTS: returns grid bag constraints of no habit message
    private GridBagConstraints getNoHabitsConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    // MODIFIES: this
    // EFFECTS: setups a row for the given habit and adds it to habitsPanel
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

    // MODIFIES: namePanel, completedPanel periodPanel, notificationsPanel, deletePanel
    // EFFECTS: adds custom listener to all components in habit row to establish hover behaviour
    private void addCustomListener(JPanel namePanel, JPanel completedPanel, JPanel periodPanel,
                                   JPanel notificationsPanel, JPanel deletePanel, HabitRowListener listener) {
        namePanel.addMouseListener(listener);
        completedPanel.addMouseListener(listener);
        periodPanel.addMouseListener(listener);
        notificationsPanel.addMouseListener(listener);
        deletePanel.addMouseListener(listener);
    }

    // EFFECTS: returns grid bag constraints for white filling at the top of each habit row
    private GridBagConstraints getHabitRowFillingConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    // EFFECTS: returns grid bag constraints for each element in habit row
    private GridBagConstraints getHabitElementConstraints(int x) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.ipady = 20;
        return constraints;
    }

    // EFFECTS: returns period string in title case ("Daily", "Weekly", etc.)
    private String getPeriodString(Habit habit) {
        String lower = habit.getPeriod().toString().toLowerCase();
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }

    // EFFECTS: returns notifications panel with clickable icon to toggle notifications on/off
    private JPanel setupNotificationsPanel(Habit habit) {
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new FlowLayout());
        notificationsPanel.setBackground(
                habit.isPeriodComplete() ? SUCCESS_GREEN : APP_COLOUR);
        notificationsPanel.add(setupNotificationsLabel(habit));
        notificationsPanel.setPreferredSize(new Dimension(30, 50));
        return notificationsPanel;
    }

    // EFFECTS: returns label containing either BELL_ON or BELL_OFF depending on whether notifications are enabled
    private JLabel setupNotificationsLabel(Habit habit) {
        JLabel notifications = new JLabel(habit.isNotifyEnabled() ? BELL_ON : BELL_OFF);
        notifications.setFont(MEDIUM_FONT);
        notifications.setPreferredSize(new Dimension(30, 30));
        setupNotificationsListener(notifications, habit);
        return notifications;
    }

    // MODIFIES: notifications
    // EFFECTS: adds listener to notifications label
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

    // EFFECTS: returns panel containing DELETE_ICON
    private JPanel setupDeletePanel(Habit habit) {
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new FlowLayout());
        deletePanel.setBackground(habit.isPeriodComplete() ? SUCCESS_GREEN : APP_COLOUR);
        deletePanel.add(setupDeleteLabel(habit));
        deletePanel.setPreferredSize(new Dimension(30, 50));
        return deletePanel;
    }

    // EFFECTS: returns delete label containing DELETE_ICON
    private JLabel setupDeleteLabel(Habit habit) {
        JLabel delete = new JLabel(DELETE_ICON);
        delete.setFont(MEDIUM_FONT);
        delete.setPreferredSize(new Dimension(30, 30));
        setupDeleteListener(delete, habit);
        return delete;
    }

    // MODIFIES: delete
    // EFFECTS: adds listener to delete label
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

    // EFFECTS: returns small horizontal white bar that serves to subdivide each habit row
    private JPanel makeGap(int width, int height) {
        JPanel empty = new JPanel();
        empty.setBackground(FONT_COLOUR);
        empty.setMaximumSize(new Dimension(width, height));
        empty.setMinimumSize(new Dimension(width, height));
        empty.setPreferredSize(new Dimension(width, height));
        return empty;
    }

    // MODIFIES: this
    // EFFECTS: setups first rows of habit list panel
    private void setupFirstRows() {
        addTitleRow();
        addHeadingRow();
    }

    // MODIFIES: this
    // EFFECTS: adds title row to habitsPanel
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

    // MODIFIES: this
    // EFFECTS: adds heading row to habitsPanel
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

    // EFFECTS: returns a JPanel for each heading element
    private JPanel setupHeadingElementPanel() {
        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new FlowLayout());
        headingPanel.setBackground(APP_COLOUR);
        return headingPanel;
    }

    // EFFECTS: returns grid bag constraints for title
    private GridBagConstraints getTitleConstraints() {
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.gridy = 0;
        titleConstraints.gridwidth = 5;
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        return titleConstraints;
    }

    /// EFFECTS: returns grid bag constraints for heading row
    private GridBagConstraints getHeadingElementConstraints(int x) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    // EFFECTS: returns JPanel containing clickable ADD_ICON
    private JPanel setupAddPanel() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new FlowLayout());
        addPanel.setBackground(APP_COLOUR);
        addPanel.setPreferredSize(new Dimension(65, 65));
        addPanel.add(setupAddLabel());
        return addPanel;
    }

    // EFFECTS: returns JLabel containing clickable ADD_ICON
    private JLabel setupAddLabel() {
        JLabel add = new JLabel(ADD_ICON);
        add.setFont(MEDIUM_FONT);
        add.setPreferredSize(new Dimension(50, 50));
        add.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> toCreateHabit());
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

    // EFFECTS: returns a label with the given text
    private JLabel setupLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        return label;
    }

    // EFFECTS: returns habit row panel, background colour is green if current period complete
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
    protected void updateAllHabits() {
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

    // Helper class to ensure that all elements in habit row have the same hover behaviour and click behaviour
    private class HabitRowListener extends MouseAdapter {
        JPanel[] panels;
        Habit habit;

        // EFFECTS: constructs a habit row listener with the given habit row elements and habit
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