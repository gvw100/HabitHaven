package model;

// Represents a habit with a name,description, frequency, period, and number of successes
public class Habit {
    private String name;
    private String description;
    private int frequency;
    private Period period;
    private int numSuccess;
    private HabitStatistics statistics;

    // EFFECTS: initializes habit
    public Habit(String name, String description, Period period, int frequency) {
        this.numSuccess = 0;
        this.name = name;
        this.frequency = 1;
        setFrequency(frequency);
        this.period = period;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // MODIFIES: this
    // EFFECTS: set this.frequency if frequency is between 1 and 15, resets numSuccess
    public void setFrequency(int frequency) {
        if (frequency > 0 && frequency < 16) {
            this.frequency = frequency;
            resetProgress();
        }
    }

    // MODIFIES: this
    // EFFECTS: set this.period, resets numSuccess
    public void setPeriod(Period period) {
        this.period = period;
        resetProgress();
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

    // MODIFIES: this
    // EFFECTS: increments numSuccess given that it has not yet surpassed frequency
    public void finishHabit() {
        if (numSuccess < frequency) {
            numSuccess++;
        }
    }

    // MODIFIES: this
    // EFFECTS: resets numSuccess and streak to 0
    public void resetProgress() {
        numSuccess = 0;
    }
}