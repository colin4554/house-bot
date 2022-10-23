package controller.actions.assign_hours;

import backend.models.AcceptedStatus;
import backend.models.Assignment;
import backend.models.CleanupHour;
import backend.models.Member;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CleanupHourAssignmentProcessor {

    private Map<String, Assignment> currentAssignments;

    private ImmutableList<CleanupHour> selectedCleanupHours = ImmutableList.of();
    private ImmutableList<Member> members;

    public CleanupHourAssignmentProcessor(ImmutableMap<String, Assignment> currentAssignments, ImmutableList<Member> members) {
        this.members = members;
        this.currentAssignments = currentAssignments;
    }

    public ImmutableList<Assignment> createAssignments(ImmutableList<CleanupHour> selectedHours) {
        currentAssignments = ImmutableMap.of();

        selectedCleanupHours = selectedHours.stream()
                .sorted(Comparator.comparing(CleanupHour::isBathroomHour).reversed().thenComparing(c -> c.getDifficulty().getDifficultyValue()))
                .collect(ImmutableList.toImmutableList());

        var availableMembers = getAvailableMembers();
        var sortedMembers = availableMembers.sorted(Comparator.comparingInt(Member::getHoursLeft).reversed()
                .thenComparing(Comparator.comparingInt(Member::getSemestersActive).reversed())).collect(ImmutableList.toImmutableList());

        var selectedMembers = new HashSet<Member>();

        currentAssignments = selectedCleanupHours.stream()
                .map(hour -> {
                    Supplier<Stream<Member>> unChoosenMembers = () -> sortedMembers.stream().filter(Predicate.not(selectedMembers::contains));

                    if (hour.isBathroomHour()) {
                        var floorResident = unChoosenMembers.get().filter(member -> hour.getBathroomFloor().equals(member.getBathroomFloor())).findFirst();

                        if (floorResident.isPresent()) {
                            selectedMembers.add(floorResident.get());
                            return createAssignment(hour, floorResident.get());
                        }
                    }

                    var member = unChoosenMembers.get().findFirst();
                    if (member.isPresent()) {
                        selectedMembers.add(member.get());
                        return createAssignment(hour, member.get());
                    } else {
                        return createAssignment(hour, Member.empty());
                    }
                })
//                .filter(assignment -> !assignment.getSlackId().isEmpty())
                .collect(Collectors.toMap(
                        Assignment::getSlackId,
                        assignment -> assignment));

        return getAssignments();
    }

    private Assignment createAssignment(CleanupHour cleanupHour, Member member) {
        return new Assignment(member.getSlackId(), member.getName(), cleanupHour);
    }

    private Stream<Member> getAvailableMembers() {
        return members.stream()
                .filter(member -> member.getCompletedHours() < member.getRequiredHours())
                .filter(member -> !currentAssignments.containsKey(member.getSlackId()));

    }

    public ImmutableList<Assignment> getAssignments() {
        return ImmutableList.copyOf(currentAssignments.values());
    }

    public Optional<Assignment> userAccepted(String slackId) {
        var assignment = currentAssignments.getOrDefault(slackId, null);
        if (assignment != null) {
            assignment.setStatus(AcceptedStatus.ACCEPTED);
        }

        return Optional.ofNullable(assignment);
    }

//    public Assignment userSkippedGetNext(String userId) {
//        return null;
//    }

    public void setMembers(ImmutableList<Member> members) {
        this.members = members;
    }
}
