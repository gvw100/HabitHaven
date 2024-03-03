package ui;

import model.Habit;
import model.HabitManager;
import model.Period;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static javax.swing.SwingUtilities.invokeLater;

// Represents a job to send a notification to the user
public class SendReminder implements Job {

    private String username;
    private String habitName;
    private int habitFrequency;
    private Period habitPeriod;
    private int habitNumSuccesses;
    private int habitStreak;
    private int bestStreak;
    private LocalDateTime dateTime;
    private static boolean isConsoleApp;

    // EFFECTS: constructs a new SendReminder
    public SendReminder() {
    }

    // EFFECTS: sets SendReminder.isConsoleApp to the given boolean
    public static void setIsConsoleApp(boolean isConsoleApp) {
        SendReminder.isConsoleApp = isConsoleApp;
    }

    // MODIFIES: this
    // EFFECTS: sets fields to the given habit's fields
    public void setHabit(Habit habit) {
        this.username = HabitManager.getUsername();
        this.habitName = habit.getName();
        this.habitFrequency = habit.getFrequency();
        this.habitPeriod = habit.getPeriod();
        this.habitNumSuccesses = habit.getNumSuccess();
        this.habitStreak = habit.getHabitStats().getStreak();
        this.bestStreak = habit.getHabitStats().getBestStreak();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // EFFECTS: sends a motivational reminder to the user
    @Override
    public void execute(JobExecutionContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL d, uuuu h:mm a");
        String messageTime = dateTime.format(formatter);
        String messageTitle = "\n" + messageTime + "\nHabit Reminder: " + habitName;
        String messageIntro = "Hey " + username + "! Remember to focus on your habit: "
                + habitName + "! You're on track to greatness!";
        String messageGoals = getMessageGoals();
        String messageProgress = getMessageProgress();
        String messageStreak = getMessageStreak();
        if (isConsoleApp) {
            String message = messageTitle + "\n" + messageIntro + "\n\n" + messageGoals + messageProgress
                    + "\n\n" + messageStreak;
            System.out.println(message);
        } else {
            String message = "Hey " + username + "! Only " + (habitFrequency - habitNumSuccesses)
                    + " more to go for " + habitName
                    + getPeriodString(" today.", " this week.", " this month.")
                    + (habitStreak == 0 ? " Go get that streak started!" : " Keep that streak going!");
            sendUIReminder(message);
        }
    }

    // EFFECTS: sends a desktop notification to the user
    private void sendUIReminder(String message) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = ImageIO.read(new File("./data/icon.png"));
            TrayIcon trayIcon = new TrayIcon(image, "Habit Reminder");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
            trayIcon.displayMessage("Habit Reminder: " + habitName, message, TrayIcon.MessageType.NONE);
            addListener(trayIcon);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not read icon file.");
        } catch (AWTException e) {
            e.printStackTrace();
            System.out.println("Could not display reminder.");
        }
    }

    // EFFECTS: adds a listener to the tray icon, so that the user can click on it to open the app
    private void addListener(TrayIcon trayIcon) {
        trayIcon.addActionListener(e -> {
            {
                if (!HabitApp.appIsOpen()) {
                    invokeLater(HabitApp::new);
                }
            }
        });
    }

    // EFFECTS: returns a message about the user's goals
    private String getMessageGoals() {
        String periodString = getPeriodString("day", "week", "month");
        if (habitFrequency == 1) {
            return "You're aiming to do this habit " + habitFrequency + " time per "
                    + periodString + ". ";
        } else {
            return "You're aiming to do this habit " + habitFrequency + " times per "
                    + periodString + ". ";
        }
    }

    // EFFECTS: returns a message about the user's progress
    private String getMessageProgress() {
        String times = habitNumSuccesses == 1 ? " time" : " times";
        if (habitNumSuccesses > 0) {
            return "You've already completed this habit " + habitNumSuccesses + times + "! "
                    + "Only " + (habitFrequency - habitNumSuccesses) + " more to go! Keep pushing forward!";
        } else {
            return "You haven't completed this habit yet "
                    + getPeriodString("today", "this week", "this month")
                    + ". Go get the ball rolling!";
        }
    }

    // EFFECTS: returns a message about the user's streak
    private String getMessageStreak() {
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
        switch (habitPeriod) {
            case DAILY:
                return day;
            case WEEKLY:
                return week;
            default:
                return month;
        }
    }
}