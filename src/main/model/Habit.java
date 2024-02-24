package model;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

// Represents a habit with a name, description, frequency, period, number of success, habit statistics,
// and habit notifications
public class Habit {
    private String name;
    private String description;
    private Period period;
    private int frequency;
    private final UUID id = UUID.randomUUID();
    private boolean notifyEnabled;
    private int numSuccess;
    private final HabitStatistics habitStats;
    private HabitReminder habitReminder;
    private LocalDateTime currentPeriodEnd;
    private LocalDateTime nextPeriodEnd;
    private Clock clock;
    private boolean isPreviousComplete;

    // REQUIRES: 0 < frequency < 16
    // EFFECTS: initializes habit
    public Habit(String name, String description, Period period, int frequency, boolean notifyEnabled, Clock clock) {
        this.numSuccess = 0;
        this.name = name;
        this.frequency = frequency;
        this.period = period;
        this.description = description;
        this.notifyEnabled = notifyEnabled;
        this.clock = clock;
        this.isPreviousComplete = false;
        this.habitStats = new HabitStatistics();
        this.habitReminder = this.notifyEnabled ? getNewReminder() : null;
        updateDateTime();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // MODIFIES: this
    // EFFECTS: set this.clock, useful for testing purposes, solely for testing purposes
    //          other clocks also updated to prevent exceptions in tests
    public void setClock(Clock clock) {
        this.clock = clock;
        if (isNotifyEnabled()) {
            habitReminder.clock = clock;
        }
    }

    // MODIFIES: this
    // EFFECTS: set this.numSuccess, solely for testing purposes
    public void setNumSuccess(int numSuccess) {
        this.numSuccess = numSuccess;
    }

    // MODIFIES: this
    // EFFECTS: sets this.notifyEnabled to notifyEnabled,
    //          if notifyEnabled != this.notifyEnabled, then habitReminder is reinitialized
    //          if notifyEnabled is true, then habitReminder is reinitialized to a new reminder
    //          if notifyEnabled is false, then habitReminder is set to null and all reminders are cancelled
    //          returns whether notifyEnabled was changed
    public boolean setNotifyEnabled(boolean notifyEnabled) {
        if (notifyEnabled == this.notifyEnabled) {
            return false;
        }
        if (notifyEnabled) {
            this.notifyEnabled = true;
            habitReminder = getNewReminder();
        } else {
            this.notifyEnabled = false;
            habitReminder.cancelReminders();
            habitReminder = null;
        }
        return true;
    }

    // REQUIRES: 0 < frequency < 16
    // MODIFIES: this
    // EFFECTS: set this.frequency,
    //          if frequency != this.frequency, reset progress,
    //          if period == Period.DAILY and notifyEnabled and habitReminder is default,
    //          then habitReminders are cancelled and a new DailyReminder is created with the new frequency
    //          returns whether frequency was changed
    public boolean setFrequency(int frequency) {
        if (this.frequency == frequency) {
            return false;
        }
        this.frequency = frequency;
        resetProgress();
        if (period == Period.DAILY && isNotifyEnabled() && habitReminder.isDefault()) {
            habitReminder.cancelReminders();
            DailyReminder reminder = (DailyReminder) habitReminder;
            reminder.setFrequency(frequency);
        }
        return true;
    }

    // MODIFIES: this
    // EFFECTS: set this.period,
    //          if this.period != period, resets progress, updates currentPeriodEnd and nextPeriodEnd
    //          if notifyEnabled, then reminders are cancelled and a new reminder with default notifications is created
    //          returns whether period was changed
    public boolean setPeriod(Period period) {
        if (this.period == period) {
            return false;
        }
        this.period = period;
        resetProgress();
        updateDateTime();
        if (isNotifyEnabled()) {
            habitReminder.cancelReminders();
            habitReminder = getNewReminder();
        }
        return true;
    }

    // REQUIRES: notifyEnabled is true and no reminders have been scheduled
    // EFFECTS: returns new habit reminder with default notifications based on period
    public HabitReminder getNewReminder() {
        switch (period) {
            case DAILY:
                return new DailyReminder(frequency, clock, this);
            case WEEKLY:
                return new WeeklyReminder(clock, this);
            default:
                return new MonthlyReminder(clock, this);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public Period getPeriod() {
        return this.period;
    }

    public UUID getId() {
        return this.id;
    }

    // EFFECTS: returns whether notifications are enabled
    public boolean isNotifyEnabled() {
        return this.notifyEnabled;
    }

    public int getNumSuccess() {
        return this.numSuccess;
    }

    public HabitStatistics getHabitStats() {
        return this.habitStats;
    }

    public HabitReminder getHabitReminder() {
        return this.habitReminder;
    }

    public Clock getClock() {
        return this.clock;
    }

    public LocalDateTime getCurrentPeriodEnd() {
        return this.currentPeriodEnd;
    }

    public LocalDateTime getNextPeriodEnd() {
        return this.nextPeriodEnd;
    }

    // EFFECTS: returns whether habit period is complete
    public boolean isPeriodComplete() {
        return frequency == numSuccess;
    }

    // REQUIRES: LocalDateTime.now(clock) is after currentPeriodEnd
    // EFFECTS: returns whether previous period was completed successfully
    public boolean isPreviousComplete() {
        return this.isPreviousComplete;
    }

    // MODIFIES: this
    // EFFECTS: if numSuccess < frequency, increments numSuccess and
    //          updates habit statistics, returns whether habit was incremented
    public boolean finishHabit() {
        if (numSuccess < frequency) {
            numSuccess++;
            habitStats.incrementTotalNumSuccess();
            checkPeriodComplete();
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: if isPeriodComplete(), then increments both habitStats.numPeriodSuccess
    //          a habitStats.streak, sets isPreviousComplete to true, and cancels reminders if notifyEnabled
    public void checkPeriodComplete() {
        if (isPeriodComplete()) {
            isPreviousComplete = true;
            habitStats.incrementNumPeriodSuccess();
            habitStats.incrementStreak();
            if (isNotifyEnabled()) {
                habitReminder.cancelReminders();
            }
        }
    }

    // REQUIRES: LocalDateTime.now(clock) is after currentPeriodEnd
    // MODIFIES: this
    // EFFECTS: updates currentPeriodEnd and nextPeriodEnd, resets numSuccess to 0, increments numPeriod,
    //          if notifyEnabled, then cancels old reminders and updates to new reminders
    public void nextHabitPeriod() {
        updateDateTime();
        numSuccess = 0;
        habitStats.incrementNumPeriod();
        if (isNotifyEnabled()) {
            habitReminder.updateReminders();
        }
    }

    // MODIFIES: this
    // EFFECTS: resets numSuccess to 0 and resets habit statistics
    public void resetProgress() {
        numSuccess = 0;
        habitStats.resetStats();
    }

    // MODIFIES: this
    // EFFECTS: updates currentPeriodEnd, nextPeriodEnd, and habit statistics based on current date time
    //          a day is defined to start at 00:00
    //          if now is not after currentPeriodEnd, do nothing,
    //          if now is between currentPeriodEnd and nextPeriodEnd, but if !isPreviousComplete(),
    //          switch to next period, then reset streak,
    //          if now is between currentPeriodEnd and nextPeriodEnd and isPreviousComplete(),
    //          switch to next period and reset isPreviousComplete to false,
    //          if now is after nextPeriodEnd, switch to next period, reset streak, and reset isPreviousComplete
    public void updateHabit() {
        LocalDateTime now = LocalDateTime.now(clock);
        if (!now.isBefore(currentPeriodEnd.plusMinutes(1)) && now.isBefore(nextPeriodEnd.plusMinutes(1))) {
            nextHabitPeriod();
            if (!isPreviousComplete()) {
                habitStats.resetStreak();
            }
            isPreviousComplete = false;
        } else if (!now.isBefore(nextPeriodEnd.plusMinutes(1))) {
            nextHabitPeriod();
            habitStats.resetStreak();
            isPreviousComplete = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: updates currentPeriodEnd and nextPeriodEnd based on period
    public void updateDateTime() {
        switch (period) {
            case DAILY:
                updateDaily();
                break;
            case WEEKLY:
                updateWeekly();
                break;
            default:
                updateMonthly();
                break;
        }
    }

    // REQUIRES: this.getPeriod() == Period.DAILY
    // MODIFIES: this
    // EFFECTS: sets currentPeriodEnd to 23:59 today nextPeriodEnd to 23:59 tomorrow
    public void updateDaily() {
        LocalDateTime now = LocalDateTime.now(clock);
        currentPeriodEnd = now.withHour(23).withMinute(59).withSecond(0).withNano(0);
        nextPeriodEnd = now.plusDays(1).withHour(23).withMinute(59).withSecond(0).withNano(0);
    }

    // REQUIRES: this.getPeriod() == Period.WEEKLY
    // MODIFIES: this
    // EFFECTS: sets currentPeriodEnd to 23:59 this Saturday and sets nextPeriodEnd to 23:59 next Saturday
    public void updateWeekly() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime nextSaturday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        LocalDateTime nextNextSaturday = nextSaturday.plusDays(7);
        currentPeriodEnd = nextSaturday.withHour(23).withMinute(59).withSecond(0).withNano(0);
        nextPeriodEnd = nextNextSaturday.withHour(23).withMinute(59).withSecond(0).withNano(0);
    }

    // REQUIRES: this.getPeriod() == Period.MONTHLY
    // MODIFIES: this
    // EFFECTS: sets currentPeriodEnd to 23:59 on the last day of the month
    //          and sets nextPeriodEnd to 23:59 on the last day of next month
    public void updateMonthly() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        currentPeriodEnd = lastDayOfMonth.withHour(23).withMinute(59).withSecond(0).withNano(0);
        LocalDateTime firstDayOfNextMonth = now.with(TemporalAdjusters.firstDayOfNextMonth());
        LocalDateTime lastDayOfNextMonth = firstDayOfNextMonth.with(TemporalAdjusters.lastDayOfMonth());
        nextPeriodEnd = lastDayOfNextMonth.withHour(23).withMinute(59).withSecond(0).withNano(0);
    }
}