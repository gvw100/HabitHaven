package model.achievement;

// Represents a habit achievement
public class Achievement {
    private final String name;
    private final String description;
    private final int target;
    private final AchievementType type;
    private final AchievementTier tier;

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
}