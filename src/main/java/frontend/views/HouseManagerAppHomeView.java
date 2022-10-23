package frontend.views;

import com.slack.api.model.view.View;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;
import static com.slack.api.model.view.Views.view;

public class HouseManagerAppHomeView {
    public static View getView(String userId) {
        return view(view -> view
                .type("home")
                .blocks(asBlocks(
                        section(section -> section.text(markdownText(mt -> mt.text(String.format("Welcome to the Slack Cleanup Coordinator House Mananger! <@%s>.", userId))))),
                        divider(),
                        actions(actions -> actions
                                .elements(asElements(
                                        button(b -> b.text(plainText(pt -> pt.text("Assign Hours Randomly"))).value("assign").actionId("assign_hours_btn")),
                                        button(b -> b.text(plainText(pt -> pt.text("Send Reminders"))).value("remind").actionId("send_reminders_btn")),
                                        button(b -> b.text(plainText(pt -> pt.text("Reload Sheets Data"))).value("reloadDataBtn").actionId("reload_sheets_data_btn")),
                                        button(b -> b.text(plainText(pt -> pt.text("Reload Sheets Keys"))).value("reloadKeysBtn").actionId("reload_sheets_keys_btn"))
                                ))
                        )
                ))
        );
    }
}
