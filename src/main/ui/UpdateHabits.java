package ui;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class UpdateHabits implements Job {

    private Runnable updateHabits;

    // EFFECTS: constructs a UpdateHabits
    public UpdateHabits() {
    }

    public void setUpdateHabits(Runnable updateHabits) {
        this.updateHabits = updateHabits;
    }

    // EFFECTS: updates all habits in habit manager
    public void execute(JobExecutionContext context) {
        updateHabits.run();
    }
}