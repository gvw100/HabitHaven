package ui.reminder;

import model.Habit;
import model.HabitManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import ui.HabitApp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static javax.swing.SwingUtilities.invokeLater;

// Represents a job to send a notification to the user
public class SendReminder implements Job {

    private Habit habit;
    private LocalDateTime dateTime;
    private static boolean isConsoleApp;

    // EFFECTS: constructs a new SendReminder
    public SendReminder() {
    }

    // EFFECTS: sets SendReminder.isConsoleApp to the given boolean
    public static void setIsConsoleApp(boolean isConsoleApp) {
        SendReminder.isConsoleApp = isConsoleApp;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // EFFECTS: sends a motivational reminder to the user
    @Override
    public void execute(JobExecutionContext context) {
        if (isConsoleApp) {
            sendConsoleReminder();
        } else {
            sendUIReminder();
        }
    }

    // EFFECTS: prints a message using System.out.println, serves as a reminder
    private void sendConsoleReminder() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL d, uuuu h:mm a");
        String messageTime = dateTime.format(formatter);
        String messageTitle = "\n" + messageTime + "\nHabit Reminder: " + habit.getName();
        String messageIntro = "Hey " + HabitManager.getUsername() + "! Remember to focus on your habit: "
                + habit.getName() + "! You're on track to greatness!";
        String messageGoals = getMessageGoals();
        String messageProgress = getMessageProgress();
        String messageStreak = getMessageStreak();
        String message = messageTitle + "\n" + messageIntro + "\n\n" + messageGoals + messageProgress
                + "\n\n" + messageStreak;
        System.out.println(message);
    }

    // EFFECTS: sends a desktop notification to the user
    private void sendUIReminder() {
        String message = "Hey " + HabitManager.getUsername() + "! Only "
                + (habit.getFrequency() - habit.getNumSuccess()) + " more to go for " + habit.getName()
                + getPeriodString(" today.", " this week.", " this month.")
                + (habit.getHabitStats().getStreak() == 0
                ? " Go get that streak started!" : " Keep that streak going!");
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = ImageIO.read(new File("./data/logo_icon.png"));
            TrayIcon trayIcon = new TrayIcon(image, "Habit Reminder");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
            trayIcon.displayMessage(
                    "Habit Reminder: " + habit.getName(), message, TrayIcon.MessageType.NONE);
            addListener(trayIcon);
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }


    // EFFECTS: adds a listener to the tray icon, so that the user can click on it to open the app
    private void addListener(TrayIcon trayIcon) {
        // EFFECTS: if the app is not already open, get the existing instance to reopen the main frame
        trayIcon.addActionListener(e -> invokeLater(() -> {
            if (!HabitApp.appIsOpen()) {
                HabitApp.getInstance();
            }
        }));
    }

    // EFFECTS: returns a message about the user's goals
    private String getMessageGoals() {
        String periodString = getPeriodString("day", "week", "month");
        if (habit.getFrequency() == 1) {
            return "You're aiming to do this habit 1 time per "
                    + periodString + ". ";
        } else {
            return "You're aiming to do this habit " + habit.getFrequency() + " times per "
                    + periodString + ". ";
        }
    }

    // EFFECTS: returns a message about the user's progress
    private String getMessageProgress() {
        String times = habit.getNumSuccess() == 1 ? " time" : " times";
        if (habit.getNumSuccess() > 0) {
            return "You've already completed this habit " + habit.getNumSuccess() + times + "! "
                    + "Only " + (habit.getFrequency() - habit.getNumSuccess()) + " more to go! Keep pushing forward!";
        } else {
            return "You haven't completed this habit yet "
                    + getPeriodString("today", "this week", "this month")
                    + ". Go get the ball rolling!";
        }
    }

    // EFFECTS: returns a message about the user's streak
    private String getMessageStreak() {
        int habitStreak = habit.getHabitStats().getStreak();
        int bestStreak = habit.getHabitStats().getBestStreak();
        String day = habitStreak == 1 ? " day" : " days";
        String week = habitStreak == 1 ? " week" : " weeks";
        String month = habitStreak == 1 ? " month" : " months";
        String periodString = getPeriodString(day, week, month);
        if (habitStreak > 0 && habitStreak < bestStreak) {
            return "With a streak of " + habitStreak + periodString + ", you're making amazing progress! "
                    + "Keep pushing and you'll reach your best streak of " + bestStreak + periodString + " in no time!";
        } else if (habitStreak > 0 && habitStreak == bestStreak) {
            return "With a streak of " + habitStreak + periodString + ", you're at your best streak! "
                    + "Keep pushing and you'll reach new heights!";
        } else {
            return "You're just getting started! Get that streak started and you'll be unstoppable!";
        }
    }

    // EFFECTS: returns a string based on the habit's period
    private String getPeriodString(String day, String week, String month) {
        switch (habit.getPeriod()) {
            case DAILY:
                return day;
            case WEEKLY:
                return week;
            default:
                return month;
        }
    }
}