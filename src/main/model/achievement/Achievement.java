package model.achievement;

import java.util.Objects;

// Represents a habit achievement
public class Achievement {
    private final String name;
    private final String description;
    private final int target;
    private final AchievementType type;
    private final AchievementTier tier;

    // EFFECTS: constructs an achievement with a name, description, target, type, and tier
    public Achievement(String name, String description, int target, AchievementType type, AchievementTier tier) {
        this.name = name;
        this.description = description;
        this.target = target;
        this.type = type;
        this.tier = tier;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getTarget() {
        return this.target;
    }

    public AchievementType getType() {
        return this.type;
    }

    public AchievementTier getTier() {
        return this.tier;
    }

    // EFFECTS: returns true if the achievement is equal to the given object, for testing purposes
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Achievement that = (Achievement) o;
        return target == that.target && Objects.equals(name, that.name) && Objects.equals(description, that.description)
                && type == that.type && tier == that.tier;
    }

    // EFFECTS: returns hashcode of achievement
    @Override
    public int hashCode() {
        return Objects.hash(name, description, target, type, tier);
    }
}
