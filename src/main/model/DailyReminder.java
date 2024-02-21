package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

// Represents a list of daily notifications for a habit
public class DailyReminder extends HabitReminder {
    private int frequency;

    public DailyReminder(int frequency) {
        super();
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
    }

    // MODIFIES: this
    // EFFECTS: updates custom daily reminders by adding one day to each reminder
    @Override
    public void updateCustomReminders() {
        HashSet<LocalDateTime> newReminders = new HashSet<>();
        for (LocalDateTime dateTime : reminders) {
            newReminders.add(dateTime.plusDays(1));
        }
        reminders.clear();
        reminders = newReminders;
    }
}
