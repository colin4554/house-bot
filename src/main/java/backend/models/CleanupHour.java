package backend.models;

public class CleanupHour {
    private String name;
    private String dueDay;
    private String dueTime;

    private int worth;
    private String link;

    private CleanupHourDifficulty difficulty;

    private String bathroomFloor;

    public CleanupHour(String name, String dueDay, String dueTime, int worth, String link, CleanupHourDifficulty difficulty, String bathroomFloor) {
        this.name = name;
        this.dueDay = dueDay;
        this.dueTime = dueTime;
        this.worth = worth;
        this.link = link;
        this.difficulty = difficulty;
        this.bathroomFloor = bathroomFloor;
    }

    public CleanupHour() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDueDay() {
        return dueDay;
    }

    public void setDueDay(String dueDay) {
        this.dueDay = dueDay;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public int getWorth() {
        return worth;
    }

    public void setWorth(int worth) {
        this.worth = worth;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public CleanupHourDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(CleanupHourDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getBathroomFloor() {
        return bathroomFloor;
    }

    public void setBathroomFloor(String bathroomFloor) {
        this.bathroomFloor = bathroomFloor;
    }

    public boolean isBathroomHour() {
        return !bathroomFloor.isEmpty();
    }
}
