package ui;

import model.Habit;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

// A scheduler for notifications of a particular habit
public class ReminderScheduler {

    private Scheduler scheduler;

    // EFFECTS: constructs a new reminder scheduler
    public ReminderScheduler() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    // EFFECTS: schedules notifications to be sent at the given times
    public void scheduleReminders(Set<LocalDateTime> reminders, Habit habit) {
        for (LocalDateTime reminder : reminders) {
            scheduleReminder(reminder, habit);
        }
    }

    // MODIFIES: this
    // EFFECTS: schedules a notification to be sent at the given time
    private void scheduleReminder(LocalDateTime reminder, Habit habit) {
        JobDataMap data = new JobDataMap();
        data.put("habit", habit);
        Date date = Date.from(reminder.atZone(ZoneId.systemDefault()).toInstant());
        JobDetail job = newJob(SendReminder.class)
                .usingJobData(data)
                .build();
        SimpleTrigger trigger = (SimpleTrigger) newTrigger()
                .startAt(date)
                .forJob(job)
                .build();
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: cancels all scheduled reminders
    public void cancelReminders() {
        try {
            scheduler.clear();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}