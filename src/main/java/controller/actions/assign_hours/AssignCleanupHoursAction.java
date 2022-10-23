package controller.actions.assign_hours;

import backend.DataRepositoryInterface;
import backend.models.Assignment;
import com.google.common.collect.ImmutableList;
import controller.actions.ActionRunner;
import frontend.SlackInterface;
import frontend.views.AssignCleanupHourMessageBlocks;

import java.util.Set;

public class AssignCleanupHoursAction extends ActionRunner.Action {

    private final Set<String> selectedHoursNames;
    private final CleanupHourAssignmentProcessor cleanupHourAssignmentProcessor;

    public AssignCleanupHoursAction(CleanupHourAssignmentProcessor cleanupHourAssignmentProcessor, Set<String> selectedHoursNames) {
        this.cleanupHourAssignmentProcessor = cleanupHourAssignmentProcessor;
        this.selectedHoursNames = selectedHoursNames;
    }

    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        var hours = dataRepository.getCleanupHours().stream().filter(
                        cleanupHour -> selectedHoursNames.contains(cleanupHour.getName()))
                .collect(ImmutableList.toImmutableList());

        var assignments = cleanupHourAssignmentProcessor.createAssignments(hours);
        dataRepository.saveNewAssignedHours(assignments);

//        for (Assignment assignment : assignments) {
//            sendAssignment(slackInterface, assignment);
//        }
    }

    private void sendAssignment(SlackInterface slackInterface, Assignment assignment) {
        if ("null".equals(assignment.getName())) {
            return;
        }

        var blocks = AssignCleanupHourMessageBlocks.getBlocks(assignment);
        slackInterface.sendMessage("US4MRGT09", "You have been assigned a cleanup hour for this week", blocks);
    }
}
