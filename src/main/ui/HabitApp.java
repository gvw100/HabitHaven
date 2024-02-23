package ui;

import javafx.util.Pair;
import model.*;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

// Habit tracker application
// Some of the code here is inspired from the TellerApp.java class in the CPSC 210 course
// https://github.students.cs.ubc.ca/CPSC210/TellerApp
public class HabitApp {
    private Scanner input;
    private final HabitManager habitManager;
    private final Clock clock;

    // EFFECTS: starts the application
    HabitApp() {
        habitManager = new HabitManager();
        clock = Clock.systemDefaultZone();
        startApp();
    }

    // MODIFIES: this
    // EFFECTS: setup scanner, display menu, and process input
    private void startApp() {
        setupScanner();
        menu();
    }

    // MODIFIES: this
    // EFFECTS: displays menu and processes menu input
    private void menu() {
        String command;
        do {
            displayMenu();
            command = input.next().toLowerCase();

            if (command.equals("q")) {
                System.out.println("\nGoodbye!");
                System.exit(0);
            } else {
                processMenuInput(command);
            }
        } while (true);
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
            default:
                System.out.println("\nInvalid command");
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a habit and adds it to habitManager.habits
    private void createHabit() {
        String name = getHabitName();
        String description = getHabitDescription();
        Period period = getHabitPeriod();
        int frequency = getHabitFrequency();
        boolean notificationEnabled = getNotificationEnabled();
        Habit habit = new Habit(name, description, period, frequency, notificationEnabled, clock);
        habitManager.addHabit(habit);
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
        boolean inputIsValid = false;
        do {
            System.out.println("Select from the following habit periods: ");
            System.out.println("\td -> Daily");
            System.out.println("\tw -> Weekly");
            System.out.println("\tm -> Monthly");
            switch (input.next()) {
                case "d":
                    period = Period.DAILY;
                    inputIsValid = true;
                    break;
                case "w":
                    period = Period.WEEKLY;
                    inputIsValid = true;
                    break;
                case "m":
                    period = Period.MONTHLY;
                    inputIsValid = true;
            }
        } while (!inputIsValid);
        return period;
    }

    // EFFECTS: returns habit frequency entered by user restricted between 1 and 15
    private int getHabitFrequency() {
        int frequency;
        do {
            System.out.println("Enter habit frequency: ");
            if (input.hasNextInt()) {
                frequency = input.nextInt();
                if (frequency > 0 && frequency < 16) {
                    break;
                } else {
                    System.out.println("Frequency must be between 1 and 15");
                }
            } else {
                input.next();
            }
        } while (true);
        return frequency;
    }

    // EFFECTS: returns whether the user wants to enable notifications
    private boolean getNotificationEnabled() {
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
                    break;
                }
            } else if (input.next().equals("m")) {
                menu();
            }
        } while (true);
        viewHabit(habits.get(index - 1));
    }

    // MODIFIES: this
    // EFFECTS: view habit and process habit input commands
    private void viewHabit(Habit habit) {
        boolean inputIsInvalid;
        do {
            habit.updateHabit();
            displayHabit(habit);
            inputIsInvalid = processHabitInput(habit);
        } while (inputIsInvalid);
    }

    // MODIFIES: this
    // EFFECTS: brings user to the appropriate habit tool according to input, returns whether input is invalid
    @SuppressWarnings("methodlength")
    private boolean processHabitInput(Habit habit) {
        switch (input.next()) {
            case "e":
                editHabit(habit);
                break;
            case "d":
                deleteHabit(habit);
                break;
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
                viewHabits();
                break;
            case "m":
                menu();
                break;
            default:
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
        System.out.println("\tm -> Back to menu");
    }

    // MODIFIES: this
    // EFFECTS: displays edit options and processes edit input
    private void editHabit(Habit habit) {
        boolean inputIsInvalid;
        do {
            inputIsInvalid = false;
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
                case "f":
                    changeFrequency(habit);
                case "b":
                    viewHabit(habit);
                default:
                    inputIsInvalid = true;
            }
        } while (inputIsInvalid);
    }

    // EFFECTS: displays options for habit editing
    private void displayEditOptions() {
        System.out.println("\nSelect one of the following: \n");
        System.out.println("\tn -> Change name");
        System.out.println("\td -> Change description");
        System.out.println("\tp -> Change period");
        System.out.println("\tf -> Change frequency");
        System.out.println("\tb -> Back to habit");
    }

    // MODIFIES: this
    // EFFECTS: changes name to input provided by user
    private void changeName(Habit habit) {
        habit.setName(getHabitName());
        editHabit(habit);
    }

    // MODIFIES: this
    // EFFECTS: changes description to input provided by user
    private void changeDescription(Habit habit) {
        habit.setDescription(getHabitDescription());
        editHabit(habit);
    }

    // MODIFIES: this
    // EFFECTS: changes period to period selected by user
    private void changePeriod(Habit habit) {
        habit.setPeriod(getHabitPeriod());
        editHabit(habit);
    }

    // MODIFIES: this
    // EFFECTS: changes frequency to frequency selected by user between 1 and 15
    private void changeFrequency(Habit habit) {
        habit.setFrequency(getHabitFrequency());
        editHabit(habit);
    }

    // MODIFIES: this
    // EFFECTS: deletes habit after confirming user action
    private void deleteHabit(Habit habit) {
        String command;
        do {
            System.out.println("\nAre you sure you want to delete this habit? y/n");
            command = input.next();
        } while (!((command.equals("y")) || (command.equals("n"))));
        if (command.equals("y")) {
            habitManager.deleteHabit(habit);
            System.out.println("\nHabit deleted successfully");
            viewHabits();
        } else {
            System.out.println();
            viewHabit(habit);
        }
    }

    // MODIFIES: this
    // EFFECTS: marks habit has complete, incrementing numSuccess, awarding user for successful completion of period
    private void finishHabit(Habit habit) {
        String periodString = getPeriodString(habit.getPeriod(), "today.", "this week.", "this month.");
        boolean isCompleted = habit.finishHabit();
        if (!isCompleted) {
            System.out.println("\nYou have already completed the habit for " + periodString);
        } else if (habit.isPeriodComplete()) {
            System.out.println("\nYou successfully completed " + habit.getName() + " " + habit.getFrequency()
                    + " times " + periodString);
            System.out.println("Great job!");
        } else {
            String message = "\nYou completed " + habit.getName() + " " + habit.getNumSuccess()
                    + " times so far " + periodString + " Keep it up!";
            System.out.println(message);
        }
        viewHabit(habit);
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
        viewHabit(habit);
    }

    // EFFECTS: processes input and returns whether the input is invalid
    private boolean processStatInput() {
        return !input.next().equals("b");
    }

    // MODIFIES: this
    // EFFECTS: displays notification options and processes input
    private void customizeNotifications(Habit habit) {
        boolean inputIsInvalid;
        do {
            inputIsInvalid = false;
            displayNotificationOptions();
            switch (input.next()) {
                case "e":
                    enableNotifications(habit);
                    break;
                case "d":
                    disableNotifications(habit);
                    break;
                case "c":
                    customizeNotificationTimes(habit);
                    break;
                case "b":
                    viewHabit(habit);
                    break;
                default:
                    inputIsInvalid = true;
            }
        } while (inputIsInvalid);
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
        } else {
            System.out.println("\nNotifications already enabled");
        }
        customizeNotifications(habit);
    }

    // MODIFIES: this
    // EFFECTS: disables notifications for given habit
    private void disableNotifications(Habit habit) {
        boolean wasChanged = habit.setNotifyEnabled(false);
        if (wasChanged) {
            System.out.println("\nNotifications disabled");
        } else {
            System.out.println("\nNotifications already disabled");
        }
        customizeNotifications(habit);
    }

    // MODIFIES: this
    // EFFECTS: customizes notification times for given habit, if already customized, user can revert to default,
    //          keep current notifications, or override current notifications
    private void customizeNotificationTimes(Habit habit) {
        if (!habit.isNotifyEnabled()) {
            System.out.println("\nNotifications are disabled. Enable notifications to customize times");
            customizeNotifications(habit);
        }
        if (!habit.getHabitReminder().isDefault() && processOverrideInput(habit)) {
            customizeNotifications(habit);
        }
        String numMessage = "How many notifications would you like to receive" + getPeriodString(habit.getPeriod(),
                " per day", " per week", " per month") + "?";
        int numNotifications = getNumNotifications(numMessage);
        Set<LocalDateTime> reminders = new HashSet<>();
        Set<Pair<Integer, LocalTime>> monthlyPairs = new HashSet<>();
        for (int i = 0; i < numNotifications; i++) {
            processTimeInput(habit, reminders, monthlyPairs, i);
        }
        storeNotifications(habit, reminders, monthlyPairs);
        customizeNotifications(habit);
    }

    // EFFECTS: processes input and returns false if user wants to override current notifications, true otherwise
    private boolean processOverrideInput(Habit habit) {
        List<String> validInputs = new ArrayList<>(Arrays.asList("d", "k", "o"));
        System.out.println("You have already customized notifications.");
        System.out.println("\td -> Revert to default notifications");
        System.out.println("\tk -> Keep current notifications");
        System.out.println("\to -> Override current notifications");
        String command;
        do {
            command = input.next();
        } while (!validInputs.contains(command));
        switch (command) {
            case "d":
                habit.getHabitReminder().setDefaultReminders();
                return true;
            case "k":
                return true;
            default:
                return false;
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

    // EFFECTS: processes time input for given habit, prompts user for time input based on period
    private void processTimeInput(Habit habit, Set<LocalDateTime> times, Set<Pair<Integer, LocalTime>> pairs, int i) {
        switch (habit.getPeriod()) {
            case DAILY:
                processDailyTimeInput(times, i);
                break;
            case WEEKLY:
                processWeeklyTimeInput(times, i);
                break;
            case MONTHLY:
                processMonthlyTimeInput(pairs, i);
        }
    }

    // EFFECTS: prompts user for time input for daily reminders
    private void processDailyTimeInput(Set<LocalDateTime> reminders, int i) {
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

    // EFFECTS: prompts user for time input for weekly reminders
    private void processWeeklyTimeInput(Set<LocalDateTime> reminders, int i) {
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

    // EFFECTS: prompts user for time input for monthly reminders
    private void processMonthlyTimeInput(Set<Pair<Integer, LocalTime>> pairs, int i) {
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
                if (dayOfMonth >= 1 && dayOfMonth <= 31) {
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

    // EFFECTS: prompts user for number of notifications, restricted between 1 and 15, inclusive
    private int getNumNotifications(String message) {
        int numNotifications;
        do {
            System.out.println(message);
            if (input.hasNextInt()) {
                numNotifications = input.nextInt();
                if (numNotifications > 0 && numNotifications < 16) {
                    break;
                } else {
                    System.out.println("Number of notifications must be between 1 and 15");
                }
            } else {
                input.next();
            }
        } while (true);
        return numNotifications;
    }

    // EFFECTS: selects appropriate string based on the period
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