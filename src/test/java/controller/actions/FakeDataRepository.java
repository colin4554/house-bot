package controller.actions;

import backend.DataRepositoryInterface;
import backend.models.Assignment;
import backend.models.CleanupHour;
import backend.models.Member;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Set;

public class FakeDataRepository implements DataRepositoryInterface {
    @Override
    public void reloadData() {

    }

    @Override
    public Member getMember(String userId) {
        return null;
    }

    @Override
    public ImmutableList<CleanupHour> getCleanupHours() {
        return null;
    }

    @Override
    public Set<String> getUserIds() {
        return null;
    }

    @Override
    public ImmutableList<Member> getMembers() {
        return null;
    }

    @Override
    public void reloadKeys() {

    }

    @Override
    public void saveNewAssignedHours(ImmutableList<Assignment> assignments) {

    }

    @Override
    public void updateAssignments(ImmutableList<Assignment> assignments) {

    }

    @Override
    public ImmutableMap<String, Assignment> getCurrentAssignments() {
        return null;
    }

    @Override
    public List<String> getAvailableWeeks() {
        return null;
    }
}
