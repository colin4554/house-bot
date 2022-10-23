package frontend.views;

import com.slack.api.model.view.View;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.staticSelect;
import static com.slack.api.model.view.Views.*;

public class SendRemindersSelectionView {

    public static View getView(List<String> weeks, int index) {

        var options = weeks.stream().map(
                option -> option(plainText(option), option)
        ).collect(Collectors.toList());

        if (index < 0 || index > options.size()) index = 0;
        int finalIndex = index;

        return view(view -> view.callbackId("send-reminder-week-selection")
                .type("modal")
                .title(viewTitle(title -> title.type("plain_text").text("Send Reminders").emoji(true)))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
                .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
                .blocks(asBlocks(
                        section(section -> section
                                .blockId("block")
                                .text(markdownText("Select the week to send reminders for"))
                                .accessory(staticSelect(multiSelect -> multiSelect
                                        .initialOption(options.get(finalIndex))
                                        .actionId("action")
                                        .options(options)
                                ))
                        )
                ))
        );
    }
}
