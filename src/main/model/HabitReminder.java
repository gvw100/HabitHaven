package model;

import org.json.JSONArray;
import org.json.JSONObject;
import ui.ReminderScheduler;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

// Represents a list of customizable notifications for a habit
public abstract class HabitReminder {
    protected static final LocalTime DAY_START_TIME = LocalTime.of(9, 0);
    protected static final int DAY_LENGTH = 12;
    protected Set<LocalDateTime> reminders;
    protected Clock clock;
    protected boolean isDefault;
    protected Habit habit;
    protected ReminderScheduler reminderScheduler;

    // EFFECTS: constructs a default habit reminder with given clock and habit
    public HabitReminder(Clock clock, Habit habit) {
        this.isDefault = true;
        this.clock = clock;
        this.habit = habit;
        this.reminderScheduler = new ReminderScheduler();
    }

    // EFFECTS: default constructor for habit reminder
    public HabitReminder() {
    }

    public Set<LocalDateTime> getReminders() {
        return this.reminders;
    }

    // MODIFIES: this
    // EFFECTS: sets the clock to the given clock, solely for testing purposes
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    // EFFECTS: returns true if the reminder is default, false if it is custom
    public boolean isDefault() {
        return isDefault;
    }

    // REQUIRES: isDefault is true
    // MODIFIES: this
    // EFFECTS: distributes default reminders into this.reminders
    public abstract void distributeReminders();

    // MODIFIES: this
    // EFFECTS: cancels existing reminders, then updates reminders based on current time
    public void updateReminders() {
        cancelReminders();
        if (isDefault) {
            distributeReminders();
        } else {
            updateCustomReminders();
        }
    }

    // REQUIRES: isDefault is false
    // MODIFIES: this
    // EFFECTS: updates custom reminders to match the current time
    public abstract void updateCustomReminders();

    // EFFECTS: returns set of active reminders, reminders that are after the current time
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
    // EFFECTS: sets reminders to newReminders, cancels old reminders, schedules new reminders,
    //          cannot be called for instances of MonthlyReminder to avoid method overriding issues
    public void setCustomReminders(Set<LocalDateTime> newReminders) {
        cancelReminders();
        isDefault = false;
        reminders = newReminders;
        reminderScheduler.scheduleReminders(getActiveReminders(), habit);
    }

    // MODIFIES: this
    // EFFECTS: reverts back to default reminders, cancels existing default reminders
    public void setDefaultReminders() {
        cancelReminders();
        isDefault = true;
        distributeReminders();
    }

    // MODIFIES: this
    // EFFECTS: cancels all reminders in the current period
    public void cancelReminders() {
        for (LocalDateTime reminder : getActiveReminders()) {
            String jobId = reminder.toString();
            String groupId = habit.getId().toString();
            reminderScheduler.cancelReminder(jobId, groupId);
        }
    }

    // EFFECTS: returns a JSON representation of this
    public abstract JSONObject toJson();

    public JSONArray remindersToJson() {
        JSONArray jsonArray = new JSONArray();
        for (LocalDateTime dt : reminders) {
            jsonArray.put(reminderToJson(dt));
        }
        return jsonArray;
    }

    public JSONObject reminderToJson(LocalDateTime dt) {
        JSONObject json = new JSONObject();
        json.put("dateTime", dt.toString());
        return json;
    }
}