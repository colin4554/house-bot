package controller.actions.remind;

import backend.DataRepositoryInterface;
import backend.models.Assignment;
import controller.actions.ActionRunner;
import controller.actions.assign_hours.CleanupHourAssignmentProcessor;
import frontend.SlackInterface;
import frontend.views.AssignCleanupHourMessageBlocks;
import util.Util;

import java.util.Set;

public class SendRemindersAction extends ActionRunner.Action {

    private final CleanupHourAssignmentProcessor cleanupHourAssignmentProcessor;
    private final String week;

    public SendRemindersAction(CleanupHourAssignmentProcessor cleanupHourAssignmentProcessor, String week) {
        this.cleanupHourAssignmentProcessor = cleanupHourAssignmentProcessor;
        this.week = week;
    }

    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {

        int weekNum = Util.parseIntSafely(week.split(" ")[1]);
        var assignments = dataRepository.getCurrentAssignments().values();

        for (Assignment assignment : assignments) {
            sendAssignment(slackInterface, assignment);
        }
    }

    private void sendAssignment(SlackInterface slackInterface, Assignment assignment) {
        if ("null".equals(assignment.getName())) {
            return;
        }

        var blocks = AssignCleanupHourMessageBlocks.getBlocks(assignment);
        slackInterface.sendMessage("US4MRGT09", "You have been assigned a cleanup hour for this week", blocks);
    }
}
