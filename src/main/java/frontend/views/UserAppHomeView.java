package frontend.views;

import backend.models.Member;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.view.View;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.view.Views.view;

public class UserAppHomeView {
    public static View getView(Member member) {
        var header = section(section -> section.text(markdownText(mt -> mt.text(String.format("Welcome to the Slack Cleanup Coordinator %s!.", member.getName())))));

        SectionBlock completedSection;

        if (member.getName().isEmpty()) {
            completedSection = section(section -> section.text(markdownText(mt -> mt.text("*ERROR YOUR NAME IS NOT IN THE HOURS SHEETS. Please Contact the Housing manager.*"))));
        } else {
            completedSection = section(section -> section.text(markdownText(mt -> mt.text(String.format("Currently you have: %d/%d completed cleanup hours",
                    member.getCompletedHours(),
                    member.getRequiredHours())))));
        }

        return view(view -> view
                .type("home")
                .blocks(asBlocks(
                        header,
                        divider(),
                        completedSection
                ))
        );
    }
}
