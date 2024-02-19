package ui;

import model.Habit;
import model.HabitManager;
import model.Period;

import java.time.Clock;
import java.util.List;
import java.util.Scanner;

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
        Habit habit = new Habit(name, description, period, frequency, clock);
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
    // EFFECTS: brings user to the appropriate habit tool according to input
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
        int streak = habit.getStreak();
        int bestStreak = habit.getBestStreak();
        int totalNumSuccess = habit.getTotalNumSuccess();
        int numPeriodSuccess = habit.getNumPeriodSuccess();
        int numPeriod = habit.getNumPeriod();
        int successRate = habit.getSuccessRate(habit.isPeriodComplete());
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