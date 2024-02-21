package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;

// Represents a list of weekly notifications for a habit
public class WeeklyReminder extends HabitReminder {

    WeeklyReminder() {
        super();
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
    }

    // MODIFIES: this
    // EFFECTS: updates custom weekly reminders by adding one week to each reminder
    @Override
    public void updateCustomReminders() {
        HashSet<LocalDateTime> newReminders = new HashSet<>();
        for (LocalDateTime dateTime : reminders) {
            newReminders.add(dateTime.plusWeeks(1));
        }
        reminders.clear();
        reminders = newReminders;
    }
}
