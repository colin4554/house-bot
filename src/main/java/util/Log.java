package util;

import frontend.SlackInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Log {
    private static final Logger logger = LoggerFactory.getLogger("cleanup-coordinator-slack");
    private static SlackInterface slackInterface = null;
    private static List<String> logMessagesQueued = new ArrayList<>();

    private Log() {

    }

    public static void e(String message, Throwable t) {
        logger.error(message, t);
        if (slackInterface != null) {
            for (String houseManagerId : Constants.getHouseManagerIds()) {
                slackInterface.sendMessage(houseManagerId, String.format("Error: %s", message));
            }
        } else {
            logMessagesQueued.add(message);
        }
    }

    public static void e(String message) {
        e(message, null);
    }

    public static void setSlackInterface(SlackInterface sInterface) {
        slackInterface = sInterface;
        if (!logMessagesQueued.isEmpty()) {
            logMessagesQueued.forEach(Log::e);
            logMessagesQueued.clear();
        }
    }
}
