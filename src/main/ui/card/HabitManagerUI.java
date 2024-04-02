package ui.card;

import javafx.util.Pair;
import model.Habit;
import model.HabitManager;
import model.achievement.Achievement;
import model.achievement.AchievementManager;
import model.log.Event;
import model.log.EventLog;
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
    private HabitApp parent;
    private JLayeredPane layeredPane;
    private JPanel sidebar;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private AchievementToast achievementToast;
    private static boolean isSaved;
    private static HabitManager habitManager;

    // EFFECTS: constructs a new HabitMangerUI panel
    public HabitManagerUI(boolean isLoaded, HabitApp parent, HabitManager habitManager) {
        parent.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        HabitManagerUI.habitManager = habitManager;
        this.achievementToast = new AchievementToast(HabitManager.isAchievementToastsEnabled());
        this.layeredPane = new JLayeredPane();
        this.parent = parent;
        add(layeredPane);
        HabitManagerUI.isSaved = isLoaded;
        if (isLoaded) {
            updateAllHabits();
        }
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
    // EFFECTS: adds window listener to parent frame
    private void setWindowListener() {
        // MODIFIES: this
        // EFFECTS: if !isAutoSave(), user is given choice to save or not,
        //          if isHideOnClose(), parent frame is hidden and appIsOpen is set to false,
        //          otherwise, the application exits
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
                    hideOrExit();
                });
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: hides the application or exits depending on HabitManager.isHideOnClose()
    private void hideOrExit() {
        if (HabitManager.isHideOnClose()) {
            parent.setVisible(false);
            HabitApp.setAppIsOpen(false);
        } else {
            printEventLog(EventLog.getInstance());
            System.exit(0);
        }
    }

    // EFFECTS: prints all events in eventLog when application exits
    private void printEventLog(EventLog eventLog) {
        for (Event event : eventLog) {
            System.out.println(event.toString());
        }
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
        mainPanel.setLayout(cardLayout);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        mainPanel.setBackground(APP_COLOUR);
        setupSidebar();
        HabitListUI habitListUI = toHabitList();
        wholePanel.add(sidebar, BorderLayout.LINE_START);
        wholePanel.add(mainPanel, BorderLayout.CENTER);
        scheduleHabitUpdates(habitListUI);
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
    // EFFECTS: setups a gradient for the sidebar
    private void setupSidebarGradient() {
        sidebar = new JPanel() {
            // MODIFIES: this, g
            // EFFECTS: creates a gradient for the sidebar, ranging from slightly over half of the height of the sidebar
            //          to the bottom of the sidebar
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
        // MODIFIES: this
        // EFFECTS: switches cards to habit list panel
        habitList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> toHabitList());
            }
        });
    }

    // MODIFIES: save
    // EFFECTS: adds listener to save sidebar option
    private void setupSaveListener(JPanel save) {
        // MODIFIES: this
        // EFFECTS: saves habits and updates save panel accordingly
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
        // MODIFIES: this
        // EFFECTS: switches cards to create habit panel
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
        // MODIFIES: this
        // EFFECTS: switches cards to lifetime stats panel
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
        // MODIFIES: this
        // EFFECTS: switches cards to settings panel
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
        // MODIFIES: this
        // EFFECTS: switches cards to credits panel
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
            // MODIFIES: this
            // EFFECTS: sets background colour to a lighter colour
            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> option.setBackground(SIDEBAR_COLOUR.brighter().brighter()));
            }

            // MODIFIES: this
            // EFFECTS: sets background colour back to regular colour
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
    // EFFECTS: setups habit list panel and displays it
    public HabitListUI toHabitList() {
        HabitListUI habitListUI =
                new HabitListUI(habitManager, cardLayout, mainPanel, achievementToast, this::toCreateHabit);
        mainPanel.add(habitListUI, "habits");
        cardLayout.show(mainPanel, "habits");
        return habitListUI;
    }

    // EFFECTS: schedules habit updates to occur daily at midnight
    private void scheduleHabitUpdates(HabitListUI habitListUI) {
        Runnable updateAllHabits = () -> {
            updateAllHabits();
            invokeLater(habitListUI::updateHabitList);
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
}