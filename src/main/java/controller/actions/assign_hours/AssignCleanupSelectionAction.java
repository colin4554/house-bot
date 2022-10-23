package controller.actions.assign_hours;

import backend.DataRepositoryInterface;
import backend.models.CleanupHour;
import controller.actions.ActionRunner;
import frontend.SlackInterface;
import frontend.views.AssignCleanupHourSelectionView;

import java.util.stream.Collectors;

public class AssignCleanupSelectionAction extends ActionRunner.Action {
    private final String triggerId;

    public AssignCleanupSelectionAction(String triggerId) {
        this.triggerId = triggerId;
    }

    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        var cleanupHours = dataRepository.getCleanupHours();

        var cleanupHourItems = cleanupHours.stream().collect(Collectors.toMap(
                CleanupHour::getName,
                CleanupHour::getName
        ));

        var view = AssignCleanupHourSelectionView.getView(cleanupHourItems);

        slackInterface.openView(triggerId, view);
    }
}
