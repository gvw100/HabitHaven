package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

// Represents a list of monthly notifications for a habit
public class MonthlyReminder extends HabitReminder {

    

    MonthlyReminder() {
        super();
        distributeReminders();
    }

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
    }

    /// MODIFIES: this
    //  EFFECTS: updates custom monthly reminders by adding one month to each reminder,
    //           ensures reminders are in the correct month, any duplicates are ignored
    @Override
    public void updateCustomReminders() {
        HashSet<LocalDateTime> newReminders = new HashSet<>();

        reminders.clear();
        reminders = newReminders;
    }
}
