package net.edge.content.achievements;

/**
 * The types of achievement difficulties.
 *
 * @author Daniel
 */
public enum AchievementDifficulty {
    /**
     * The easy achievement difficulty.
     */
    EASY(75_000, 0x1C889E),

    /**
     * The medium achievement difficulty.
     */
    MEDIUM(225_000, 0xD9750B),

    /**
     * The hard achievement difficulty.
     */
    HARD(750_000, 0xC41414),

    /**
     * The elite achievement difficulty.
     */
    ELITE(1_500_000, 0xC52BE0);

    /**
     * The reward point for completing the achievement
     */
    private final int reward;

    /**
     * The color of the completion banner
     */
    private final int color;

    /**
     * Achievement difficulty
     */
    AchievementDifficulty(int reward, int color) {
        this.reward = reward;
        this.color = color;
    }

    /**
     * Gets the reward point
     */
    public int getReward() {
        return reward;
    }

    /**
     * Gets the color
     */
    public int getColor() {
        return color;
    }
}