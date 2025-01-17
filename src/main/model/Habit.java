package model;

import model.achievement.Achievement;
import model.log.Event;
import model.log.EventLog;
import model.reminder.DailyReminder;
import model.reminder.HabitReminder;
import model.reminder.MonthlyReminder;
import model.reminder.WeeklyReminder;
import org.json.JSONObject;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static model.achievement.AchievementManager.getAchieved;

// Represents a habit with a name, description, period, frequency, number of successes, habit statistics,
// habit notifications, and habit achievements
public class Habit {
    private String name;
    private String description;
    private Period period;
    private int frequency;
    private final UUID id;
    private boolean notifyEnabled;
    private int numSuccess;
    private LocalDateTime currentPeriodEnd;
    private LocalDateTime nextPeriodEnd;
    private Clock clock;
    private boolean isPreviousComplete;
    private final HabitStatistics habitStats;
    private HabitReminder habitReminder;
    private List<Achievement> achievements;
    private boolean isArchived;

    // REQUIRES: 0 < frequency < 16
    // EFFECTS: initializes habit
    public Habit(String name, String description, Period period, int frequency, boolean notifyEnabled, Clock clock) {
        this.numSuccess = 0;
        this.name = name;
        this.frequency = frequency;
        this.period = period;
        this.description = description;
        this.notifyEnabled = notifyEnabled;
        this.id = UUID.randomUUID();
        this.clock = clock;
        this.isPreviousComplete = false;
        this.habitStats = new HabitStatistics();
        this.habitReminder = this.notifyEnabled ? getNewReminder() : null;
        this.achievements = new ArrayList<>();
        this.isArchived = false;
        updateDateTime();
        EventLog.getInstance().logEvent(
                new Event("Added new habit \"" + name + "\" with id " + id + " to habit manager"));
    }

    // REQUIRES: 0 < frequency < 16
    // EFFECTS: initializes habit for returning users
    public Habit(String n, String d, Period p, int f, UUID id, boolean ne, int ns, LocalDateTime cpe,
                 LocalDateTime npe, boolean ipc, Clock c, HabitStatistics hs, HabitReminder hr, boolean isArchived) {
        this.name = n;
        this.description = d;
        this.period = p;
        this.frequency = f;
        this.notifyEnabled = ne;
        this.id = id;
        this.numSuccess = ns;
        this.currentPeriodEnd = cpe;
        this.nextPeriodEnd = npe;
        this.clock = c;
        this.isPreviousComplete = ipc;
        this.habitStats = hs;
        this.habitReminder = hr;
        this.achievements = getAchieved(habitStats, period);
        this.isArchived = isArchived;
    }

    public void setHabitReminder(HabitReminder habitReminder) {
        this.habitReminder = habitReminder;
    }

    // MODIFIES: this
    // EFFECTS: sets this.name to given name
    public void setName(String name) {
        this.name = name;
        EventLog.getInstance().logEvent(new Event("Name of habit with id " + id + " changed to \"" + name + "\""));
    }

    // MODIFIES: this
    // EFFECTS: sets this.description to given description
    public void setDescription(String description) {
        this.description = description;
        EventLog.getInstance().logEvent(
                new Event("Description of habit with id " + id + " changed to \"" + description + "\""));
    }

    // MODIFIES: this
    // EFFECTS: set this.clock, and habitReminder.clock, solely for testing purposes
    public void setClock(Clock clock) {
        this.clock = clock;
        if (isNotifyEnabled()) {
            habitReminder.setClock(clock);
        }
    }

    // MODIFIES: this
    // EFFECTS: set this.numSuccess, solely for testing purposes
    public void setNumSuccess(int numSuccess) {
        this.numSuccess = numSuccess;
    }

