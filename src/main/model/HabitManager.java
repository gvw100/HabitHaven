package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Represents list of the user's habits
public class HabitManager {

    private final List<Habit> habits;
    private static String username;
    private static boolean isAutoSave;

    // EFFECTS: constructs a habit manager with an empty list of habits
    public HabitManager() {
        habits = new ArrayList<>();
        isAutoSave = false;
    }

    public List<Habit> getHabits() {
        return this.habits;
    }

    // EFFECTS: returns size of habits, solely for testing purposes
    public int getSize() {
        return this.habits.size();
    }

    public static String getUsername() {
        return HabitManager.username;
    }

    public static boolean isIsAutoSave() {
        return HabitManager.isAutoSave;
    }

    public static void setUsername(String username) {
        HabitManager.username = username;
    }

    public static void setIsAutoSave(boolean isAutoSave) {
        HabitManager.isAutoSave = isAutoSave;
    }

    // MODIFIES: this
    // EFFECTS: habit added to list of habits
    public void addHabit(Habit habit) {
        habits.add(habit);
    }

    // REQUIRES: habit is in this.habits
    // MODIFIES: this
    // EFFECTS: habit deleted from list of habits and reminders are cancelled if habit.isNotifyEnabled()
    public void deleteHabit(Habit habit) {
        habits.remove(habit);
        if (habit.isNotifyEnabled()) {
            habit.getHabitReminder().cancelReminders();
        }
    }

    // MODIFIES: this
    // EFFECTS: disables reminders for all habits
    public void turnOffReminders() {
        for (Habit h : habits) {
            h.setNotifyEnabled(false);
        }
    }

    // EFFECTS: returns habit manager as a JSONObject
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("username", HabitManager.getUsername());
        json.put("isAutoSave", HabitManager.isAutoSave);
        json.put("habits", habitsToJson());
        return json;
    }

    // EFFECTS: returns habits habits as a JSONArray
    private JSONArray habitsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Habit h : habits) {
            jsonArray.put(h.toJson());
        }
        return jsonArray;
    }
}