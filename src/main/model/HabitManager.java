package model;

import org.json.JSONArray;
import org.json.JSONObject;
import ui.HabitApp;

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

    // EFFECTS: returns size of habits, solely for testing purposes
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

    // EFFECTS: returns habit manager as a JSON object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("username", HabitApp.getUsername());
        json.put("habits", habitsToJson());
        return json;
    }

    // EFFECTS: returns habits in habit manager as a JSON array
    private JSONArray habitsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Habit h : habits) {
            jsonArray.put(h.toJson());
        }
        return jsonArray;
    }
}