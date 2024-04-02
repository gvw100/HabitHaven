package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import javafx.util.Pair;
import model.*;
import model.reminder.DailyReminder;
import model.reminder.HabitReminder;
import model.reminder.MonthlyReminder;
import model.reminder.WeeklyReminder;
import org.json.*;
import ui.reminder.ReminderScheduler;

// Represents a reader that reads habits from JSON data stored in file
// Citation: Code inspired by JsonSerializationDemo https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonReader {
    private String source;
    private Clock clock;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
        this.clock = Clock.systemDefaultZone();
    }

    // EFFECTS: reads habit manager from file and returns it;
    //          throws IOException if an error occurs reading data from file
    public HabitManager read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        parseHabitManager(jsonObject);
        return parseHabitManager(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }
        return contentBuilder.toString();
    }

    // EFFECTS: parses HabitManager from JSON object and returns it
    private HabitManager parseHabitManager(JSONObject jsonObject) {
        HabitManager hm = new HabitManager(
                jsonObject.getString("username"),
                jsonObject.getBoolean("isAutoSave"),
                jsonObject.getBoolean("achievementToastsEnabled"),
                jsonObject.getBoolean("hideOnClose"));
        addHabits(hm, jsonObject);
        return hm;
    }

    // MODIFIES: hm
    // EFFECTS: parses habits from JSON object and adds them to habit manager
    private void addHabits(HabitManager hm, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("habits");
        for (Object json : jsonArray) {
            JSONObject nextHabit = (JSONObject) json;
            addHabit(hm, nextHabit);
        }
    }

    // MODIFIES: hm
    // EFFECTS: parses habit from JSON object and adds it to habit manager
    private void addHabit(HabitManager hm, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        String description = jsonObject.getString("description");
        Period period = Period.valueOf(jsonObject.getString("period"));
        int frequency = jsonObject.getInt("frequency");
        UUID id = UUID.fromString(jsonObject.getString("id"));
        boolean notifyEnabled = jsonObject.getBoolean("notifyEnabled");
        int numSuccess = jsonObject.getInt("numSuccess");
        LocalDateTime currentPeriodEnd = LocalDateTime.parse(jsonObject.getString("currentPeriodEnd"));
        LocalDateTime nextPeriodEnd = LocalDateTime.parse(jsonObject.getString("nextPeriodEnd"));
        boolean isPreviousComplete = jsonObject.getBoolean("isPreviousComplete");
        HabitStatistics stats = parseHabitStatistics(jsonObject.getJSONObject("habitStats"));
        boolean isArchived = jsonObject.getBoolean("isArchived");
        Habit habit = new Habit(name, description, period, frequency, id, notifyEnabled, numSuccess,
                currentPeriodEnd, nextPeriodEnd, isPreviousComplete, clock, stats, null, isArchived);
        HabitReminder reminder;
        reminder = notifyEnabled ? parseHabitReminder(jsonObject.getJSONObject("habitReminder"), period, habit) : null;
        habit.setHabitReminder(reminder);
        hm.addHabit(habit);
    }

    // EFFECTS: parses habit statistics from JSON object and returns it
    private HabitStatistics parseHabitStatistics(JSONObject jsonObject) {
        int streak = jsonObject.getInt("streak");
        int bestStreak = jsonObject.getInt("bestStreak");
        int totalNumSuccess = jsonObject.getInt("totalNumSuccess");
        int numPeriodSuccess = jsonObject.getInt("numPeriodSuccess");
        int numPeriod = jsonObject.getInt("numPeriod");
        return new HabitStatistics(streak, bestStreak, totalNumSuccess, numPeriodSuccess, numPeriod);
    }

    // EFFECTS: parses habit reminder from JSON object and returns it
    private HabitReminder parseHabitReminder(JSONObject jsonObject, Period period, Habit habit) {
        switch (period) {
            case DAILY:
                return parseDailyReminder(jsonObject, habit);
            case WEEKLY:
                return parseWeeklyReminder(jsonObject, habit);
            default:
                return parseMonthlyReminder(jsonObject, habit);
        }
    }

    // EFFECTS: parses daily reminder from JSON object and returns it
    private HabitReminder parseDailyReminder(JSONObject jsonObject, Habit habit) {
        Set<LocalDateTime> reminders = parseReminderDateTimes(jsonObject.getJSONArray("reminders"));
        boolean isDefault = jsonObject.getBoolean("isDefault");
        ReminderScheduler reminderScheduler = new ReminderScheduler();
        return new DailyReminder(reminders, clock, isDefault, habit, reminderScheduler);
    }

    // EFFECTS: parses weekly reminder from JSON object and returns it
    private HabitReminder parseWeeklyReminder(JSONObject jsonObject, Habit habit) {
        Set<LocalDateTime> reminders = parseReminderDateTimes(jsonObject.getJSONArray("reminders"));
        boolean isDefault = jsonObject.getBoolean("isDefault");
        ReminderScheduler reminderScheduler = new ReminderScheduler();
        return new WeeklyReminder(reminders, clock, isDefault, habit, reminderScheduler);
    }

    // EFFECTS: parses monthly reminder from JSON object and returns it
    private HabitReminder parseMonthlyReminder(JSONObject jsonObject, Habit habit) {
        boolean isDefault = jsonObject.getBoolean("isDefault");
        Set<Pair<Integer, LocalTime>> customReminders =
                isDefault ? null : parseCustomMonthlyPairs(jsonObject.getJSONArray("customReminders"));
        Set<LocalDateTime> reminders = parseReminderDateTimes(jsonObject.getJSONArray("reminders"));
        ReminderScheduler reminderScheduler = new ReminderScheduler();
        return new MonthlyReminder(customReminders, reminders, clock, isDefault, habit, reminderScheduler);
    }

    // EFFECTS: parses reminder date times from JSON array and returns it
    private Set<LocalDateTime> parseReminderDateTimes(JSONArray jsonArray) {
        Set<LocalDateTime> reminders = new HashSet<>();
        for (Object json : jsonArray) {
            JSONObject nextReminder = (JSONObject) json;
            reminders.add(LocalDateTime.parse(nextReminder.getString("dateTime")));
        }
        return reminders;
    }

    // EFFECTS: parses custom monthly pairs from JSON array and returns it
    private Set<Pair<Integer, LocalTime>> parseCustomMonthlyPairs(JSONArray jsonArray) {
        Set<Pair<Integer, LocalTime>> customReminders = new HashSet<>();
        for (Object json : jsonArray) {
            JSONObject nextPair = (JSONObject) json;
            customReminders.add(new Pair<>(nextPair.getInt("day"), LocalTime.parse(nextPair.getString("time"))));
        }
        return customReminders;
    }
}