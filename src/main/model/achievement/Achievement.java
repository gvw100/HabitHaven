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

    // EFFECTS: returns true if the achievement is equal to the given object, for testing purposes
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Achievement)) {
            return false;
        }
        Achievement a = (Achievement) o;
        return target == a.target && name.equals(a.name) && description.equals(a.description) && type == a.type
                && tier == a.tier;
    }
}