    // MODIFIES: this
    // EFFECTS: toggles this.notifyEnabled
    //          if this.notifyEnabled is true, then habitReminder is reinitialized to a new reminder
    //          if this.notifyEnabled is false, then all reminders are cancelled and habitReminder is set to null
    public void toggleNotifyEnabled() {
        this.notifyEnabled = !this.notifyEnabled;
        if (this.notifyEnabled) {
            habitReminder = getNewReminder();
        } else {
            habitReminder.cancelReminders();
            habitReminder = null;
        }
        EventLog.getInstance().logEvent(
                new Event("Notifications of habit \"" + name
                        + "\" with id " + id + (this.notifyEnabled ? " enabled" : " disabled")));
    }

    // REQUIRES: 0 < frequency < 16
    // MODIFIES: this
    // EFFECTS: set this.frequency,
    //          if frequency != this.frequency, reset progress,
    //          update achievements
    //          if isNotifyEnabled, then reminders are updated
    //          returns whether frequency was changed
    public boolean setFrequency(int frequency) {
        if (this.frequency == frequency) {
            return false;
        }
        this.frequency = frequency;
        if (isNotifyEnabled()) {
            habitReminder.updateReminders();
        }
        resetProgress();
        achievements = getAchieved(habitStats, period);
        EventLog.getInstance().logEvent(
                new Event("Frequency of habit \"" + name + "\" with id " + id + " changed to " + frequency));
        return true;
    }

