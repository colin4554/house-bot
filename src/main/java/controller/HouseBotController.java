package controller;

import backend.DataRepository;
import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.model.event.MessageEvent;
import com.slack.api.model.view.ViewState;
import controller.actions.*;
import controller.actions.assign_hours.AssignCleanupHoursAction;
import controller.actions.remind.SendRemindersAction;
import controller.actions.assign_hours.AssignCleanupSelectionAction;
import controller.actions.assign_hours.CleanupHourAssignmentProcessor;
import controller.actions.remind.SendRemindersSelectionAction;
import frontend.SlackInterface;

import java.util.Set;

public class HouseBotController {

    private final ActionRunner actionRunner;
    private final CleanupHourAssignmentProcessor cleanupHourAssignmentProcessor;

    public HouseBotController(SlackInterface slackInterface) {
        var dataRepo = new DataRepository();
        actionRunner = new ActionRunner(dataRepo, slackInterface);
        cleanupHourAssignmentProcessor = new CleanupHourAssignmentProcessor(dataRepo.getCurrentAssignments(), dataRepo.getMembers());
    }

    public void handleNoBallsEvent(EventsApiPayload<MessageEvent> payload) {
        var userId = payload.getEvent().getUser();
        actionRunner.runAction(new NoBallsAction(userId));
    }

    public void handleAppHomeOpenedEvent(String userId) {
        actionRunner.runAction(new AppHomeOpenedAction(userId));
    }

    public void handleAssignHoursEvent(Set<String> selectedHoursNames) {
        actionRunner.runAction(new AssignCleanupHoursAction(cleanupHourAssignmentProcessor, selectedHoursNames));
    }

    public void handleSendRemindersEvent(ViewState.SelectedOption week) {
//        System.out.println(week);
        actionRunner.runAction(new SendRemindersAction(cleanupHourAssignmentProcessor, week));
    }
    public void handleSendRemindersSelection(String triggerId) {
        actionRunner.runAction(new SendRemindersSelectionAction(triggerId));
    }

    public void handleReloadSheetsDataEvent() {
        actionRunner.runAction(new ReloadSheetsDataAction(cleanupHourAssignmentProcessor));
    }

    public void handleAcceptHourEvent(String userId, String channelId, String ts) {
        actionRunner.runAction(new AcceptCleanupHourAction(userId, channelId, ts, cleanupHourAssignmentProcessor));
    }

    public void handleSkipHourEvent(String userId, String assignmentId) {

    }

    public void handleReloadSheetsKeyEvent() {
        actionRunner.runAction(new ReloadSheetsKeysAction());
    }

    public void handleAssignmentSelection(String triggerId) {
        actionRunner.runAction(new AssignCleanupSelectionAction(triggerId));
    }
}
