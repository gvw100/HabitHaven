package ui;

import model.Habit;
import model.HabitManager;
import model.Period;

import java.util.List;
import java.util.Scanner;

// Habit tracker application
public class HabitApp {
    private Scanner input;
    private HabitManager habitManager;

    // EFFECTS: starts the application
    HabitApp() {
        habitManager = new HabitManager();
        startApp();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void startApp() {
        setupScanner();
        menu();
    }

    private void menu() {
        boolean keepGoing = true;
        String command;
        do {
            displayMenu();
            command = input.next().toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
                System.out.println("Goodbye!");
            } else {
                processMenuInput(command);
            }
        } while (keepGoing);
    }

    // EFFECTS: initializes scanner
    private void setupScanner() {
        input = new Scanner(System.in);
        input.useDelimiter("\n");
    }

    // EFFECTS: displays menu with choices
    private void displayMenu() {
        System.out.println("Welcome to HabitHaven!\n");
        System.out.println("Select from:\n");
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
                System.out.println("Invalid command");
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a habit and adds it to habitManager.habits
    private void createHabit() {
        String name = getHabitName();
        String description = getHabitDescription();
        Period period = getHabitPeriod();
        int frequency = getHabitFrequency();
        Habit habit = new Habit(name, description, period, frequency);
        habitManager.addHabit(habit);
    }

    private String getHabitName() {
        System.out.println("Enter habit name: ");
        return input.next();
    }

    private String getHabitDescription() {
        System.out.println("Enter habit description: ");
        return input.next();
    }

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

    private void viewHabits() {
        int index;
        List<Habit> habits = habitManager.getHabits();
        do {
            System.out.println("Here are your habits! Select from the following: ");
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

    private void viewHabit(Habit habit) {
        boolean inputIsInvalid;
        do {
            inputIsInvalid = false;
            displayHabit(habit);
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
                case "h":
                    viewHabits();
                    break;
                case "m":
                    menu();
                default:
                    inputIsInvalid = true;
            }
        } while (inputIsInvalid);
    }

    private void displayHabit(Habit habit) {
        System.out.println("Name: " + habit.getName());
        System.out.println("Description: " + habit.getDescription());
        System.out.println("Period: " + habit.getPeriod());
        System.out.println("Frequency: " + habit.getFrequency());
        System.out.println("Number of completions: " + habit.getNumSuccess());
        System.out.println("\n\te -> Edit habit");
        System.out.println("\td -> Delete habit");
        System.out.println("\tf -> Finish habit");
        System.out.println("\th -> Back to habit list");
        System.out.println("\tm -> Back to menu");
    }

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

    private void displayEditOptions() {
        System.out.println("Select one of the following: ");
        System.out.println("\tn -> Change name");
        System.out.println("\td -> Change description");
        System.out.println("\tp -> Change period");
        System.out.println("\tf -> Change frequency");
        System.out.println("\tb -> Back to habit");
    }

    private void changeName(Habit habit) {
        habit.setName(getHabitName());
        editHabit(habit);
    }

    private void changeDescription(Habit habit) {
        habit.setDescription(getHabitDescription());
        editHabit(habit);
    }

    private void changePeriod(Habit habit) {
        habit.setPeriod(getHabitPeriod());
        editHabit(habit);
    }

    private void changeFrequency(Habit habit) {
        habit.setFrequency(getHabitFrequency());
        editHabit(habit);
    }

    private void deleteHabit(Habit habit) {
        String command;
        do {
            System.out.println("Are you sure you want to delete this habit? y/n");
            command = input.next();
        } while (!((command.equals("y")) || (command.equals("n"))));
        if (command.equals("y")) {
            habitManager.deleteHabit(habit);
            viewHabits();
        } else {
            viewHabit(habit);
        }
    }

    private void finishHabit(Habit habit) {
        habit.finishHabit();
        viewHabit(habit);
    }
}