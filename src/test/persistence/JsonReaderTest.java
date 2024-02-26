package persistence;

import javafx.util.Pair;
import model.Habit;
import model.HabitManager;
import model.Period;
import org.junit.jupiter.api.Test;
import ui.HabitApp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Citation: Code inspired by JsonSerializationDemo https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonReaderTest extends JsonTest {
    private JsonReader reader;
    private HabitManager hm;

   @Test
    void testReaderNonExistentFile() {
        reader = new JsonReader("./data/noSuchFile.json");
        assertThrows(IOException.class, () -> reader.read());
    }

    @Test
    void testReaderEmptyHabitManager() {
        reader = new JsonReader("./data/testReaderEmptyHabitManager.json");
        try {
            hm = reader.read();
            assertEquals(0, hm.getSize());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralHabitManager() {
        reader = new JsonReader("./data/testReaderGeneralHabitManager.json");
        try {
            hm = reader.read();
            assertEquals(6, hm.getSize());
            assertEquals("Gavin", HabitApp.getUsername());
            requestCheckHabit1();
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    private void requestCheckHabit1() {
       Habit habit = hm.getHabits().get(0);
       String n = "dsci revision";
       String d = "revise for dsci mt once per day";
       Period p = Period.DAILY;
       int f = 1;
       UUID id = UUID.fromString("ffa469d5-ec39-438b-964e-703a6b84544b");
       boolean ne = true;
       int ns = 0;
       LocalDateTime cpe = LocalDateTime.parse("2024-02-25T23:59");
       LocalDateTime npe = LocalDateTime.parse("2024-02-26T23:59");
       boolean ipc = false;
       checkHabit(habit, n, d, p, f, id, ne, ns, cpe, npe, ipc);

       int s, bs, tns, nps, np;
       s = bs = tns = nps = np = 0;
       checkHabitStatistics(habit, s, bs, tns, nps, np);

       int fr = 1;
       Set<LocalDateTime> reminders = new HashSet<>();
       reminders.add(LocalDateTime.parse("2024-02-25T18:30"));
       reminders.add(LocalDateTime.parse("2024-02-25T17:00"));
       boolean isDefault = false;
       checkDailyReminder(habit, fr, reminders, isDefault);

       requestCheckHabit2();
    }

    private void requestCheckHabit2() {
       Habit habit = hm.getHabits().get(1);
       String n = "jogging";
       String d = "jog twice per week";
       Period p = Period.WEEKLY;
       int f = 2;
       UUID id = UUID.fromString("42f3bb31-6161-48c2-aec4-5f3eec0d9a19");
       boolean ne = false;
       int ns = 1;
       LocalDateTime cpe = LocalDateTime.parse("2024-03-02T23:59");
       LocalDateTime npe = LocalDateTime.parse("2024-03-09T23:59");
       boolean ipc = true;
       checkHabit(habit, n, d, p, f, id, ne, ns, cpe, npe, ipc);

       int s, bs, tns, nps, np;
       s = 7;
       bs = 10;
       tns = 35;
       nps = 17;
       np = 19;
       checkHabitStatistics(habit, s, bs, tns, nps, np);

       requestCheckHabit3();
    }

    private void requestCheckHabit3() {
       Habit habit = hm.getHabits().get(2);
       String n = "math 200 grind";
       String d = "git gud 15 times per day";
       Period p = Period.DAILY;
       int f = 15;
       UUID id = UUID.fromString("bc04c252-5279-4eb7-9963-59c50c92946b");
       boolean ne = true;
       int ns = 0;
       LocalDateTime cpe = LocalDateTime.parse("2024-02-25T23:59");
       LocalDateTime npe = LocalDateTime.parse("2024-02-26T23:59");
       boolean ipc = false;
       checkHabit(habit, n, d, p, f, id, ne, ns, cpe, npe, ipc);

       int s, bs, tns, nps, np;
       s = 0;
       bs = 2;
       tns = 30;
       nps = 2;
       np = 3;
       checkHabitStatistics(habit, s, bs, tns, nps, np);

       int fr = 15;
       Set<LocalDateTime> reminders = new HashSet<>();
       int intervalMin = 48;
       for (int i = 0; i < 15; i++) {
           reminders.add(LocalDateTime.parse("2024-02-25T09:00").plusMinutes(intervalMin * i));
       }
       boolean isDefault = true;
       checkDailyReminder(habit, fr, reminders, isDefault);

       requestCheckHabit4();
    }

    private void requestCheckHabit4() {
       Habit habit = hm.getHabits().get(3);
       String n = "reading book";
       String d = "read two books per month";
       Period p = Period.MONTHLY;
       int f = 2;
       UUID id = UUID.fromString("a13e7b82-dfc7-43f1-80b0-4163a6317c4c");
       boolean ne = true;
       int ns = 1;
       LocalDateTime cpe = LocalDateTime.parse("2024-02-29T23:59");
       LocalDateTime npe = LocalDateTime.parse("2024-03-31T23:59");
       boolean ipc = true;
       checkHabit(habit, n, d, p, f, id, ne, ns, cpe, npe, ipc);

       int s, bs, tns, nps, np;
       s = 1;
       bs = 1;
       tns = 2;
       nps = 1;
       np = 5;
       checkHabitStatistics(habit, s, bs, tns, nps, np);

       Set<LocalDateTime> reminders = new HashSet<>();
       for (int i = 0; i < 29; i++) {
           int day = i + 1;
           if (day < 10) {
                reminders.add(LocalDateTime.parse("2024-02-0" + day + "T09:00"));
              } else {
                reminders.add(LocalDateTime.parse("2024-02-" + day + "T09:00"));
           }
       }
       boolean isDefault = true;
       checkMonthlyReminder(habit, null, reminders, isDefault);

       requestCheckHabit5();
    }

    private void requestCheckHabit5() {
        Habit habit = hm.getHabits().get(4);
        String n = "make personal project";
        String d = "make personal project once per month";
        Period p = Period.MONTHLY;
        int f = 1;
        UUID id = UUID.fromString("f68fd6ee-a514-49c2-b3d8-3ff265e4ebac");
        boolean ne = true;
        int ns = 0;
        LocalDateTime cpe = LocalDateTime.parse("2024-02-29T23:59");
        LocalDateTime npe = LocalDateTime.parse("2024-03-31T23:59");
        boolean ipc = false;
        checkHabit(habit, n, d, p, f, id, ne, ns, cpe, npe, ipc);

        int s, bs, tns, nps, np;
        s = bs = tns = nps = np = 0;
        checkHabitStatistics(habit, s, bs, tns, nps, np);

        Set<Pair<Integer, LocalTime>> customReminders = new HashSet<>();
        customReminders.add(new Pair<>(1, LocalTime.of(17, 0)));
        customReminders.add(new Pair<>(5, LocalTime.of(17, 0)));
        customReminders.add(new Pair<>(10, LocalTime.of(17, 0)));
        customReminders.add(new Pair<>(15, LocalTime.of(17, 0)));
        customReminders.add(new Pair<>(20, LocalTime.of(17, 0)));
        customReminders.add(new Pair<>(25, LocalTime.of(17, 0)));
        customReminders.add(new Pair<>(30, LocalTime.of(17, 0)));
        Set<LocalDateTime> reminders = new HashSet<>();
        reminders.add(LocalDateTime.parse("2024-02-01T17:00"));
        reminders.add(LocalDateTime.parse("2024-02-05T17:00"));
        reminders.add(LocalDateTime.parse("2024-02-10T17:00"));
        reminders.add(LocalDateTime.parse("2024-02-15T17:00"));
        reminders.add(LocalDateTime.parse("2024-02-20T17:00"));
        reminders.add(LocalDateTime.parse("2024-02-25T17:00"));
        reminders.add(LocalDateTime.parse("2024-02-29T17:00"));
        boolean isDefault = false;
        checkMonthlyReminder(habit, customReminders, reminders, isDefault);

        requestCheckHabit6();
    }

    private void requestCheckHabit6() {
       Habit habit = hm.getHabits().get(5);
       String n = "resistance bands";
       String d = "do resistance bands 5 times per week";
       Period p = Period.WEEKLY;
       int f = 5;
       UUID id = UUID.fromString("9b2ccb3f-3d9b-451a-a18f-79695bf1850f");
       boolean ne = true;
       int ns = 5;
       LocalDateTime cpe = LocalDateTime.parse("2024-03-02T23:59");
       LocalDateTime npe = LocalDateTime.parse("2024-03-09T23:59");
       boolean ipc = true;
       checkHabit(habit, n, d, p, f, id, ne, ns, cpe, npe, ipc);

       int s, bs, tns, nps, np;
       s = 1;
       bs = 1;
       tns = 5;
       nps = 1;
       np = 0;
       checkHabitStatistics(habit, s, bs, tns, nps, np);

       Set<LocalDateTime> reminders = new HashSet<>();
       for (int i = 0; i < 7; i++) {
           reminders.add(LocalDateTime.parse("2024-02-25T16:30").plusDays(i));
       }
       boolean isDefault = false;
       checkWeeklyReminder(habit, reminders, isDefault);
    }
}
