package model;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

// Represents a list of daily notifications for a habit
public class DailyReminder extends HabitReminder {
    private int frequency;

    // EFFECTS: constructs a daily reminder with given frequency, clock, and habit
    public DailyReminder(int frequency, Clock clock, Habit habit) {
        super(clock, habit);
        this.frequency = frequency;
        distributeReminders();
    }

    // MODIFIES: this
    // EFFECTS: sets frequency to given frequency, isDefault is true, notifications redistributed
    public void setFrequency(int frequency) {
        this.frequency = frequency;
        isDefault = true;
        distributeReminders();
    }

    // EFFECTS: returns frequency of reminders per day, for testing purposes
    public int getFrequency() {
        return this.frequency;
    }

    // MODIFIES: this
    // EFFECTS: distributes daily reminders starting from DAY_START_TIME
    @Override
    public void distributeReminders() {
        reminders = new HashSet<>();
        double reminderInterval = (double) DAY_LENGTH / (double) frequency;
        int hours = (int) Math.floor(reminderInterval);
        int minutes = (int) Math.round((reminderInterval - hours) * 60);
        LocalDate now = LocalDate.now(clock);
        LocalDateTime reminderDateTime = LocalDateTime.of(now, DAY_START_TIME);
        for (int i = 0; i < frequency; i++) {
            reminders.add(reminderDateTime);
            reminderDateTime = reminderDateTime.plusHours(hours).plusMinutes(minutes);
        }
        reminderScheduler.scheduleReminders(getActiveReminders(), habit);
    }

    // MODIFIES: this
    // EFFECTS: updates custom daily reminders based on existing custom reminders
    @Override
    public void updateCustomReminders() {
        Set<LocalDateTime> newReminders = new HashSet<>();
        for (LocalDateTime dateTime : reminders) {
            LocalDateTime newDateTime = makeDailyReminder(dateTime.toLocalTime(), clock);
            newReminders.add(newDateTime);
        }
        reminders.clear();
        reminders = newReminders;
        reminderScheduler.scheduleReminders(getActiveReminders(), habit);
    }

    // EFFECTS: returns a LocalDateTime object representing the reminder at the given time today
    public static LocalDateTime makeDailyReminder(LocalTime time, Clock clock) {
        return LocalDateTime.of(LocalDate.now(clock), time);
    }
}