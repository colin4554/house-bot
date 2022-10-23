package controller.actions.fakes;

import backend.DataRepositoryInterface;
import controller.actions.ActionRunner;
import frontend.SlackInterface;

import java.util.*;

public class FakeAction extends ActionRunner.Action {

    List<String> calls = new ArrayList<>();

    public static String getCallString(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        return Objects.toString(dataRepository) + Objects.toString(slackInterface);
    }

    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        calls.add(getCallString(dataRepository, slackInterface));
    }

    public List<String> getCalls() {
        return calls;
    }
}