    // MODIFIES: this
    // EFFECTS: set this.period,
    //          if this.period != period, resets progress, updates currentPeriodEnd and nextPeriodEnd, updates
    //          achievements
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
        achievements = getAchieved(habitStats, period);
        EventLog.getInstance().logEvent(
                new Event("Period of habit \"" + name + "\" with id " + id + " changed to " + period));
        return true;
    }

    // MODIFIES: this
    // EFFECTS: toggles isArchived, if archived, cancels reminders, if unarchived, updates habit
    public void toggleIsArchived() {
        this.isArchived = !this.isArchived;
        if (this.isArchived && isNotifyEnabled()) {
            habitReminder.cancelReminders();
        } else {
            updateHabit();
        }
        EventLog.getInstance().logEvent(
                new Event("Habit \"" + name + "\" with id " + id + (isArchived ? " archived" : " unarchived")));
    }

    // REQUIRES: no reminders scheduled yet for this period, notifyEnabled is true
    // EFFECTS: returns new habit reminder with default notifications based on period
    public HabitReminder getNewReminder() {
        switch (period) {
            case DAILY:
                return new DailyReminder(clock, this);
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

    public List<Achievement> getAchievements() {
        return this.achievements;
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

    // REQUIRES: LocalDateTime.now(clock) is between currentPeriodEnd and nextPeriodEnd
    // EFFECTS: returns whether previous period was completed successfully
    public boolean isPreviousComplete() {
        return this.isPreviousComplete;
    }

    public boolean isArchived() {
        return this.isArchived;
    }

    // MODIFIES: this
    // EFFECTS: if numSuccess < frequency, increments numSuccess,
    //          updates habit statistics, reminders, and achievements, returns whether habit was incremented
    public boolean finishHabit() {
        if (numSuccess < frequency) {
            numSuccess++;
            habitStats.incrementTotalNumSuccess();
            EventLog.getInstance().logEvent(new Event("Habit \"" + name + "\" with id " + id + " completed"));
            checkPeriodComplete();
            achievements = getAchieved(habitStats, period);
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: if numSuccess > 0, decrements numSuccess and habitStats.totalNumSuccess,
    //          if isPeriodComplete(), then decrements habitStats.numPeriodSuccess and habitStats.streak,
    //          if notifyEnabled, then updates reminders,
    //          updates achievements, returns whether habit was decremented
    public boolean undoFinishHabit() {
        if (numSuccess > 0) {
            if (isPeriodComplete()) {
                habitStats.decrementNumPeriodSuccess();
                habitStats.decrementStreak();
                isPreviousComplete = false;
            }
            numSuccess--;
            habitStats.decrementTotalNumSuccess();
            if (isNotifyEnabled()) {
                habitReminder.updateReminders();
            }
            achievements = getAchieved(habitStats, period);
            EventLog.getInstance().logEvent(new Event("Habit \"" + name + "\" with id " + id + " uncompleted"));
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: if isPeriodComplete(), then increments both habitStats.numPeriodSuccess
    //          a habitStats.streak, sets isPreviousComplete to true, and cancels reminders if isNotifyEnabled()
    public void checkPeriodComplete() {
        if (isPeriodComplete()) {
            isPreviousComplete = true;
            habitStats.incrementNumPeriodSuccess();
            habitStats.incrementStreak();
            if (isNotifyEnabled()) {
                habitReminder.cancelReminders();
            }
            EventLog.getInstance().logEvent(
                    new Event("Habit \"" + name + "\" with id " + id + " completed for the period"));
        }
    }

    // REQUIRES: LocalDateTime.now(clock) is after currentPeriodEnd
    // MODIFIES: this
    // EFFECTS: updates currentPeriodEnd and nextPeriodEnd, resets numSuccess to 0, increments numPeriod
    public void nextHabitPeriod() {
        updateDateTime();
        numSuccess = 0;
        habitStats.incrementNumPeriod();
    }

    // MODIFIES: this
    // EFFECTS: resets numSuccess to 0, isPreviousComplete to false, and resets habit statistics
    public void resetProgress() {
        numSuccess = 0;
        isPreviousComplete = false;
        habitStats.resetStats();
    }

    // MODIFIES: this
    // EFFECTS: updates notifications, currentPeriodEnd, nextPeriodEnd, and habit statistics based on current date time
    //          a day is defined to start at 00:00
    //          if isNotifyEnabled(), then update reminders
    //          if now is not after currentPeriodEnd, do nothing,
    //          if now is between currentPeriodEnd and nextPeriodEnd, but if !isPreviousComplete(),
    //          switch to next period, then reset streak,
    //          if now is between currentPeriodEnd and nextPeriodEnd and isPreviousComplete(),
    //          switch to next period and reset isPreviousComplete to false,
    //          if now is after nextPeriodEnd, switch to next period, reset streak, and reset isPreviousComplete
    //          returns whether this was modified
    //          archived habits do not get updated
    public boolean updateHabit() {
        if (isArchived) {
            return false;
        }
        boolean changeMade = false;
        LocalDateTime now = LocalDateTime.now(clock);
        if (!now.isBefore(currentPeriodEnd.plusMinutes(1)) && now.isBefore(nextPeriodEnd.plusMinutes(1))) {
            nextHabitPeriod();
            if (!isPreviousComplete()) {
                habitStats.resetStreak();
            }
            isPreviousComplete = false;
            changeMade = true;
        } else if (!now.isBefore(nextPeriodEnd.plusMinutes(1))) {
            nextHabitPeriod();
            habitStats.resetStreak();
            isPreviousComplete = false;
            changeMade = true;
        }
        if (isNotifyEnabled()) {
            habitReminder.updateReminders();
        }
        achievements = getAchieved(habitStats, period);
        return changeMade;
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

    // EFFECTS: returns habit as a JSONObject
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);
        json.put("period", period.toString());
        json.put("frequency", frequency);
        json.put("id", id.toString());
        json.put("notifyEnabled", notifyEnabled);
        json.put("numSuccess", numSuccess);
        json.put("currentPeriodEnd", currentPeriodEnd.toString());
        json.put("nextPeriodEnd", nextPeriodEnd.toString());
        json.put("isPreviousComplete", isPreviousComplete);
        json.put("habitStats", habitStats.toJson());
        if (habitReminder != null) {
            json.put("habitReminder", habitReminder.toJson());
        }
        json.put("isArchived", isArchived);
        return json;
    }
}