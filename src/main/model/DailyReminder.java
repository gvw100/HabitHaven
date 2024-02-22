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

    public DailyReminder(int frequency, Clock clock) {
        super(clock);
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
        reminderScheduler.scheduleReminders(getActiveReminders());
    }

    // MODIFIES: this
    // EFFECTS: updates custom daily reminders by adding one day to each reminder
    @Override
    public void updateCustomReminders() {
        Set<LocalDateTime> newReminders = new HashSet<>();
        for (LocalDateTime dateTime : reminders) {
            LocalDateTime newDateTime = makeDailyReminder(dateTime.toLocalTime(), clock);
            newReminders.add(newDateTime);
        }
        reminders.clear();
        reminders = newReminders;
        reminderScheduler.scheduleReminders(getActiveReminders());
    }

    // EFFECTS: returns a LocalDateTime object representing the reminder for the given time
    public static LocalDateTime makeDailyReminder(LocalTime time, Clock clock) {
        return LocalDateTime.of(LocalDate.now(clock), time);
    }
}
