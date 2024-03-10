package ui;

import model.HabitManager;
import persistence.JsonReader;
import ui.card.HabitManagerUI;
import ui.card.NewUserUI;
import ui.card.StartUI;
import ui.reminder.SendReminder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static ui.Constants.*;

// Habit tracker Swing application
// Singleton pattern ensures no duplicate notifications
// and allows app to be re-opened back to the same state
public class HabitApp extends JFrame {
    private static HabitApp habitApp;

    private CardLayout cardLayout;
    private StartUI startScreen;
    private NewUserUI newUserScreen;
    private HabitManagerUI habitManagerScreen;
    private HabitManager habitManager;
    private static boolean appIsOpen;

    // EFFECTS: starts the application
    private HabitApp() {
        checkExistingInstance();
        SendReminder.setIsConsoleApp(false);
        appIsOpen = true;
        startApp();
    }

    public static void getInstance() {
        if (habitApp == null) {
            habitApp = new HabitApp();
        } else {
            habitApp.setVisible(true);
            appIsOpen = true;
        }
    }

    private void checkExistingInstance() {
        try {
            if (checkAbnormalExit()) {
                removeAllFiles();
            }
            if (INSTANCE_EXISTS.exists()) {
                SIGNAL_VISIBLE.createNewFile();
                System.exit(0);
            } else {
                INSTANCE_EXISTS.deleteOnExit();
                SIGNAL_VISIBLE.deleteOnExit();
                INSTANCE_EXISTS.createNewFile();
                setupWatchService();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkAbnormalExit() {
        if (!INSTANCE_EXISTS.exists() && !SIGNAL_VISIBLE.exists()) {
            return false;
        }
        if (INSTANCE_EXISTS.exists() && !SIGNAL_VISIBLE.exists()) {
            return false;
        }
        return true;
    }

    private void removeAllFiles() throws IOException {
        if (INSTANCE_EXISTS.exists()) {
            Files.delete(INSTANCE_EXISTS.toPath());
        }
        if (SIGNAL_VISIBLE.exists()) {
            Files.delete(SIGNAL_VISIBLE.toPath());
        }
    }

    // MODIFIES: this
    // EFFECTS: sets up a watch service to detect when the application is opened
    // Learnt from https://www.baeldung.com/java-nio2-watchservice
    private void setupWatchService() {
        Thread thread = new Thread(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path path = Paths.get("./data/signal");
                path.register(watchService, ENTRY_CREATE);
                WatchKey key;
                while (true) {
                    key = watchService.take();
                    if (SIGNAL_VISIBLE.exists()) {
                        getInstance();
                        Files.delete(SIGNAL_VISIBLE.toPath());
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    // EFFECTS: returns true if the application is open
    public static boolean appIsOpen() {
        return appIsOpen;
    }

    public static void setAppIsOpen(boolean isOpen) {
        appIsOpen = isOpen;
    }

    private void startApp() {
        cardLayout = new CardLayout();
        setupFrame();
        scaleIcons();
        JLabel logo = new JLabel(LOGO);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        startScreen = new StartUI();
        startScreen.setNewUserListener(this::onNewUser);
        startScreen.setLoadUserListener(this::loadUser);
        add(startScreen, "startScreen");
        cardLayout.show(getContentPane(), "startScreen");
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("HabitHaven");
        setIconImage(LOGO_ICON.getImage());
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(cardLayout);
    }

    private void scaleIcons() {
        Image logo = LOGO.getImage().getScaledInstance(LOGO_WIDTH, LOGO_HEIGHT, Image.SCALE_SMOOTH);
        LOGO.setImage(logo);
        Image logoIcon = TRANSPARENT_ICON.getImage()
                .getScaledInstance(SIDE_BAR_WIDTH / 3, SIDE_BAR_WIDTH / 3, Image.SCALE_SMOOTH);
        TRANSPARENT_ICON.setImage(logoIcon);
        Image delete = DELETE_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        DELETE_ICON.setImage(delete);
        Image deleteHover = DELETE_ICON_HOVER.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        DELETE_ICON_HOVER.setImage(deleteHover);
        Image add = ADD_ICON.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ADD_ICON.setImage(add);
        Image addHover = ADD_ICON_HOVER.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
        ADD_ICON_HOVER.setImage(addHover);
        scaleMoreIcons();
    }

    private void scaleMoreIcons() {
        scaleNotificationIcons();
        Image list = LIST_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        LIST_ICON.setImage(list);
        Image stats = STATS_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        STATS_ICON.setImage(stats);
        Image save = SAVE_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        SAVE_ICON.setImage(save);
        Image saveOff = SAVE_OFF_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        SAVE_OFF_ICON.setImage(saveOff);
        Image settings = SETTINGS_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        SETTINGS_ICON.setImage(settings);
        Image credits = CREDITS_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        CREDITS_ICON.setImage(credits);
        Image habit = HABIT_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        HABIT_ICON.setImage(habit);
        Image trophy = TROPHY_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        TROPHY_ICON.setImage(trophy);
        scaleAchievements();
    }

    private void scaleNotificationIcons() {
        Image bellOn = BELL_ON.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        BELL_ON.setImage(bellOn);
        Image bellOff = BELL_OFF.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        BELL_OFF.setImage(bellOff);
        Image bellOnHover = BELL_ON_HOVER.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
        BELL_ON_HOVER.setImage(bellOnHover);
        Image bellOffHover = BELL_OFF_HOVER.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        BELL_OFF_HOVER.setImage(bellOffHover);
    }

    private void scaleAchievements() {
        Image achievementOn = ACHIEVEMENT_ON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ACHIEVEMENT_ON.setImage(achievementOn);
        Image achievementOff = ACHIEVEMENT_OFF.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ACHIEVEMENT_OFF.setImage(achievementOff);
        Image bronze = BRONZE_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        BRONZE_ICON.setImage(bronze);
        Image silver = SILVER_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        SILVER_ICON.setImage(silver);
        Image gold = GOLD_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        GOLD_ICON.setImage(gold);
        Image platinum = PLATINUM_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        PLATINUM_ICON.setImage(platinum);
        Image bronzeToast = BRONZE_TOAST.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        BRONZE_TOAST.setImage(bronzeToast);
        Image silverToast = SILVER_TOAST.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        SILVER_TOAST.setImage(silverToast);
        Image goldToast = GOLD_TOAST.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        GOLD_TOAST.setImage(goldToast);
        Image platinumToast = PLATINUM_TOAST.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        PLATINUM_TOAST.setImage(platinumToast);
    }

    private void onNewUser() {
        setUpNewUserScreen();
        add(newUserScreen, "newUserScreen");
        cardLayout.show(getContentPane(), "newUserScreen");
    }

    private void setUpNewUserScreen() {
        newUserScreen = new NewUserUI();
        newUserScreen.setSubmitListener(this::onNewUserSubmit);
        newUserScreen.setBackListener(() -> {
            startScreen.enableButtons();
            cardLayout.show(getContentPane(), "startScreen");
        });
    }

    private void onNewUserSubmit() {
        HabitManager.setUsername(newUserScreen.getText());
        habitManagerScreen = new HabitManagerUI(false, this, new HabitManager());
        toHabits();
    }

    private void loadUser() {
        if (loadHabitManager()) {
            habitManagerScreen = new HabitManagerUI(true, this, habitManager);
            toHabits();
        } else {
            startScreen.enableButtons();
        }
    }

    // MODIFIES: this
    // EFFECTS: loads user data from file, updates all habits, schedules habit updates, and displays menu
    private boolean loadHabitManager() {
        JsonReader jsonReader = new JsonReader(HABIT_MANAGER_STORE);
        try {
            habitManager = jsonReader.read();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Save file not found. Please create a new user.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void toHabits() {
        add(habitManagerScreen, "habitManagerScreen");
        cardLayout.show(getContentPane(), "habitManagerScreen");
    }
}