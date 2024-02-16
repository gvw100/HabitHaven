package model;

import java.util.ArrayList;
import java.util.List;

// Represents list of the user's habits
public class HabitManager {
    private final List<Habit> habits;

    // EFFECTS: instantiates list of habits
    public HabitManager() {
        habits = new ArrayList<>();
    }

    public List<Habit> getHabits() {
        return this.habits;
    }

    // EFFECTS: returns size of habits
    public int getSize() {
        return this.habits.size();
    }

    // MODIFIES: this
    // EFFECTS: habit added to list of habits
    public void addHabit(Habit habit) {
        habits.add(habit);
    }

    // REQUIRES: habit is in this.habits
    // MODIFIES: this
    // EFFECTS: habit deleted from list of habits
    public void deleteHabit(Habit habit) {
        habits.remove(habit);
    }
}