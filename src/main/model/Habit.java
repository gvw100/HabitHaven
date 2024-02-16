package model;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

// Represents a habit with a name, description, frequency, period, number of successes,
// end of current period, and end of next period.
public class Habit extends HabitStatistics {
    private String name;
    private String description;
    private int frequency;
    private Period period;
    private int numSuccess;
    private LocalDateTime currentPeriodEnd;
    private LocalDateTime nextPeriodEnd;
    private Clock clock;
    private boolean isPreviousComplete;

    // REQUIRES: 0 < frequency < 16
    // EFFECTS: initializes habit
    public Habit(String name, String description, Period period, int frequency, Clock clock) {
        this.numSuccess = 0;
        this.name = name;
        this.frequency = frequency;
        this.period = period;
        this.description = description;
        this.clock = clock;
        this.isPreviousComplete = false;
        updateDateTime();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // REQUIRES: 0 < frequency < 16
    // MODIFIES: this
    // EFFECTS: set this.frequency, resets progress if frequency != this.frequency
    public void setFrequency(int frequency) {
        if (this.frequency == frequency) {
            return;
        }
        this.frequency = frequency;
        resetProgress();
    }

    // MODIFIES: this
    // EFFECTS: set this.period, resets progress if period != this.period , updates currentPeriodEnd and nextPeriodEnd
    public void setPeriod(Period period) {
        if (this.period == period) {
            return;
        }
        this.period = period;
        resetProgress();
        updateDateTime();
    }

    // MODIFIES: this
    // EFFECTS: set this.clock, useful for testing purposes
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    // MODIFIES: this
    // EFFECTS: set this.numSuccess, solely for testing purposes
    public void setNumSuccess(int numSuccess) {
        this.numSuccess = numSuccess;
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

    public int getNumSuccess() {
        return this.numSuccess;
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
    // EFFECTS: first updates habit based on current date time, then
    //          if numSuccess < frequency, increments numSuccess and
    //          updates habit statistics, returns whether habit was incremented
    public boolean finishHabit() {
        updateHabit();
        if (numSuccess < frequency) {
            numSuccess++;
            super.incrementNumSuccess();
            checkPeriodComplete();
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: if isPreviousComplete(), then increments both super.numPeriodSuccess and super.streak
    public void checkPeriodComplete() {
        if (isPeriodComplete()) {
            isPreviousComplete = true;
            incrementNumPeriodSuccess();
            incrementStreak();
        }
    }

    // REQUIRES: LocalDateTime.now(clock) is after currentPeriodEnd
    // MODIFIES: this
    // EFFECTS: updates currentPeriodEnd and nextPeriodEnd, resets numSuccess to 0, increments super.numPeriod
    public void nextHabitPeriod() {
        updateDateTime();
        numSuccess = 0;
        incrementNumPeriod();
    }

    // MODIFIES: this
    // EFFECTS: resets numSuccess to 0 and resets habit statistics
    public void resetProgress() {
        numSuccess = 0;
        super.resetStats();
    }

    // MODIFIES: this
    // EFFECTS: updates currentPeriodEnd, nextPeriodEnd, and habit statistics based on current date time
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
                super.resetStreak();
            }
            isPreviousComplete = false;
        } else if (!now.isBefore(nextPeriodEnd.plusMinutes(1))) {
            nextHabitPeriod();
            super.resetStreak();
            isPreviousComplete = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: updates currentPeriodEnd and nextPeriodEnd based on period
    public void updateDateTime() {
        switch (period) {
            case DAILY:
                updateDaily(clock);
                break;
            case WEEKLY:
                updateWeekly(clock);
                break;
            case MONTHLY:
                updateMonthly(clock);
        }
    }

    // REQUIRES: this.getPeriod() == Period.DAILY
    // MODIFIES: this
    // EFFECTS: sets currentPeriodEnd to 23:59 today nextPeriodEnd to 23:59 tomorrow
    private void updateDaily(Clock clock) {
        LocalDateTime now = LocalDateTime.now(clock);
        currentPeriodEnd = now.withHour(23).withMinute(59).withSecond(0).withNano(0);
        nextPeriodEnd = now.plusDays(1).withHour(23).withMinute(59).withSecond(0).withNano(0);
    }

    // REQUIRES: this.getPeriod() == Period.WEEKLY
    // MODIFIES: this
    // EFFECTS: sets currentPeriodEnd to 23:59 this Saturday and sets nextPeriodEnd to 23:59 next Saturday
    private void updateWeekly(Clock clock) {
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
    private void updateMonthly(Clock clock) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        currentPeriodEnd = lastDayOfMonth.withHour(23).withMinute(59).withSecond(0).withNano(0);
        LocalDateTime firstDayOfNextMonth = now.with(TemporalAdjusters.firstDayOfNextMonth());
        LocalDateTime lastDayOfNextMonth = firstDayOfNextMonth.with(TemporalAdjusters.lastDayOfMonth());
        nextPeriodEnd = lastDayOfNextMonth.withHour(23).withMinute(59).withSecond(0).withNano(0);
    }
}