package model;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

// Represents a list of weekly notifications for a habit
public class WeeklyReminder extends HabitReminder {

    WeeklyReminder(Clock clock) {
        super(clock);
        distributeReminders();
    }

    // MODIFIES: this
    // EFFECTS: distributes reminders once per day over the week
    @Override
    public void distributeReminders() {
        reminders = new HashSet<>();
        LocalDate now = LocalDate.now(clock);
        LocalDate sunday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDateTime reminderDateTime = LocalDateTime.of(sunday, DAY_START_TIME);
        for (int i = 0; i < 7; i++) {
            reminders.add(reminderDateTime);
            reminderDateTime = reminderDateTime.plusDays(1);
        }
        reminderScheduler.scheduleReminders(getActiveReminders());
    }

    // MODIFIES: this
    // EFFECTS: updates custom weekly reminders by adding one week to each reminder
    @Override
    public void updateCustomReminders() {
        Set<LocalDateTime> newReminders = new HashSet<>();
        for (LocalDateTime dateTime : reminders) {
            LocalDateTime newDateTime = makeWeeklyReminder(dateTime.getDayOfWeek(), dateTime.toLocalTime(), clock);
            newReminders.add(newDateTime);
        }
        reminders.clear();
        reminders = newReminders;
        reminderScheduler.scheduleReminders(getActiveReminders());
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
}
