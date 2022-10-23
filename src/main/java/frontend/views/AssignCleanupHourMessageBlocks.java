package frontend.views;

import backend.models.Assignment;
import backend.models.CleanupHour;
import com.slack.api.model.block.LayoutBlock;

import java.util.List;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;

public class AssignCleanupHourMessageBlocks {
    public static List<LayoutBlock> getBlocks(Assignment assignment) {
        return asBlocks(
                section(section -> section.text(markdownText(mt -> mt.text(String.format("*Howdy <@%s>! You have been assigned a cleanup hour for this week:*", assignment.getSlackId()))))),
                divider(),
                section(section -> section.text(markdownText(mt -> mt.text(getAssignmentMessageText(assignment.getCleanupHour()))))),
                actions(actions -> actions
                        .elements(asElements(
                                button(b -> b.text(plainText(pt -> pt.text("Accept Hour"))).value("d").actionId("accept_hour_btn")),
                                button(b -> b.text(plainText(pt -> pt.text("Skip Hour"))).value("d").actionId("skip_hour_btn"))
                        ))
                ));
    }

    private static String getAssignmentMessageText(CleanupHour cleanupHour) {
        return String.format("*Assignment*: %s\n", cleanupHour.getName()) +
                String.format("Due Date: %s at %s\n", cleanupHour.getDueDay(), cleanupHour.getDueTime()) +
                String.format("Worth: %d Hour\n", cleanupHour.getWorth()) +
                String.format("Link: %s\n", cleanupHour.getLink());
    }

    public static List<LayoutBlock> getAcceptedBlocks(Assignment assignment) {
        return asBlocks(
                section(section -> section.text(markdownText(mt -> mt.text(String.format("*Howdy <@%s>! You have ACCEPTED the cleanup hour:*", assignment.getSlackId()))))),
                divider(),
                section(section -> section.text(markdownText(mt -> mt.text(getAssignmentMessageText(assignment.getCleanupHour()))))));
    }
}
