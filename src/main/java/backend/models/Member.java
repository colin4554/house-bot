package backend.models;

import java.util.UUID;

public class Member {
    private String slackId;
    private String name;
    private int completedHours;
    private int requiredHours;
    private int semestersActive;
    private String bathroomFloor;

    public Member(String slackId, String name, int completedHours, int requiredHours, int semestersActive, String bathroomFloor) {
        this.slackId = slackId;
        this.name = name;
        this.completedHours = completedHours;
        this.requiredHours = requiredHours;
        this.semestersActive = semestersActive;
        this.bathroomFloor = bathroomFloor;
    }

    public static Member empty() {
        return new Member(UUID.randomUUID().toString(), "null", 0, 0, 0, "");
    }

    public String getSlackId() {
        return slackId;
    }

    public String getName() {
        return name;
    }

    public int getCompletedHours() {
        return completedHours;
    }

    public int getRequiredHours() {
        return requiredHours;
    }

    public int getSemestersActive() {
        return semestersActive;
    }

    public String getBathroomFloor() {
        return bathroomFloor;
    }

    public int getHoursLeft() {
        if (completedHours >= requiredHours) {
            return 0;
        }

        return requiredHours - completedHours;
    }
}
