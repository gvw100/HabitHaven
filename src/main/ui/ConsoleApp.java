package ui;

import javafx.util.Pair;
import model.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

// Habit tracker console application
// Citation: Some of the code here is inspired from the TellerApp.java class in the CPSC 210 course
//           https://github.students.cs.ubc.ca/CPSC210/TellerApp
public class ConsoleApp {
    private static final String HABIT_MANAGER_STORE = "./data/habitManager.json";
    private static final int MAX_FREQUENCY = 15;
    private static final int MONTH_MAX_DAYS = 31;
    private Scanner input;
    private HabitManager habitManager;
    private final Clock clock;
    private boolean isSaved;

    // EFFECTS: starts the application, clock initialized to system default zone and isSaved initialized to true
    public ConsoleApp() {
        clock = Clock.systemDefaultZone();
        isSaved = true;
        startApp();
    }

    // MODIFIES: this
    // EFFECTS: sets up scanner and loads habit data from file or creates a new user
    private void startApp() {
        setupScanner();
        loadOrCreateUser();
    }

    // EFFECTS: schedules habit updates to occur daily at midnight
    private void scheduleHabitUpdates() {
        Runnable updateAllHabits = this::updateAllHabits;
        JobDataMap data = new JobDataMap();
        data.put("updateHabits", updateAllHabits);
        JobDetail job = newJob(UpdateHabits.class)
                .usingJobData(data)
                .build();
        Trigger trigger = newTrigger()
                .withSchedule(dailyAtHourAndMinute(0, 0)
                        .withMisfireHandlingInstructionFireAndProceed())
                .forJob(job)
                .build();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates all habits in habit manager based on current time
    private void updateAllHabits() {
        System.out.println("\nUpdating habits...");
        for (Habit habit : habitManager.getHabits()) {
            habit.updateHabit();
        }
    }

    // MODIFIES: this
    // EFFECTS: loads from habit data from file or creates a new user
    private void loadOrCreateUser() {
        String command;
        do {
            System.out.println("Welcome to HabitHaven!");
            System.out.println("\tn -> New user");
            System.out.println("\tl -> Load from file");
            command = input.next();
        } while (!(command.equals("n") || command.equals("l")));
        if (command.equals("n")) {
            newUser();
        } else {
            loadHabitManager();
        }
    }

    // MODIFIES: this
    // EFFECTS: sets username to input provided by user, schedules habit updates, and displays menu
    private void newUser() {
        habitManager = new HabitManager();
        System.out.println("Enter your name: ");
        HabitManager.setUsername(input.next());
        System.out.println("Welcome, " + HabitManager.getUsername() + "!");
        isSaved = false;
        scheduleHabitUpdates();
        menu();
    }

    // MODIFIES: this
    // EFFECTS: loads user data from file, updates all habits, schedules habit updates, and displays menu
    private void loadHabitManager() {
        JsonReader jsonReader = new JsonReader(HABIT_MANAGER_STORE);
        try {
            habitManager = jsonReader.read();
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + HABIT_MANAGER_STORE);
            System.exit(-1);
        }
        System.out.println("\nWelcome back, " + HabitManager.getUsername() + "!");
        updateAllHabits();
        scheduleHabitUpdates();
        menu();
    }

    // MODIFIES: this
    // EFFECTS: displays menu and processes menu input
    private void menu() {
        String command;
        do {
            displayMenu();
            command = input.next();
            if (command.equals("q")) {
                if (!isSaved) {
                    confirmSave();
                } else {
                    break;
                }
            } else {
                processMenuInput(command);
            }
        } while (true);
        closeApp();
    }

    // MODIFIES: this
    // EFFECTS: prompts user to save changes, and closes the application
    private void confirmSave() {
        String command;
        do {
            System.out.println("Save changes? y/n");
            command = input.next();
        } while (!(command.equals("y") || command.equals("n")));
        if (command.equals("y")) {
            saveHabitManager();
            closeApp();
        } else {
            closeApp();
        }
    }

    // EFFECTS: closes the application
    private void closeApp() {
        System.out.println("\nGoodbye!");
        System.exit(0);
    }

    // MODIFIES: this
    // EFFECTS: initializes scanner
    private void setupScanner() {
        input = new Scanner(System.in);
        input.useDelimiter("\n");
    }

