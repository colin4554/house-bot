package controller.actions;

import backend.DataRepositoryInterface;
import backend.models.CleanupHour;
import com.google.common.collect.ImmutableList;
import controller.actions.assign_hours.CleanupHourAssignmentProcessor;
import frontend.SlackInterface;
import frontend.views.AssignCleanupHourMessageBlocks;
import util.Util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.stream.Stream;

public class AcceptCleanupHourAction extends ActionRunner.UserAction {

    private static final int HOURS_REMINDER_BEFORE = 5;

    private final String channelId;
    private final String ts;
    private final CleanupHourAssignmentProcessor assignmentProcessor;

    public AcceptCleanupHourAction(String slackId, String channelId, String ts, CleanupHourAssignmentProcessor assignmentProcessor) {
        super(slackId);
        this.assignmentProcessor = assignmentProcessor;
        this.channelId = channelId;
        this.ts = ts;
    }

    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        var assignment = assignmentProcessor.userAccepted(slackId);
        dataRepository.updateAssignments(assignmentProcessor.getAssignments());
        assignment.ifPresent(value -> {
            slackInterface.updateMessage(channelId, "You have accepted a cleanup hour!", AssignCleanupHourMessageBlocks.getAcceptedBlocks(value), ts);

            var epochSecondReminderTimes = getReminderTimes(value.getCleanupHour());
            for (Integer reminderTimeSeconds : epochSecondReminderTimes) {
                slackInterface.scheduleMessage(channelId, String.format("Reminder to complete your cleanup Hour! It is due today at %s", value.getCleanupHour().getDueTime()), reminderTimeSeconds);
            }
        });
    }

    private ImmutableList<Integer> getReminderTimes(CleanupHour cleanupHour) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("en"));
        var zoneId = ZoneId.of("UTC");
        var now = LocalDate.from(Instant.now().atZone(zoneId));
        var timeString = Util.fixTimeString(cleanupHour.getDueTime());
        var zonedTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("hh:mm a", Locale.US));

        return Stream.of(cleanupHour.getDueDay())
                .flatMap(hoursStr -> Stream.of(hoursStr.split(",")))
                .map(String::trim)
                .map(hourStr -> DayOfWeek.from(formatter.parse(hourStr)))
                .map(dayOfWeek -> now.with(TemporalAdjusters.next(dayOfWeek)))
                .map(ld -> ld.atTime(zonedTime.getHour(), zonedTime.getMinute()))
                .map(ld -> ld.toInstant(ZoneOffset.UTC))
                .map(instant -> instant.plus(5, ChronoUnit.HOURS)) //Adjust to EST
                .map(instant -> instant.minus(HOURS_REMINDER_BEFORE, ChronoUnit.HOURS))
                .map(time -> (int) time.getEpochSecond())
                .collect(ImmutableList.toImmutableList());
    }
}
