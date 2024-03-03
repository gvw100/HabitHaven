package ui;

import model.HabitManager;

import javax.swing.*;
import java.awt.*;

import static ui.Constants.*;

// Habit tracker Swing application
public class HabitApp extends JFrame {
    private CardLayout cardLayout;
    private StartUI startScreen;
    private NewUserUI newUserScreen;
    private HabitManagerUI habitManagerScreen;
    private static boolean appIsOpen;

    // EFFECTS: starts the application
    public HabitApp() {
        SendReminder.setIsConsoleApp(false);
        appIsOpen = true;
        startApp();
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
        setLocationRelativeTo(null);
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
        Image bellOn = BELL_ON.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        BELL_ON.setImage(bellOn);
        Image bellOff = BELL_OFF.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        BELL_OFF.setImage(bellOff);
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
        habitManagerScreen = new HabitManagerUI(false, this);
        toHabits();
    }

    private void loadUser() {
        habitManagerScreen = new HabitManagerUI(true, this);
        toHabits();
    }

    private void toHabits() {
        add(habitManagerScreen, "habitManagerScreen");
        cardLayout.show(getContentPane(), "habitManagerScreen");
    }
}