package model;

import org.json.JSONObject;
import ui.ReminderScheduler;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

// Represents a list of weekly notifications for a habit
public class WeeklyReminder extends HabitReminder {

    // EFFECTS: constructs a default weekly reminder with given clock and habit
    public WeeklyReminder(Clock clock, Habit habit) {
        super(clock, habit);
        updateDefaultReminders();
    }

    // EFFECTS: constructs a weekly reminder for returning users
    public WeeklyReminder(Set<LocalDateTime> reminders, Clock clock, boolean isDefault,
                   Habit habit, ReminderScheduler reminderScheduler) {
        this.reminders = reminders;
        this.clock = clock;
        this.isDefault = isDefault;
        this.habit = habit;
        this.reminderScheduler = reminderScheduler;
    }

    // REQUIRES: no reminders scheduled yet for this period, isDefault is true
    // MODIFIES: this
    // EFFECTS: distributes reminders once per day over the week at DAY_START_TIME
    @Override
    public void updateDefaultReminders() {
        reminders = new HashSet<>();
        LocalDate now = LocalDate.now(clock);
        LocalDate sunday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDateTime reminderDateTime = LocalDateTime.of(sunday, DAY_START_TIME);
        for (int i = 0; i < 7; i++) {
            reminders.add(reminderDateTime);
            reminderDateTime = reminderDateTime.plusDays(1);
        }
        reminderScheduler.scheduleReminders(reminders, habit);
    }

    // REQUIRES: no reminders scheduled yet for this period, isDefault is false
    // MODIFIES: this
    // EFFECTS: updates custom weekly reminders based on existing custom reminders
    @Override
    public void updateCustomReminders() {
        Set<LocalDateTime> newReminders = new HashSet<>();
        for (LocalDateTime dateTime : reminders) {
            LocalDateTime newDateTime = makeWeeklyReminder(dateTime.getDayOfWeek(), dateTime.toLocalTime(), clock);
            newReminders.add(newDateTime);
        }
        reminders.clear();
        reminders = newReminders;
        reminderScheduler.scheduleReminders(reminders, habit);
    }

    // EFFECTS: returns a LocalDateTime object representing the reminder for the given day and time
    //          with respect to the current time
    public static LocalDateTime makeWeeklyReminder(DayOfWeek day, LocalTime time, Clock clock) {
        int dayOfWeekValueTranslated = day.getValue() == 7 ? 1 : day.getValue() + 1;
        DayOfWeek today = LocalDateTime.now(clock).getDayOfWeek();
        int todayValueTranslated = today.getValue() == 7 ? 1 : today.getValue() + 1;
        LocalDate dayOfWeek;
        if (todayValueTranslated >= dayOfWeekValueTranslated) {
            dayOfWeek = LocalDate.now(clock).with(TemporalAdjusters.previousOrSame(day));
        } else {
            dayOfWeek = LocalDate.now(clock).with(TemporalAdjusters.next(day));
        }
        return LocalDateTime.of(dayOfWeek, time);
    }

    // EFFECTS: returns WeeklyReminder as a JSONObject
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("reminders", remindersToJson());
        json.put("isDefault", isDefault);
        return json;
    }
}