package model;

import ui.ReminderScheduler;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

// Represents a list of notifications for a habit
public abstract class HabitReminder {
    protected static final LocalTime DAY_START_TIME = LocalTime.of(8, 30);
    protected static final int DAY_LENGTH = 12;
    protected Set<LocalDateTime> reminders;
    protected final Clock clock;
    protected boolean isDefault;
    protected Habit habit;
    protected ReminderScheduler reminderScheduler;

    // EFFECTS: initializes habit reminders based on period and frequency
    public HabitReminder(Clock clock, Habit habit) {
        this.isDefault = true;
        this.clock = clock;
        this.habit = habit;
        this.reminderScheduler = new ReminderScheduler();
    }

    public boolean isDefault() {
        return isDefault;
    }

    // MODIFIES: this
    // EFFECTS: distributes reminders based on period and frequency
    public abstract void distributeReminders();

    // MODIFIES: this
    // EFFECTS: updates reminders after period has passed
    public void updateReminders() {
        if (isDefault) {
            distributeReminders();
        } else {
            updateCustomReminders();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates custom reminders based on period
    public abstract void updateCustomReminders();

    public Set<LocalDateTime> getActiveReminders() {
        Set<LocalDateTime> activeReminders = new HashSet<>();
        for (LocalDateTime reminder : reminders) {
            if (reminder.isAfter(LocalDateTime.now(clock))) {
                activeReminders.add(reminder);
            }
        }
        return activeReminders;
    }

    // MODIFIES: this
    // EFFECTS: sets reminders to newReminders
    public void setCustomReminders(Set<LocalDateTime> newReminders) {
        isDefault = false;
        reminders = newReminders;
        reminderScheduler.scheduleReminders(getActiveReminders(), habit);
    }

    // MODIFIES: this
    // EFFECTS: reverts back to default reminders
    public void setDefaultReminders() {
        isDefault = true;
        distributeReminders();
    }

    // MODIFIES: this
    // EFFECTS: cancels all reminders in the current period
    public void cancelReminders() {
        reminderScheduler.cancelReminders();
    }
}