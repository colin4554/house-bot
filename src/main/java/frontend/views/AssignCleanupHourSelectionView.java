package frontend.views;

import com.slack.api.model.view.View;

import java.util.Map;
import java.util.stream.Collectors;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.multiStaticSelect;
import static com.slack.api.model.view.Views.*;

public class AssignCleanupHourSelectionView {

    public static View getView(Map<String, String> cleanupHourItems) {
        var options = cleanupHourItems.entrySet().stream().map(
                cleanupHourItem -> option(plainText(cleanupHourItem.getKey()), cleanupHourItem.getValue())
        ).collect(Collectors.toList());

        return view(view -> view.callbackId("assign-cleanup-hours-selection")
                .type("modal")
                .title(viewTitle(title -> title.type("plain_text").text("Cleanup Hour Selection").emoji(true)))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
                .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
                .blocks(asBlocks(
                        section(section -> section
                                .blockId("block")
                                .text(markdownText("Select Cleanup Hours to be Assigned this week!"))
                                .accessory(multiStaticSelect(multiSelect -> multiSelect
                                        .initialOptions(options)
                                        .actionId("action")
                                        .options(options)
                                ))
                        )
                ))
        );
    }
}
