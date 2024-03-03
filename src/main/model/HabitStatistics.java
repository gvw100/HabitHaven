package model;

import org.json.JSONObject;

// Statistics for a habit, with the number of consecutive period completions,
// the all-time best streak, the number of times the habit was
// marked as complete, the total number of successful periods,
// and the number of periods tracked
public class HabitStatistics {
    private int streak;
    private int bestStreak;
    private int totalNumSuccess;
    private int numPeriodSuccess;
    private int numPeriod;

    // EFFECTS: constructs habit statistics with all fields set to 0
    public HabitStatistics() {
        streak = 0;
        bestStreak = 0;
        totalNumSuccess = 0;
        numPeriodSuccess = 0;
        numPeriod = 0;
    }

    // EFFECTS: constructs habit statistics for returning users
    public HabitStatistics(int streak, int bestStreak, int totalNumSuccess, int numPeriodSuccess, int numPeriod) {
        this.streak = streak;
        this.bestStreak = bestStreak;
        this.totalNumSuccess = totalNumSuccess;
        this.numPeriodSuccess = numPeriodSuccess;
        this.numPeriod = numPeriod;
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
    // EFFECTS: increments streak, if streak > bestStreak, then increments bestStreak too
    public void incrementStreak() {
        streak++;
        if (streak > bestStreak) {
            bestStreak++;
        }
    }

    // MODIFIES: this
    // EFFECTS: increments totalNumSuccess
    public void incrementTotalNumSuccess() {
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

    // REQUIRES: streak > 0
    // MODIFIES: this
    // EFFECTS: decrements streak, if streak == bestStreak, then decrement bestStreak too
    public void decrementStreak() {
        if (streak == bestStreak) {
            bestStreak--;
        }
        streak--;
    }

    // REQUIRES: totalNumSuccess > 0
    // MODIFIES: this
    // EFFECTS: decrements totalNumSuccess
    public void decrementTotalNumSuccess() {
        totalNumSuccess--;
    }

    // REQUIRES: numPeriodSuccess > 0
    // MODIFIES: this
    // EFFECTS: decrements numPeriodSuccess
    public void decrementNumPeriodSuccess() {
        numPeriodSuccess--;
    }

    // EFFECTS: returns habitStatistics as a JSONObject
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("streak", streak);
        json.put("bestStreak", bestStreak);
        json.put("totalNumSuccess", totalNumSuccess);
        json.put("numPeriodSuccess", numPeriodSuccess);
        json.put("numPeriod", numPeriod);
        return json;
    }
}