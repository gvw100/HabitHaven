package ui;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ReminderScheduler {

    private ScheduledExecutorService scheduler;
    private Clock clock;

    public ReminderScheduler(Clock clock) {
        this.clock = clock;
        this.scheduler = Executors.newScheduledThreadPool(5);
    }

    // EFFECTS: schedules notifications to be sent, ignores reminders that have already passed
    public void scheduleReminders(Set<LocalDateTime> reminders) {
        for (LocalDateTime reminder : reminders) {
            scheduleReminder(reminder);
        }
    }

    private void scheduleReminder(LocalDateTime reminder) {
        long delay = calculateDelay(reminder);
    }

    private long calculateDelay(LocalDateTime reminder) {
        LocalDateTime now = LocalDateTime.now(clock);
        long delay = java.time.Duration.between(now, reminder).getSeconds();
        return delay;
    }
}
