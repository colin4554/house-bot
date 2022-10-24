package controller.actions.remind;

import backend.DataRepositoryInterface;
import backend.models.Assignment;
import backend.models.CleanupHour;
import com.google.common.collect.ImmutableList;
import com.slack.api.model.view.ViewState;
import controller.actions.ActionRunner;
import controller.actions.assign_hours.CleanupHourAssignmentProcessor;
import frontend.SlackInterface;
import frontend.views.AssignCleanupHourMessageBlocks;
import util.Util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class SendRemindersAction extends ActionRunner.Action {

    private final CleanupHourAssignmentProcessor cleanupHourAssignmentProcessor;
    private final ViewState.SelectedOption selectedOption;
    private String title = "this week";

    public SendRemindersAction(CleanupHourAssignmentProcessor cleanupHourAssignmentProcessor, ViewState.SelectedOption selectedOption) {
        this.cleanupHourAssignmentProcessor = cleanupHourAssignmentProcessor;
        this.selectedOption = selectedOption;
    }

    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {

        String id = selectedOption.getValue();
        String title = selectedOption.getText().getText();
        this.title = title;

//        int weekNum = Util.parseIntSafely(week.split(" ")[1]);
        var assignments = dataRepository.getAssignmentsForWeek(title);

        for (Assignment assignment : assignments) {
//            System.out.println(assignment);
            sendAssignment(slackInterface, assignment);
            if (!"null".equals(assignment.getName()) && !assignment.getCleanupHour().getDueTime().equals("")) {
                var epochSecondReminderTimes = getReminderTimes(assignment.getCleanupHour());
                for (Integer reminderTimeSeconds : epochSecondReminderTimes) {
                    System.out.println("schedule message");
                    System.out.println(reminderTimeSeconds);
                    slackInterface.scheduleMessage("D03BY53RSFM", String.format("Reminder to complete your cleanup hour! It is due today at %s", assignment.getCleanupHour().getDueTime()), reminderTimeSeconds);
                }
            }
        }
    }

    private void sendAssignment(SlackInterface slackInterface, Assignment assignment) {
        if ("null".equals(assignment.getName())) {
            return;
        }

        var blocks = AssignCleanupHourMessageBlocks.getBlocks(assignment, title);
        slackInterface.sendMessage(assignment.getSlackId(), "You have been assigned a cleanup hour for " + title, blocks);
    }


    private static final int HOURS_REMINDER_BEFORE = 6;

    // See Accept Cleanup Hour Action
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
                .map(dayOfWeek -> {
                    // If the house manager is late to assign cleanup hours until Monday, the reminder is set correctly
                    if (dayOfWeek == DayOfWeek.MONDAY) return now.with(TemporalAdjusters.nextOrSame(dayOfWeek));
                    else return now.with(TemporalAdjusters.next(dayOfWeek));
                })
                .map(ld -> ld.atTime(zonedTime.getHour(), zonedTime.getMinute()))
                .map(ld -> ld.toInstant(ZoneOffset.UTC))
                .map(instant -> instant.plus(5, ChronoUnit.HOURS)) //Adjust to EST
                .map(instant -> instant.minus(HOURS_REMINDER_BEFORE, ChronoUnit.HOURS))
                .map(time -> (int) time.getEpochSecond())
                .collect(ImmutableList.toImmutableList());
    }
}
