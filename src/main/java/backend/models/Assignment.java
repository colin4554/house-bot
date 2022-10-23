package backend.models;

public class Assignment {
    private String slackId;
    private String name;
    private CleanupHour cleanupHour;

    private AcceptedStatus status;

    public Assignment(String slackId, String name, CleanupHour cleanupHour) {
        this.slackId = slackId;
        this.name = name;
        this.cleanupHour = cleanupHour;
        this.status = AcceptedStatus.PENDING;
    }

    public String getSlackId() {
        return slackId;
    }

    public void setSlackId(String slackId) {
        this.slackId = slackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CleanupHour getCleanupHour() {
        return cleanupHour;
    }

    public void setCleanupHour(CleanupHour cleanupHour) {
        this.cleanupHour = cleanupHour;
    }

    public AcceptedStatus getStatus() {
        return status;
    }

    public void setStatus(AcceptedStatus status) {
        this.status = status;
    }
}
