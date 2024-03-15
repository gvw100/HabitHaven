package ui;

import model.HabitManager;
import persistence.JsonReader;
import ui.card.HabitManagerUI;
import ui.card.NewUserUI;
import ui.card.StartUI;
import ui.reminder.SendReminder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Habit tracker Swing application
// Singleton pattern allows app to be reopened to the same state
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

    // EFFECTS: if HabitApp not yet instantiated, create a new HabitApp,
    //          otherwise, show the existing instance
    public static void getInstance() {
        if (habitApp == null) {
            habitApp = new HabitApp();
        } else {
            habitApp.setVisible(true);
            appIsOpen = true;
        }
    }

    // MODIFIES: this
    // EFFECTS: inspiration taken from: https://stackoverflow.com/questions/41051127/javafx-single-instance-application
    //          uses sockets to check whether an instance already exists.
    //          if instance does not exist:
    //          await for incoming socket messages on a separate thread
    //          if instance already exists:
    //          BindException caught, send message to socket and exit,
    //          message to be read by existing instance,
    //          JFrame of existing instance shown
    private void checkExistingInstance() {
        try {
            ServerSocket serverSocket = new ServerSocket(SINGLE_INSTANCE_PORT);
            awaitMessages(serverSocket);
        } catch (BindException e) {
            signalVisibleMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: starts a new thread that awaits for connection to made to socket,
    //          if message is SIGNAL_VISIBLE, display the current instance
    private void awaitMessages(ServerSocket serverSocket) {
        Thread thread = new Thread(() -> {
            while (true) {
                try (Socket socket = serverSocket.accept();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String message = reader.readLine();
                    if (message != null && message.contains(SIGNAL_VISIBLE)) {
                        invokeLater(HabitApp::getInstance);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    // EFFECTS: writes SIGNAL_VISIBLE message to the socket, then exits the current instance
    private void signalVisibleMessage() {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), SINGLE_INSTANCE_PORT);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.println(SIGNAL_VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    // EFFECTS: returns true if the application is open
    public static boolean appIsOpen() {
        return appIsOpen;
    }

    public static void setAppIsOpen(boolean isOpen) {
        appIsOpen = isOpen;
    }

    // MODIFIES: this
    // EFFECTS: sets up components, frame is made visible
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

    // MODIFIES: this
    // EFFECTS: setups the frame
    private void setupFrame() {
        setTitle("HabitHaven");
        setIconImage(LOGO_ICON.getImage());
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(cardLayout);
    }

    // EFFECTS: scales icon constants used in the app
    private void scaleIcons() {
        Image logo = LOGO.getImage().getScaledInstance(LOGO_WIDTH, LOGO_HEIGHT, Image.SCALE_SMOOTH);
        LOGO.setImage(logo);
        Image icon = TRANSPARENT_ICON.getImage()
                .getScaledInstance(SIDE_BAR_WIDTH / 3, SIDE_BAR_WIDTH / 3, Image.SCALE_SMOOTH);
        TRANSPARENT_ICON.setImage(icon);
        Image delete = DELETE_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        DELETE_ICON.setImage(delete);
        Image deleteHover = DELETE_ICON_HOVER.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        DELETE_ICON_HOVER.setImage(deleteHover);
        Image add = ADD_ICON.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ADD_ICON.setImage(add);
        Image addHover = ADD_ICON_HOVER.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
        ADD_ICON_HOVER.setImage(addHover);
        Image exit = EXIT_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        EXIT_ICON.setImage(exit);
        Image hide = HIDE_ICON.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        HIDE_ICON.setImage(hide);
        scaleMoreIcons();
    }

    // EFFECTS: scales icon constants used in the app
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

    // EFFECTS: scales notification icon constants used in the app
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

    // EFFECTS: scales achievement icon constants used in the app
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

    // MODIFIES: this
    // EFFECTS: switches to new user panel
    private void onNewUser() {
        setUpNewUserScreen();
        add(newUserScreen, "newUserScreen");
        cardLayout.show(getContentPane(), "newUserScreen");
    }

    // MODIFIES: this
    // EFFECTS: setups up new user panel
    private void setUpNewUserScreen() {
        newUserScreen = new NewUserUI();
        newUserScreen.setSubmitListener(this::onNewUserSubmit);
        newUserScreen.setBackListener(() -> {
            startScreen.enableButtons();
            cardLayout.show(getContentPane(), "startScreen");
        });
    }

    // MODIFIES: this
    // EFFECTS: sets username of new user, redirects user to main application panel
    private void onNewUserSubmit() {
        HabitManager.setUsername(newUserScreen.getText());
        habitManagerScreen = new HabitManagerUI(false, this, new HabitManager());
        toHabits();
    }

    // MODIFIES: this
    // EFFECTS: tries to load habits from file, if success, redirect user to main application panel,
    //          otherwise, re-enable buttons in start screen
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

    // MODIFIES: this
    // EFFECTS: brings the user to the main application panel
    private void toHabits() {
        add(habitManagerScreen, "habitManagerScreen");
        cardLayout.show(getContentPane(), "habitManagerScreen");
    }
}