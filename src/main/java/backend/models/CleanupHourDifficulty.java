package backend.models;

public enum CleanupHourDifficulty {
    EASY(0),
    MEDIUM(1),
    HARD(2);

    private final int difficulty;

    CleanupHourDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficultyValue() {
        return difficulty;
    }
}
