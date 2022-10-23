package controller.actions;

import backend.DataRepositoryInterface;
import frontend.SlackInterface;

public class NoBallsAction extends ActionRunner.UserAction {

    public NoBallsAction(String userId) {
        super(userId);
    }

    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        slackInterface.sendMessage(slackId, "All Shaft!");
    }
}
