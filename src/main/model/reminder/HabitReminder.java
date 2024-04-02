package model.reminder;

import model.Habit;
import org.json.JSONArray;
import org.json.JSONObject;
import ui.reminder.ReminderScheduler;

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
        this.reminders = new HashSet<>();
        this.isDefault = true;
        this.clock = clock;
        this.habit = habit;
        this.reminderScheduler = new ReminderScheduler();
    }

    // EFFECTS: default constructor for habit reminder, called when loading from JSON for returning user
    public HabitReminder() {
    }

    public Set<LocalDateTime> getReminders() {
        return this.reminders;
    }

    public Habit getHabit() {
        return this.habit;
    }

    public Clock getClock() {
        return this.clock;
    }

    public ReminderScheduler getReminderScheduler() {
        return this.reminderScheduler;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    // EFFECTS: returns true if the reminder is default, false if it is custom
    public boolean isDefault() {
        return this.isDefault;
    }

    // REQUIRES: no reminders scheduled yet for this period, isDefault is true
    // MODIFIES: this
    // EFFECTS: distributes default reminders into this.reminders
    public abstract void updateDefaultReminders();

    // MODIFIES: this
    // EFFECTS: cancels existing reminders, then updates reminders based on current time
    public void updateReminders() {
        cancelReminders();
        if (isDefault) {
            updateDefaultReminders();
        } else {
            updateCustomReminders();
        }
    }

    // REQUIRES: no reminders scheduled yet for this period, isDefault is false
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
    // EFFECTS: sets this.reminders to newReminders, cancels old reminders, isDefault is false, schedules new reminders
    //          cannot be called for instances of MonthlyReminder to avoid method overriding issues
    public void setCustomReminders(Set<LocalDateTime> newReminders) {
        cancelReminders();
        isDefault = false;
        reminders = newReminders;
        reminderScheduler.scheduleReminders(getActiveReminders(), habit);
    }

    // MODIFIES: this
    // EFFECTS: reverts back to default reminders, cancels existing reminders
    public void setDefaultReminders() {
        isDefault = true;
        updateReminders();
    }

    // MODIFIES: this
    // EFFECTS: cancels all reminders in the current period for this.habit
    public void cancelReminders() {
        for (LocalDateTime reminder : reminders) {
            String jobId = reminder.toString();
            String groupId = habit.getId().toString();
            reminderScheduler.cancelReminder(jobId, groupId);
        }
    }

    // EFFECTS: returns HabitReminder as a JSONObject
    public abstract JSONObject toJson();

    // EFFECTS: returns reminders as a JSONArray
    protected JSONArray remindersToJson() {
        JSONArray jsonArray = new JSONArray();
        for (LocalDateTime dt : reminders) {
            jsonArray.put(reminderToJson(dt));
        }
        return jsonArray;
    }

    // EFFECTS: returns reminder as a JSONObject
    protected JSONObject reminderToJson(LocalDateTime dt) {
        JSONObject json = new JSONObject();
        json.put("dateTime", dt.toString());
        return json;
    }
}