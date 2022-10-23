package backend;

import backend.models.Assignment;
import backend.models.CleanupHour;
import backend.models.Member;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Set;

public interface DataRepositoryInterface {
    void reloadData();

    Member getMember(String userId);

    ImmutableList<CleanupHour> getCleanupHours();

    Set<String> getUserIds();

    ImmutableList<Member> getMembers();

    void reloadKeys();

    void saveNewAssignedHours(ImmutableList<Assignment> assignments);

    void updateAssignments(ImmutableList<Assignment> assignments);

    ImmutableMap<String, Assignment> getCurrentAssignments();

    List<String> getAvailableWeeks();
}
