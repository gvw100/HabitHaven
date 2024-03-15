package model.achievement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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
    void testEquals() {
        assertNotEquals(2, achievement);
        assertNotEquals(null, achievement);
        Achievement name = new Achievement("name", "Test", 1, AchievementType.STREAK, AchievementTier.BRONZE);
        Achievement description = new Achievement("Test", "description", 1, AchievementType.STREAK, AchievementTier.BRONZE);
        Achievement target = new Achievement("Test", "Test", 2, AchievementType.STREAK, AchievementTier.BRONZE);
        Achievement type = new Achievement("Test", "Test", 1, AchievementType.SINGULAR_SUCCESSES, AchievementTier.BRONZE);
        Achievement tier = new Achievement("Test", "Test", 1, AchievementType.STREAK, AchievementTier.GOLD);
        assertNotEquals(achievement, name);
        assertNotEquals(achievement, description);
        assertNotEquals(achievement, target);
        assertNotEquals(achievement, type);
        assertNotEquals(achievement, tier);
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash("Test", "Test", 1, AchievementType.STREAK, AchievementTier.BRONZE), achievement.hashCode());
    }
}
