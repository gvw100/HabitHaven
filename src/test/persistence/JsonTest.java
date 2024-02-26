package persistence;

import javafx.util.Pair;
import model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Citation: Code inspired by JsonSerializationDemo https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonTest {

    protected void checkHabit(Habit habit, String n, String d, Period p, int f, UUID id,
                              boolean ne, int ns, LocalDateTime cpe, LocalDateTime npe, boolean ipc) {
        assertEquals(n, habit.getName());
        assertEquals(d, habit.getDescription());
        assertEquals(p, habit.getPeriod());
        assertEquals(f, habit.getFrequency());
        assertEquals(id, habit.getId());
        assertEquals(ne, habit.isNotifyEnabled());
        assertEquals(ns, habit.getNumSuccess());
        assertEquals(cpe, habit.getCurrentPeriodEnd());
        assertEquals(npe, habit.getNextPeriodEnd());
        assertEquals(ipc, habit.isPreviousComplete());
    }

    protected void checkHabitStatistics(Habit habit, int s, int bs, int tns, int nps, int np) {
        HabitStatistics hs = habit.getHabitStats();
        assertEquals(s, hs.getStreak());
        assertEquals(bs, hs.getBestStreak());
        assertEquals(tns, hs.getTotalNumSuccess());
        assertEquals(nps, hs.getNumPeriodSuccess());
        assertEquals(np, hs.getNumPeriod());
    }

    protected void checkDailyReminder(Habit habit, int freq, Set<LocalDateTime> reminders, boolean isDefault) {
        assertEquals(freq, ((DailyReminder) habit.getHabitReminder()).getFrequency());
        checkReminder(habit, reminders, isDefault);
    }

    protected void checkWeeklyReminder(Habit habit, Set<LocalDateTime> reminders, boolean isDefault) {
        checkReminder(habit, reminders, isDefault);
    }

    protected void checkMonthlyReminder(Habit habit, Set<Pair<Integer, LocalTime>> customReminders,
                                        Set<LocalDateTime> reminders, boolean isDefault) {
        if (customReminders != null) {
            assertEquals(customReminders, ((MonthlyReminder) habit.getHabitReminder()).getCustomReminders());
        }
        checkReminder(habit, reminders, isDefault);
    }

    private void checkReminder(Habit habit, Set<LocalDateTime> reminders, boolean isDefault) {
        assertEquals(reminders, habit.getHabitReminder().getReminders());
        assertEquals(isDefault, habit.getHabitReminder().isDefault());
        assertEquals(habit, habit.getHabitReminder().getHabit());
    }
}
