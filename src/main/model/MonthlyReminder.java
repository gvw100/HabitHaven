package model;

import javafx.util.Pair;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

// Represents a list of monthly notifications for a habit
public class MonthlyReminder extends HabitReminder {

    private Set<Pair<Integer, LocalTime>> customReminders;

    // EFFECTS: constructs a MonthlyReminder with clock and habit
    MonthlyReminder(Clock clock, Habit habit) {
        super(clock, habit);
        customReminders = null;
        distributeReminders();
    }

    // EFFECTS: returns set of custom reminder pairs, for testing purposes
    Set<Pair<Integer, LocalTime>> getCustomReminders() {
        return this.customReminders;
    }

    // REQUIRES: no reminders scheduled yet for this period, isDefault is true
    // MODIFIES: this
    // EFFECTS: distributes reminders once per day over the month
    @Override
    public void distributeReminders() {
        reminders = new HashSet<>();
        LocalDate now = LocalDate.now(clock);
        YearMonth yearMonth = YearMonth.now(clock);
        int daysThisMonth = yearMonth.lengthOfMonth();
        LocalDate first = now.withDayOfMonth(1);
        LocalDateTime reminderDateTime = LocalDateTime.of(first, DAY_START_TIME);
        for (int i = 0; i < daysThisMonth; i++) {
            reminders.add(reminderDateTime);
            reminderDateTime = reminderDateTime.plusDays(1);
        }
        reminderScheduler.scheduleReminders(getActiveReminders(), habit);
    }

    // REQUIRES: no reminders scheduled yet for this period, isDefault is false
    /// MODIFIES: this
    //  EFFECTS: updates custom monthly reminders based on this.customReminders,
    //           ensures that all reminders are in the current month,
    //           duplicates are ignored (February 31 becomes February 28/29, etc.)
    @Override
    public void updateCustomReminders() {
        Set<LocalDateTime> newReminders = new HashSet<>();
        for (Pair<Integer, LocalTime> reminder : customReminders) {
            int numDays = YearMonth.now(clock).lengthOfMonth();
            LocalDateTime next;
            if (reminder.getKey() > numDays) {
                next = LocalDateTime.of(LocalDate.now(clock).withDayOfMonth(numDays), reminder.getValue());
            } else {
                next = LocalDateTime.of(LocalDate.now(clock).withDayOfMonth(reminder.getKey()), reminder.getValue());
            }
            newReminders.add(next);
        }
        reminders.clear();
        reminders = newReminders;
        reminderScheduler.scheduleReminders(getActiveReminders(), habit);
    }

    // EFFECTS: throws UnsupportedOperationException, use setCustomMonthlyReminders instead
    @Override
    public void setCustomReminders(Set<LocalDateTime> newReminders) {
        throw new UnsupportedOperationException("Use setCustomMonthlyReminders instead for MonthlyReminder");
    }

    // MODIFIES: this
    // EFFECTS: sets customReminders to newReminders,
    //          cancels old reminders,
    //          sets isDefault to false,
    //          distributes custom reminders based on current time
    public void setCustomMonthlyReminders(Set<Pair<Integer, LocalTime>> newReminders) {
        cancelReminders();
        customReminders = newReminders;
        isDefault = false;
        updateCustomReminders();
    }
}