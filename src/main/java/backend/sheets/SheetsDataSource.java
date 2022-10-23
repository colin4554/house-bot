package backend.sheets;

import backend.models.Assignment;
import backend.models.CleanupHour;
import backend.models.Member;
import com.google.common.collect.ImmutableList;

import java.util.Map;

public interface SheetsDataSource {

    Map<String, String> getKeys();

    ImmutableList<Member> getMembersList();

    ImmutableList<CleanupHour> getCleanupHours();

    void createNewAssignment(ImmutableList<Assignment> assignedHours);

    void updateAssignments(ImmutableList<Assignment> assignments);
}
