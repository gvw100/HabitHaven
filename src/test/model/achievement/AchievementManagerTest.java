package model.achievement;

import model.HabitStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AchievementManagerTest {
    private HabitStatistics h1;
    private HabitStatistics h2;

    @BeforeEach
    void runBefore() {
        h1 = new HabitStatistics(3, 10, 50, 20, 79);
        h2 = new HabitStatistics(5, 10, 50, 20, 79);
    }

    @Test
    public void testGetAchieved() {
        // TODO
    }
}
