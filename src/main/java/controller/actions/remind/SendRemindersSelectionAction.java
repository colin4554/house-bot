package controller.actions.remind;

import backend.DataRepositoryInterface;
import backend.models.CleanupHour;
import controller.actions.ActionRunner;
import frontend.SlackInterface;
import frontend.views.AssignCleanupHourSelectionView;
import frontend.views.SendRemindersSelectionView;

import java.util.stream.Collectors;

public class SendRemindersSelectionAction extends ActionRunner.Action {
    private final String triggerId;

    public SendRemindersSelectionAction(String triggerId) {
        this.triggerId = triggerId;
    }

    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        var weeks = dataRepository.getAvailableWeeks();

        var view = SendRemindersSelectionView.getView(weeks, weeks.size() - 1);

        slackInterface.openView(triggerId, view);
    }
}
