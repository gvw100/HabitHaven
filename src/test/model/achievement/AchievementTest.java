package model.achievement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AchievementTest {
    private Achievement achievement;

    @BeforeEach
    void runBefore() {
        achievement = new Achievement("Test", "Test", 1, AchievementType.STREAK, AchievementTier.BRONZE);
    }

    @Test
    void testConstructor() {
        assertEquals("Test", achievement.getName());
        assertEquals("Test", achievement.getDescription());
        assertEquals(1, achievement.getTarget());
        assertEquals(AchievementType.STREAK, achievement.getType());
        assertEquals(AchievementTier.BRONZE, achievement.getTier());
    }

    @Test
    void testEqualsDifferentTypes() {
        Achievement achievement = new Achievement("name", "description", 3, AchievementType.STREAK, AchievementTier.BRONZE);
        assertFalse(achievement.equals(2));
    }
}
