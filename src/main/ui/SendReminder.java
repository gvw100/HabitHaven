package ui;

import model.Habit;
import model.Period;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // EFFECTS: constructs a new SendReminder
    public SendReminder() {
    }

    // MODIFIES: this
    // EFFECTS: sets fields to the given habit's fields
    public void setHabit(Habit habit) {
        this.username = HabitApp.getUsername();
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
        String periodString = obtainPeriodString("day", "week", "month");
        String messageGoals = "You're aiming to do this habit " + habitFrequency + " times per "
                + periodString + ". ";
        String messageProgress = getMessageProgress();
        String messageStreak = getMessageStreak();
        System.out.println(messageTitle + "\n" + messageIntro + "\n\n" + messageGoals + messageProgress
                + "\n\n" + messageStreak);
    }

    // EFFECTS: returns a message about the user's progress
    String getMessageProgress() {
        if (habitNumSuccesses > 0) {
            return "You've already completed this habit " + habitNumSuccesses + " times! "
                    + "Only " + (habitFrequency - habitNumSuccesses) + " more to go! Keep pushing forward!";
        } else {
            return "You haven't completed this habit yet "
                    + obtainPeriodString("today", "this week", "this month")
                    + ". Go get the ball rolling!";
        }
    }

    // EFFECTS: returns a message about the user's streak
    String getMessageStreak() {
        if (habitStreak > 0 && habitStreak < bestStreak) {
            return "With a streak of " + habitStreak + " days, you're making amazing progress! "
                    + "Keep pushing and you'll reach your best streak of " + bestStreak + " days in no time!";
        } else if (habitStreak > 0 && habitStreak == bestStreak) {
            return "With a streak of " + habitStreak + " days, you're at your best streak! "
                    + "Keep pushing and you'll reach new heights!";
        } else {
            return "You're just getting started! Get that streak started and you'll be unstoppable!";
        }
    }

    // EFFECTS: returns a string based on the habit's period
    String obtainPeriodString(String day, String week, String month) {
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