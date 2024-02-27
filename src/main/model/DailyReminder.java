package model;

import org.json.JSONObject;
import ui.ReminderScheduler;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

// Represents a list of daily notifications for a habit
public class DailyReminder extends HabitReminder {

    // EFFECTS: constructs a default daily reminder with given clock and habit
    public DailyReminder(Clock clock, Habit habit) {
        super(clock, habit);
        updateDefaultReminders();
    }

    // EFFECTS: constructs a daily reminder for returning users
    public DailyReminder(Set<LocalDateTime> reminders, Clock clock,
                         boolean isDefault, Habit habit, ReminderScheduler reminderScheduler) {
        this.reminders = reminders;
        this.clock = clock;
        this.isDefault = isDefault;
        this.habit = habit;
        this.reminderScheduler = reminderScheduler;
    }

    // REQUIRES: no reminders scheduled yet for this period, isDefault is true
    // MODIFIES: this
    // EFFECTS: distributes habit.getFrequency() default daily reminders starting at DAY_START_TIME
    @Override
    public void updateDefaultReminders() {
        reminders = new HashSet<>();
        double reminderInterval = (double) DAY_LENGTH / (double) habit.getFrequency();
        int hours = (int) Math.floor(reminderInterval);
        int minutes = (int) Math.round((reminderInterval - hours) * 60);
        LocalDate now = LocalDate.now(clock);
        LocalDateTime reminderDateTime = LocalDateTime.of(now, DAY_START_TIME);
        for (int i = 0; i < habit.getFrequency(); i++) {
            reminders.add(reminderDateTime);
            reminderDateTime = reminderDateTime.plusHours(hours).plusMinutes(minutes);
        }
        reminderScheduler.scheduleReminders(reminders, habit);
    }

    // REQUIRES: no reminders scheduled yet for this period, isDefault is false
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
        reminderScheduler.scheduleReminders(reminders, habit);
    }

    // EFFECTS: returns a LocalDateTime object representing the reminder at the given time today
    public static LocalDateTime makeDailyReminder(LocalTime time, Clock clock) {
        return LocalDateTime.of(LocalDate.now(clock), time);
    }

    // EFFECTS: returns DailyReminder as a JSONObject
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("reminders", remindersToJson());
        json.put("isDefault", isDefault);
        return json;
    }
}