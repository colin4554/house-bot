package controller.actions;

import backend.DataRepositoryInterface;
import frontend.SlackInterface;

public class ActionRunner {
    protected DataRepositoryInterface dataRepository;
    protected SlackInterface slackInterface;

    public ActionRunner(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        this.dataRepository = dataRepository;
        this.slackInterface = slackInterface;
    }

    public void runAction(Action action) {
        action.run(dataRepository, slackInterface);
    }

    public abstract static class UserAction extends Action {

        protected final String slackId;

        public UserAction(String slackId) {
            this.slackId = slackId;
        }
    }

    public abstract static class Action {

        public Action() {
        }

        protected abstract void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface);
    }

}
