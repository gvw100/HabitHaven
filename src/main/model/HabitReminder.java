package model;

import java.time.*;
import java.util.HashSet;

// Represents a list of notifications for a habit
public abstract class HabitReminder {
    protected static final LocalTime DAY_START_TIME = LocalTime.of(8, 30);
    protected static final int DAY_LENGTH = 12;
    protected HashSet<LocalDateTime> reminders;
    protected final Clock clock;
    protected boolean isDefault;

    // EFFECTS: initializes habit reminders based on period and frequency
    public HabitReminder() {
        this.isDefault = true;
        this.clock = Clock.systemDefaultZone();
    }

    // MODIFIES: this
    // EFFECTS: distributes reminders based on period and frequency
    public abstract void distributeReminders();

    // MODIFIES: this
    // EFFECTS: updates reminders after period has passed
    public void updateReminders() {
        if (!isDefault) {
            distributeReminders();
        } else {
            updateCustomReminders();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates custom reminders based on period
    public abstract void updateCustomReminders();

    // MODIFIES: this
    // EFFECTS: sets reminders to newReminders
    public void setReminders(HashSet<LocalDateTime> newReminders) {
        isDefault = false;
        reminders = newReminders;
    }

    // MODIFIES: this
    // EFFECTS: reverts back to default reminders
    public void setDefaultReminders() {
        isDefault = true;
        distributeReminders();
    }
}