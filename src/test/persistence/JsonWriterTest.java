package persistence;

import model.HabitManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// A test class for JsonWriter
// Citation: Code inspired by JsonSerializationDemo https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
        assertThrows(IOException.class, writer::open);
    }

    @Test
    void testWriterEmptyHabitManager() {
        try {
            HabitManager hm = new HabitManager();
            HabitManager.setUsername("Gavin");
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyHabitManager.json");
            writer.open();
            writer.write(hm);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyHabitManager.json");
            hm = reader.read();
            assertEquals("Gavin", HabitManager.getUsername());
            assertFalse(HabitManager.isAutoSave());
            assertEquals(0, hm.getSize());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralHabitManager() {
        try {
            JsonReader reader1 = new JsonReader("./data/testReaderGeneralHabitManager.json");
            HabitManager hm = reader1.read();
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralHabitManager.json");
            writer.open();
            writer.write(hm);
            writer.close();
            JsonReader reader2 = new JsonReader("./data/testWriterGeneralHabitManager.json");
            hm = reader2.read();
            assertEquals(6, hm.getSize());
            assertTrue(HabitManager.isAutoSave());
            assertEquals("Gavin", HabitManager.getUsername());
            requestCheckHabit1(hm);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}