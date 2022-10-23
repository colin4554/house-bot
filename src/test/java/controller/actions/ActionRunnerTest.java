package controller.actions;

import backend.DataRepositoryInterface;
import controller.actions.fakes.FakeAction;
import frontend.SlackInterface;
import kotlin.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ActionRunnerTest {

//    @Test
    //Much slower
//    public void test() {
//        ActionRunner.Action mockedAction = mock(ActionRunner.Action.class);
//
//        DataRepositoryInterface dataRepo = new FakeDataRepository();
//
//        ActionRunner runner = new ActionRunner(dataRepo, null);
//        runner.runAction(mockedAction);
//
//        verify(mockedAction).run(dataRepo, null);
//    }

    @Test
    public void test() {
        FakeAction fakeAction = new FakeAction();

        DataRepositoryInterface dataRepo = new FakeDataRepository();

        ActionRunner runner = new ActionRunner(dataRepo, null);
        runner.runAction(fakeAction);

        var call = new Pair<DataRepositoryInterface, SlackInterface>(dataRepo, null);
        var expected = getExpectedCallList(call);
        var actual = fakeAction.getCalls();

        Assertions.assertEquals(expected, actual);
    }

    private List<String> getExpectedCallList(Pair<DataRepositoryInterface, SlackInterface>... pairs) {
        return Arrays.stream(pairs)
                .map(pair -> FakeAction.getCallString(pair.getFirst(), pair.getSecond()))
                .collect(Collectors.toList());
    }

}