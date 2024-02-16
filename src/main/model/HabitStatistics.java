package model;

// Statistics for a habit, streak is number of consecutive period completions,
// bestStreak is all-time best streak, totalNumSuccess gives the number of
// times the habit was marked as complete, numPeriodSuccess
// gives the number of times habit complete within period
public abstract class HabitStatistics {
    private int streak;
    private int bestStreak;
    private int totalNumSuccess;
    private int numPeriodSuccess;
    private int numPeriod;

    // EFFECTS: initializes all fields to 0
    HabitStatistics() {
        streak = 0;
        bestStreak = 0;
        totalNumSuccess = 0;
        numPeriodSuccess = 0;
        numPeriod = 0;
    }

    public int getStreak() {
        return this.streak;
    }

    public int getBestStreak() {
        return this.bestStreak;
    }

    public int getTotalNumSuccess() {
        return this.totalNumSuccess;
    }

    public int getNumPeriodSuccess() {
        return this.numPeriodSuccess;
    }

    public int getNumPeriod() {
        return this.numPeriod;
    }

    // EFFECTS: if numPeriod != 0 and isPeriodComplete, returns (numPeriodSuccess / (numPeriod + 1)) * 100
    //          if numPeriod != 0 and !isPeriodComplete, returns (numPeriodSuccess / numPeriod) * 100
    //          if numPeriod == 0, then returns numPeriodSuccess * 100
    public int getSuccessRate(boolean isPeriodComplete) {
        if (numPeriod != 0) {
            int trueNumPeriod = isPeriodComplete ? numPeriod + 1 : numPeriod;
            double rate = (double) numPeriodSuccess / (double) trueNumPeriod;
            return (int) Math.round(rate * 100);
        } else {
            return numPeriodSuccess * 100;
        }
    }

    // MODIFIES: this
    // EFFECTS: resets this.streak to 0
    public void resetStreak() {
        streak = 0;
    }

    // MODIFIES: this
    // EFFECTS: resets all fields to 0
    public void resetStats() {
        streak = bestStreak = totalNumSuccess = numPeriodSuccess = numPeriod = 0;
    }

    // MODIFIES: this
    // EFFECTS: increments streak, if streak > bestStreak, then assign bestStreak = streak
    public void incrementStreak() {
        streak++;
        if (streak > bestStreak) {
            bestStreak = streak;
        }
    }

    // MODIFIES: this
    // EFFECTS: increments totalNumSuccess
    public void incrementNumSuccess() {
        totalNumSuccess++;
    }

    // MODIFIES: this
    // EFFECTS: increments numPeriodSuccess
    public void incrementNumPeriodSuccess() {
        numPeriodSuccess++;
    }

    // MODIFIES: this
    // EFFECTS: increments numPeriod
    public void incrementNumPeriod() {
        numPeriod++;
    }
}