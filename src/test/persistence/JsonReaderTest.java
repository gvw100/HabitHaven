package persistence;

import model.HabitManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// A test class for JsonReader
// Citation: Code inspired by JsonSerializationDemo https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonReaderTest extends JsonTest {
    private JsonReader reader;
    private HabitManager hm;

   @Test
    void testReaderNonExistentFile() {
        reader = new JsonReader("./data/noSuchFile.json");
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void testReaderEmptyHabitManager() {
        reader = new JsonReader("./data/testReaderEmptyHabitManager.json");
        try {
            hm = reader.read();
            assertEquals("gregor kiczales", HabitManager.getUsername());
            assertFalse(HabitManager.isAutoSave());
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
            assertTrue(HabitManager.isAutoSave());
            assertEquals("Gavin", HabitManager.getUsername());
            requestCheckHabit1(hm);
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}