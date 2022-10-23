package frontend;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.event.MessageChangedEvent;
import com.slack.api.model.view.ViewState;
import controller.HouseBotController;
import util.Constants;
import util.Log;

import java.util.stream.Collectors;

public class HouseBot {


    private final App app;
    private final SocketModeApp socketModeApp;
    private final HouseBotController controller;

    public HouseBot() throws Exception {
        var appConfig = AppConfig.builder().singleTeamBotToken(Constants.getBotToken()).build();
        app = new App(appConfig);
        socketModeApp = new SocketModeApp(Constants.getAppToken(), app);

        setupListeners();

        var slackInterface = new SlackInterface(Constants.getBotToken());
        Log.setSlackInterface(slackInterface); //Must be before the next one
        controller = new HouseBotController(slackInterface);
    }
    public void start() throws Exception {
        socketModeApp.start();
    }

    private void setupListeners() {
        app.command("/hello", (req, ctx) -> {
            return ctx.ack(":wave: Hello!");
        });

        app.message("no balls", (payload, ctx) -> {
            controller.handleNoBallsEvent(payload);
            return ctx.ack();
        });

        app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
            var userId = payload.getEvent().getUser();
            controller.handleAppHomeOpenedEvent(userId);
            return ctx.ack();
        });

        app.blockAction("cleanup-assignment-selection-action", (req, ctx) -> ctx.ack());


        app.blockAction("assign_hours_btn", (req, ctx) -> {
            controller.handleAssignmentSelection(ctx.getTriggerId());

            return ctx.ack();
        });

        app.blockAction("send_reminders_btn", (req, ctx) -> {
            controller.handleSendRemindersSelection(ctx.getTriggerId());

            return ctx.ack();
        });

        app.viewSubmission("send-reminder-week-selection", (req, ctx) -> {
            var week = req.getPayload().getView().getState().getValues().get("block").get("action").getSelectedOption().getValue();

            controller.handleSendRemindersEvent(week);

            return ctx.ack();
        });


        app.viewSubmission("assign-cleanup-hours-selection", (req, ctx) -> {
            var selectedHoursNames = req.getPayload().getView().getState().getValues().get("block").get("action").getSelectedOptions()
                    .stream().map(ViewState.SelectedOption::getValue).collect(Collectors.toUnmodifiableSet());

            controller.handleAssignHoursEvent(selectedHoursNames);

            return ctx.ack();
        });

        app.blockAction("reload_sheets_data_btn", (req, ctx) -> {
            controller.handleReloadSheetsDataEvent();
            return ctx.ack();
        });

        app.blockAction("reload_sheets_keys_btn", (req, ctx) -> {
            controller.handleReloadSheetsKeyEvent();
            return ctx.ack();
        });

        app.blockAction("accept_hour_btn", (req, ctx) -> {
            var userId = ctx.getRequestUserId();
            var channelId = req.getPayload().getChannel().getId();
            var ts = req.getPayload().getMessage().getTs();
            controller.handleAcceptHourEvent(userId, channelId, ts);
            return ctx.ack();
        });

        app.event(MessageChangedEvent.class, (payload, ctx) -> ctx.ack());

        app.blockAction("skip_hour_btn", (req, ctx) -> {
            var userId = ctx.getRequestUserId();
            var assignmentId = req.getPayload().getActions().get(1).getValue();
            controller.handleSkipHourEvent(userId, assignmentId);
            return ctx.ack();
        });
    }
}
