package model;

import model.log.Event;
import model.log.EventLog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Represents list of the user's habits
public class HabitManager {

    private final List<Habit> habits;
    private static String username;
    private static boolean isAutoSave;
    private static boolean achievementToastsEnabled;
    private static boolean hideOnClose;

    // EFFECTS: constructs a habit manager with an empty list of habits,
    //          by default auto save is off and achievement toasts are enabled
    public HabitManager() {
        habits = new ArrayList<>();
        isAutoSave = false;
        achievementToastsEnabled = true;
        hideOnClose = true;
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

    public static boolean isAutoSave() {
        return HabitManager.isAutoSave;
    }

    public static boolean isAchievementToastsEnabled() {
        return HabitManager.achievementToastsEnabled;
    }

    public static boolean isHideOnClose() {
        return HabitManager.hideOnClose;
    }

    public static void setUsername(String username) {
        HabitManager.username = username;
        EventLog.getInstance().logEvent(new Event("Username changed to " + username));
    }

    public static void setIsAutoSave(boolean isAutoSave) {
        HabitManager.isAutoSave = isAutoSave;
    }

    // MODIFIES: this
    // EFFECTS: toggles HabitManager.isAutoSave
    public static void toggleAutoSave() {
        HabitManager.isAutoSave = !HabitManager.isAutoSave;
        EventLog.getInstance().logEvent(new Event("Auto save turned " + (HabitManager.isAutoSave ? "on" : "off")));
    }

    public static void setAchievementToastsEnabled(boolean achievementToastsEnabled) {
        HabitManager.achievementToastsEnabled = achievementToastsEnabled;
    }

    // MODIFIES: this
    // EFFECTS: toggles HabitManager.achievementToastsEnabled
    public static void toggleAchievementToastsEnabled() {
        HabitManager.achievementToastsEnabled = !HabitManager.achievementToastsEnabled;
        EventLog.getInstance().logEvent(
                new Event("Achievement toasts turned " + (HabitManager.achievementToastsEnabled ? "on" : "off")));
    }

    public static void setHideOnClose(boolean hideOnClose) {
        HabitManager.hideOnClose = hideOnClose;
    }

    // MODIFIES: this
    // EFFECTS: toggles HabitManager.hideOnClose
    public static void toggleHideOnClose() {
        HabitManager.hideOnClose = !HabitManager.hideOnClose;
        EventLog.getInstance().logEvent(
                new Event("Application set to " + (HabitManager.hideOnClose ? "hide on close" : "exit on close")));
    }

    // MODIFIES: this
    // EFFECTS: habit added to list of habits
    public void addHabit(Habit habit) {
        habits.add(habit);
        EventLog.getInstance().logEvent(new Event("Added habit " + habit.getName() + " with id " + habit.getId()));
    }

    // REQUIRES: habit is in this.habits
    // MODIFIES: this, habit
    // EFFECTS: habit deleted from list of habits and reminders are cancelled if habit.isNotifyEnabled()
    public void deleteHabit(Habit habit) {
        habits.remove(habit);
        if (habit.isNotifyEnabled()) {
            habit.getHabitReminder().cancelReminders();
        }
        EventLog.getInstance().logEvent(new Event("Removed habit " + habit.getName() + " with id" + habit.getId()));
    }

    // MODIFIES: this
    // EFFECTS: disables reminders for all habits
    public void turnOffReminders() {
        for (Habit h : habits) {
            if (h.isNotifyEnabled()) {
                h.toggleNotifyEnabled();
            }
        }
        EventLog.getInstance().logEvent(new Event("All notifications turned off"));
    }

    // EFFECTS: returns habit manager as a JSONObject
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("isAutoSave", HabitManager.isAutoSave);
        json.put("achievementToastsEnabled", achievementToastsEnabled);
        json.put("hideOnClose", hideOnClose);
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