    // EFFECTS: displays menu with choices
    private void displayMenu() {
        System.out.println("\nWelcome to HabitHaven!\n");
        System.out.println("Select from:");
        System.out.println("\tc -> Create habit ");
        System.out.println("\tv -> View habit list");
        System.out.println("\tu -> Change username");
        System.out.println("\ts -> Save to file");
        System.out.println("\tq -> Quit");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    private void processMenuInput(String command) {
        switch (command) {
            case "c":
                createHabit();
                break;
            case "v":
                viewHabits();
                break;
            case "u":
                changeUsername();
                break;
            case "s":
                saveHabitManager();
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a habit and adds it to habitManager.habits
    private void createHabit() {
        String name = getHabitName();
        String description = getHabitDescription();
        Period period = getHabitPeriod();
        int frequency = getHabitFrequency();
        boolean notificationEnabled = getNotificationsEnabled();
        Habit habit = new Habit(name, description, period, frequency, notificationEnabled, clock);
        habitManager.addHabit(habit);
        isSaved = false;
    }

    // EFFECTS: returns habit name entered by user
    private String getHabitName() {
        System.out.println("Enter habit name: ");
        return input.next();
    }

    // EFFECTS: returns habit description entered by user
    private String getHabitDescription() {
        System.out.println("Enter habit description: ");
        return input.next();
    }

    // EFFECTS: returns habit period selected by user
    private Period getHabitPeriod() {
        Period period = null;
        boolean inputIsInvalid;
        do {
            inputIsInvalid = false;
            System.out.println("Select from the following habit periods: ");
            System.out.println("\td -> Daily");
            System.out.println("\tw -> Weekly");
            System.out.println("\tm -> Monthly");
            switch (input.next()) {
                case "d":
                    period = Period.DAILY;
                    break;
                case "w":
                    period = Period.WEEKLY;
                    break;
                case "m":
                    period = Period.MONTHLY;
                    break;
                default:
                    inputIsInvalid = true;
            }
        } while (inputIsInvalid);
        return period;
    }

    // EFFECTS: returns habit frequency entered by user restricted between 1 and MAX_FREQUENCY, inclusive
    private int getHabitFrequency() {
        int frequency;
        do {
            System.out.println("Enter habit frequency: ");
            if (input.hasNextInt()) {
                frequency = input.nextInt();
                if (frequency > 0 && frequency <= MAX_FREQUENCY) {
                    return frequency;
                } else {
                    System.out.println("Frequency must be between 1 and " + MAX_FREQUENCY);
                }
            } else {
                input.next();
            }
        } while (true);
    }

    // EFFECTS: returns whether the user wants to enable notifications
    private boolean getNotificationsEnabled() {
        String command;
        do {
            System.out.println("Enable notifications? y/n");
            command = input.next();
        } while (!((command.equals("y")) || (command.equals("n"))));
        return command.equals("y");
    }

    // MODIFIES: this
    // EFFECTS: displays all habits and processes input
    private void viewHabits() {
        int index;
        List<Habit> habits = habitManager.getHabits();
        do {
            System.out.println("\nHere are your habits! Select from the following: \n");
            for (int i = 0; i < habits.size(); i++) {
                int num = i + 1;
                System.out.println("\t" + num + " -> " + habits.get(i).getName());
            }
            System.out.println("\tm -> Back to menu");
            if (input.hasNextInt()) {
                index = input.nextInt();
                if (index >= 1 && index <= habits.size()) {
                    viewHabit(habits.get(index - 1));
                }
            } else if (input.next().equals("m")) {
                return;
            }
        } while (true);
    }

    // MODIFIES: this
    // EFFECTS: view habit and process habit input
    private void viewHabit(Habit habit) {
        boolean isBackInput;
        do {
            displayHabit(habit);
            isBackInput = processHabitInput(habit);
        } while (!isBackInput);
    }

    // MODIFIES: this
    // EFFECTS: brings user to the appropriate habit tool according to input, returns whether user will go back
    //          to habit list
    private boolean processHabitInput(Habit habit) {
        switch (input.next()) {
            case "e":
                editHabit(habit);
                break;
            case "d":
                if (deleteHabit(habit)) {
                    return true;
                } else {
                    break;
                }
            case "f":
                finishHabit(habit);
                break;
            case "s":
                showStatistics(habit);
                break;
            case "n":
                customizeNotifications(habit);
                break;
            case "h":
                return true;
        }
        return false;
    }

    // EFFECTS: displays habit information
    private void displayHabit(Habit habit) {
        System.out.println("\nName: " + habit.getName());
        System.out.println("Description: " + habit.getDescription());
        System.out.println("Period: " + habit.getPeriod());
        System.out.println("Frequency: " + habit.getFrequency());
        System.out.println("Number of times completed: " + habit.getNumSuccess());
        System.out.println("\n\te -> Edit habit");
        System.out.println("\td -> Delete habit");
        System.out.println("\tf -> Finish habit");
        System.out.println("\ts -> Show in-depth statistics");
        System.out.println("\tn -> Customize notifications");
        System.out.println("\th -> Back to habit list");
    }

    // MODIFIES: this
    // EFFECTS: displays edit options and processes edit input
    private void editHabit(Habit habit) {
        do {
            displayEditOptions();
            switch (input.next()) {
                case "n":
                    changeName(habit);
                    break;
                case "d":
                    changeDescription(habit);
                    break;
                case "p":
                    changePeriod(habit);
                    break;
                case "f":
                    changeFrequency(habit);
                    break;
                case "r":
                    resetProgress(habit);
                    break;
                case "b":
                    return;
            }
        } while (true);
    }

    // EFFECTS: displays options for habit editing
    private void displayEditOptions() {
        System.out.println("\nSelect one of the following: \n");
        System.out.println("\tn -> Change name");
        System.out.println("\td -> Change description");
        System.out.println("\tp -> Change period");
        System.out.println("\tf -> Change frequency");
        System.out.println("\tr -> Reset habit progress");
        System.out.println("\tb -> Back to habit");
    }

    // MODIFIES: this
    // EFFECTS: changes name to input provided by user
    private void changeName(Habit habit) {
        habit.setName(getHabitName());
        isSaved = false;
    }

    // MODIFIES: this
    // EFFECTS: changes description to input provided by user
    private void changeDescription(Habit habit) {
        habit.setDescription(getHabitDescription());
        isSaved = false;
    }

    // MODIFIES: this
    // EFFECTS: changes period to period selected by user
    private void changePeriod(Habit habit) {
        String command;
        do {
            System.out.println("\nWarning: Changing habit period will reset habit progress and statistics. "
                    + "Are you sure you want to continue? y/n");
            command = input.next();
        } while (!(command.equals("y") || command.equals("n")));
        if (command.equals("n")) {
            return;
        }
        if (!habit.setPeriod(getHabitPeriod())) {
            System.out.println("\nPeriod already set to " + habit.getPeriod());
        } else {
            isSaved = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: changes frequency to frequency selected by user between 1 and MAX_FREQUENCY
    private void changeFrequency(Habit habit) {
        String command;
        do {
            System.out.println("\nWarning: Changing habit frequency will reset habit progress and statistics. "
                    + "Are you sure you want to continue? y/n");
            command = input.next();
        } while (!(command.equals("y") || command.equals("n")));
        if (command.equals("n")) {
            return;
        }
        if (!habit.setFrequency(getHabitFrequency())) {
            System.out.println("\nFrequency already set to " + habit.getFrequency());
        } else {
            isSaved = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: resets habit progress and statistics
    private void resetProgress(Habit habit) {
        String command;
        do {
            System.out.println("Are you sure you want to reset habit progress? y/n");
            command = input.next();
        } while (!(command.equals("y") || command.equals("n")));
        if (command.equals("y")) {
            habit.resetProgress();
            isSaved = false;
            System.out.println("Habit progress reset successfully");
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes habit after confirming user action, returns whether habit was deleted
    private boolean deleteHabit(Habit habit) {
        String command;
        do {
            System.out.println("\nAre you sure you want to delete this habit? y/n");
            command = input.next();
        } while (!(command.equals("y") || command.equals("n")));
        if (command.equals("y")) {
            habitManager.deleteHabit(habit);
            System.out.println("\nHabit deleted successfully");
            isSaved = false;
            return true;
        } else {
            return false;
        }
    }

    // MODIFIES: this
    // EFFECTS: marks habit as complete, incrementing numSuccess, awarding user for successful completion of period
    private void finishHabit(Habit habit) {
        String periodString = getPeriodString(habit.getPeriod(), "today.", "this week.", "this month.");
        boolean isCompleted = habit.finishHabit();
        if (!isCompleted) {
            System.out.println("\nYou have already completed the habit for " + periodString);
        } else if (habit.isPeriodComplete()) {
            System.out.println("\nYou successfully completed " + habit.getName() + " " + habit.getFrequency()
                    + " times " + periodString);
            System.out.println("Great job!");
            isSaved = false;
        } else {
            String message = "\nYou completed " + habit.getName() + " " + habit.getNumSuccess()
                    + " times so far " + periodString + " Keep it up!";
            System.out.println(message);
            isSaved = false;
        }
    }

    // EFFECTS: displays in-depth habit statistics
    private void showStatistics(Habit habit) {
        int streak = habit.getHabitStats().getStreak();
        int bestStreak = habit.getHabitStats().getBestStreak();
        int totalNumSuccess = habit.getHabitStats().getTotalNumSuccess();
        int numPeriodSuccess = habit.getHabitStats().getNumPeriodSuccess();
        int numPeriod = habit.getHabitStats().getNumPeriod();
        int successRate = habit.getHabitStats().getSuccessRate(habit.isPeriodComplete());
        boolean inputIsInvalid;
        do {
            String periodString = getPeriodString(habit.getPeriod(), "days", "weeks", "months");
            System.out.println("Statistics for " + habit.getName() + ":");
            System.out.println("Current streak: " + streak + " " + periodString);
            System.out.println("Best streak: " + bestStreak + " " + periodString);
            System.out.println("Lifetime number of completions: " + totalNumSuccess);
            System.out.println("Number of successful " +  periodString + ": " + numPeriodSuccess);
            System.out.println("Number of " + periodString + " tracked: " + numPeriod);
            System.out.println("Success rate: " + successRate + "%");
            System.out.println("\n\t b -> Back to habit");
            inputIsInvalid = processStatInput();
        } while (inputIsInvalid);
    }

    // EFFECTS: processes input and returns whether the input is invalid
    private boolean processStatInput() {
        return !input.next().equals("b");
    }

    // MODIFIES: this
    // EFFECTS: displays notification options and processes input
    private void customizeNotifications(Habit habit) {
        do {
            displayNotificationOptions();
            switch (input.next()) {
                case "e":
                    enableNotifications(habit);
                    break;
                case "d":
                    disableNotifications(habit);
                    break;
                case "c":
                    customizeNotificationDateTimes(habit);
                    break;
                case "b":
                    return;
            }
        } while (true);
    }

    // EFFECTS: displays notification options
    private void displayNotificationOptions() {
        System.out.println("\nSelect one of the following: \n");
        System.out.println("\te -> Enable notifications");
        System.out.println("\td -> Disable notifications");
        System.out.println("\tc -> Customize notification times");
        System.out.println("\tb -> Back to habit");
    }

    // MODIFIES: this
    // EFFECTS: enables notifications for given habit
    private void enableNotifications(Habit habit) {
        boolean wasChanged = habit.setNotifyEnabled(true);
        if (wasChanged) {
            System.out.println("\nNotifications enabled");
            isSaved = false;
        } else {
            System.out.println("\nNotifications already enabled");
        }
    }

    // MODIFIES: this
    // EFFECTS: disables notifications for given habit
    private void disableNotifications(Habit habit) {
        boolean wasChanged = habit.setNotifyEnabled(false);
        if (wasChanged) {
            System.out.println("\nNotifications disabled");
            isSaved = false;
        } else {
            System.out.println("\nNotifications already disabled");
        }
    }

    // MODIFIES: this
    // EFFECTS: customizes notification times for given habit, if already customized, user can revert to default,
    //          keep current notifications, or override current notifications
    private void customizeNotificationDateTimes(Habit habit) {
        if (!habit.isNotifyEnabled()) {
            System.out.println("\nNotifications are disabled. Enable notifications to customize times");
            return;
        }
        if (!habit.getHabitReminder().isDefault() && !processOverrideInput(habit)) {
            return;
        }
        String numMessage = "How many notifications would you like to receive" + getPeriodString(habit.getPeriod(),
                " per day", " per week", " per month") + "?";
        int numNotifications = getNumNotifications(numMessage, habit.getPeriod());
        Set<LocalDateTime> reminders = new HashSet<>();
        Set<Pair<Integer, LocalTime>> monthlyPairs = new HashSet<>();
        for (int i = 0; i < numNotifications; i++) {
            processDateTimeInput(habit, reminders, monthlyPairs, i);
        }
        storeNotifications(habit, reminders, monthlyPairs);
        isSaved = false;
    }

    // REQUIRES: habit.isNotifyEnabled() is true
    // MODIFIES: this
    // EFFECTS: processes input and returns true if user wants to override current notifications, false otherwise
    private boolean processOverrideInput(Habit habit) {
        List<String> validInputs = new ArrayList<>(Arrays.asList("d", "k", "o"));
        String command;
        do {
            System.out.println("You have already customized notifications.");
            System.out.println("\td -> Revert to default notifications");
            System.out.println("\tk -> Keep current notifications");
            System.out.println("\to -> Override current notifications");
            command = input.next();
        } while (!validInputs.contains(command));
        switch (command) {
            case "d":
                habit.getHabitReminder().setDefaultReminders();
                isSaved = false;
                return false;
            case "k":
                return false;
            default:
                return true;
        }
    }

    // MODIFIES: this
    // EFFECTS: stores notifications for given habit, sets LocalDateTime reminders for daily and weekly habits,
    //          and sets Pair<Integer, LocalTime> reminders for monthly habits
    private void storeNotifications(Habit habit, Set<LocalDateTime> reminders, Set<Pair<Integer, LocalTime>> pairs) {
        if (habit.getPeriod() == Period.MONTHLY) {
            MonthlyReminder monthlyReminder = (MonthlyReminder) habit.getHabitReminder();
            monthlyReminder.setCustomMonthlyReminders(pairs);
        } else {
            habit.getHabitReminder().setCustomReminders(reminders);
        }
    }

    // MODIFIES: reminders, pairs
    // EFFECTS: processes time input for given habit, prompts user for time input based on period
    private void processDateTimeInput(Habit habit, Set<LocalDateTime> reminders,
                                      Set<Pair<Integer, LocalTime>> pairs, int i) {
        switch (habit.getPeriod()) {
            case DAILY:
                processDailyInput(reminders, i);
                break;
            case WEEKLY:
                processWeeklyInput(reminders, i);
                break;
            case MONTHLY:
                processMonthlyInput(pairs, i);
        }
    }

    // MODIFIES: reminders
    // EFFECTS: prompts user for time input for daily reminders, adding to reminders and ensuring no duplicates
    private void processDailyInput(Set<LocalDateTime> reminders, int i) {
        do {
            int hours = processHourInput(i);
            int minutes = processMinuteInput(i);
            LocalDateTime reminder = DailyReminder.makeDailyReminder(LocalTime.of(hours, minutes), clock);
            int size = reminders.size();
            reminders.add(reminder);
            if (size != reminders.size()) {
                break;
            } else {
                System.out.println("Reminder already exists for that time");
            }
        } while (true);
    }

    // MODIFIES: reminders
    // EFFECTS: prompts user for day and time input for weekly reminders, adding to reminders and ensuring no duplicates
    private void processWeeklyInput(Set<LocalDateTime> reminders, int i) {
        do {
            DayOfWeek dayOfWeek = processDayOfWeekInput(i);
            int hours = processHourInput(i);
            int minutes = processMinuteInput(i);
            LocalDateTime reminder = WeeklyReminder.makeWeeklyReminder(dayOfWeek, LocalTime.of(hours, minutes), clock);
            int size = reminders.size();
            reminders.add(reminder);
            if (size != reminders.size()) {
                break;
            } else {
                System.out.println("Reminder already exists for that time");
            }
        } while (true);
    }

    // MODIFIES: pairs
    // EFFECTS: prompts user for day and time input for monthly reminders, adding to pairs and ensuring no duplicates
    private void processMonthlyInput(Set<Pair<Integer, LocalTime>> pairs, int i) {
        do {
            int dayOfMonth = processDayOfMonthInput(i);
            int hours = processHourInput(i);
            int minutes = processMinuteInput(i);
            LocalTime time = LocalTime.of(hours, minutes);
            Pair<Integer, LocalTime> pair = new Pair<>(dayOfMonth, time);
            int size = pairs.size();
            pairs.add(pair);
            if (size != pairs.size()) {
                break;
            } else {
                System.out.println("Reminder already exists for that time");
            }
        } while (true);
    }

    // EFFECTS: prompts user for day of week input
    private DayOfWeek processDayOfWeekInput(int i) {
        DayOfWeek dayOfWeek;
        do {
            String message = "Enter day of week (1 for Sunday, 2 for Monday, ...) for notification " + (i + 1) + ": ";
            System.out.println(message);
            if (input.hasNextInt()) {
                int day = input.nextInt();
                if (day >= 1 && day <= 7) {
                    int translatedDay = day == 1 ? 7 : day - 1;
                    dayOfWeek = DayOfWeek.of(translatedDay);
                    break;
                }
            } else {
                input.next();
            }
        } while (true);
        return dayOfWeek;
    }

    // EFFECTS: prompts user for day of month input
    private int processDayOfMonthInput(int i) {
        int dayOfMonth;
        do {
            System.out.println("Enter day of month (1-31) for notification " + (i + 1) + ": ");
            if (input.hasNextInt()) {
                dayOfMonth = input.nextInt();
                if (dayOfMonth >= 1 && dayOfMonth <= MONTH_MAX_DAYS) {
                    break;
                }
            } else {
                input.next();
            }
        } while (true);
        return dayOfMonth;
    }

    // EFFECTS: prompts user for hour input
    private int processHourInput(int i) {
        int hours;
        do {
            System.out.println("Enter hour (0-23) for notification " + (i + 1) + ": ");
            if (input.hasNextInt()) {
                hours = input.nextInt();
                if (hours >= 0 && hours < 24) {
                    break;
                }
            } else {
                input.next();
            }
        } while (true);
        return hours;
    }

    // EFFECTS: prompts user for minute input
    private int processMinuteInput(int i) {
        int minutes;
        do {
            System.out.println("Enter minute (0-59) for notification " + (i + 1) + ": ");
            if (input.hasNextInt()) {
                minutes = input.nextInt();
                if (minutes >= 0 && minutes < 60) {
                    break;
                }
            } else {
                input.next();
            }
        } while (true);
        return minutes;
    }

    // EFFECTS: prompts user for number of notifications, restricted between 1 and max, inclusive
    //          max is 31 for monthly, MAX_FREQUENCY for daily and weekly
    private int getNumNotifications(String message, Period period) {
        int max = period == Period.MONTHLY ? MONTH_MAX_DAYS : MAX_FREQUENCY;
        int numNotifications;
        do {
            System.out.println(message);
            if (input.hasNextInt()) {
                numNotifications = input.nextInt();
                if (numNotifications > 0 && numNotifications <= max) {
                    break;
                } else {
                    System.out.println("Number of notifications must be between 1 and " + max);
                }
            } else {
                input.next();
            }
        } while (true);
        return numNotifications;
    }

    // MODIFIES: this
    // EFFECTS: saves habit manager to file
    private void saveHabitManager() {
        JsonWriter jsonWriter = new JsonWriter(HABIT_MANAGER_STORE);
        try {
            jsonWriter.open();
            jsonWriter.write(habitManager);
            jsonWriter.close();
            System.out.println("Your habits have been saved successfully!");
            isSaved = true;
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + HABIT_MANAGER_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: changes username to input provided by user
    private void changeUsername() {
        System.out.println("Enter new username: ");
        HabitManager.setUsername(input.next());
        isSaved = false;
        System.out.println("Username changed to " + HabitManager.getUsername());
    }

    // EFFECTS: returns appropriate string based on the period
    private String getPeriodString(Period period, String day, String week, String month) {
        String periodString = null;
        switch (period) {
            case DAILY:
                periodString = day;
                break;
            case WEEKLY:
                periodString = week;
                break;
            case MONTHLY:
                periodString = month;
        }
        return periodString;
    }
}