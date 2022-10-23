package backend.sheets;

import backend.models.Assignment;
import backend.models.CleanupHour;
import backend.models.CleanupHourDifficulty;
import backend.models.Member;
import backend.sheets.response.Result;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import org.jetbrains.annotations.NotNull;
import util.Log;
import util.Util;

import java.util.*;
import java.util.stream.Collectors;

public class HouseBotSheetsDataSource implements SheetsDataSource {
    private static final List<Object> ASSIGNMENT_HEADER = ImmutableList.of("Status", "Name", "Hour", "Due Day", "Due Time", "Worth");
    private static final List<Object> ASSIGNMENT_FOOTER = ImmutableList.of("!!!!AUTO ASSIGNED HOURS - ADD OTHER ASSIGNMENTS BELOW (OR ELSE IT WILL BE OVERWRITTEN)!!!!");
    private final SheetsAPI sheetsAPI;
    private String currentWeekTab;

    public HouseBotSheetsDataSource(SheetsAPI sheetsAPI) {
        this.sheetsAPI = sheetsAPI;
        this.currentWeekTab = "";
    }

    @Override
    public ImmutableList<Member> getMembersList() {
        Result<ValueRange> response = sheetsAPI.getMembersSheet();

        if (response.isError()) {
            return ImmutableList.of();
        }

        var values = response.getValue().getValues();

        return values.stream().map(row -> {
            var slackId = getStringFromRowSafely(row, 0);
            var name = getStringFromRowSafely(row, 1);

            var completedHoursStr = getStringFromRowSafely(row, 2);
            var completedHours = Util.parseIntSafely(completedHoursStr);

            var requiredHoursStr = getStringFromRowSafely(row, 3);
            var requiredHours = Util.parseIntSafely(requiredHoursStr);

            var semestersStr = getStringFromRowSafely(row, 4);
            var semesters = Util.parseIntSafely(semestersStr);

            var bathroomHall = getStringFromRowSafely(row, 5);

            return new Member(slackId, name, completedHours, requiredHours, semesters, bathroomHall);
        }).collect(ImmutableList.toImmutableList());
    }

    @Override
    public ImmutableList<CleanupHour> getCleanupHours() {
        Result<ValueRange> response = sheetsAPI.getCleanupHours();

        if (response.isError()) {
            return ImmutableList.of();
        }

        var values = response.getValue().getValues();
        return values.stream().map(row -> {
            var name = getStringFromRowSafely(row, 0);
            var dueDay = getStringFromRowSafely(row, 1);
            var dueTime = getStringFromRowSafely(row, 2);

            var worthStr = getStringFromRowSafely(row, 3);
            var worth = Util.parseIntSafely(worthStr);

            var link = getStringFromRowSafely(row, 4);

            var difficultyStr = getStringFromRowSafely(row, 5);
            var difficulty = CleanupHourDifficulty.MEDIUM;
            if (!difficultyStr.isEmpty()) {
                difficulty = CleanupHourDifficulty.valueOf(difficultyStr.toUpperCase());
            }

            var bathroomFloor = getStringFromRowSafely(row, 6);

            return new CleanupHour(name, dueDay, dueTime, worth, link, difficulty, bathroomFloor);
        }).collect(ImmutableList.toImmutableList());
    }

    @Override
    public void createNewAssignment(ImmutableList<Assignment> assignedHours) {
        var sheets = sheetsAPI.getSheets();
        if (sheets.isError()) {
            Log.e("Couldn't get Sheet Tabs");
            return;
        }

        String nextSheetTitle = getNextSheetTitle(sheets);
        var result = sheetsAPI.createNewSheet(nextSheetTitle);
        if (result.isError()) {
            Log.e("Couldn't create new sheet Tab");
            return;
        }

        currentWeekTab = nextSheetTitle;
        updateCurrentWeekAssignments(assignedHours);
    }

    @Override
    public void updateAssignments(ImmutableList<Assignment> assignments) {
        updateCurrentWeekAssignments(assignments);
    }

    private void updateCurrentWeekAssignments(ImmutableList<Assignment> assignedHours) {
        var values = convertAssignmentsToRows(assignedHours);
        values.add(0, ASSIGNMENT_HEADER);
        values.add(ASSIGNMENT_FOOTER);

        ValueRange body = new ValueRange().setValues(values);

        //TODO ADD SKIPPERS LIST

        var range = String.format("%s!A1:G", currentWeekTab);
        var result = sheetsAPI.updateValueRange(range, body);
        if (result.isError()) {
            Log.e("Error updating values");
        }

    }

    private List<List<Object>> convertAssignmentsToRows(ImmutableList<Assignment> assignedHours) {
        return assignedHours.stream()
                .sorted(Comparator.comparing(a -> a.getCleanupHour().getName()))
                .map(assignment -> List.of(
                        (Object) assignment.getStatus().toString(),
                        assignment.getName(),
                        assignment.getCleanupHour().getName(),
                        assignment.getCleanupHour().getDueDay(),
                        assignment.getCleanupHour().getDueTime(),
                        assignment.getCleanupHour().getWorth()

                ))
                .collect(Collectors.toList());
    }

    private List<Assignment> getCurrentAssignments() {
        return null;
    }

    @NotNull
    private String getNextSheetTitle(Result<List<Sheet>> sheets) {
        var nextSheetNumber = getLastWeeklySheetNumber(sheets).orElse(0) + 1;
        return "Week " + nextSheetNumber;
    }

    @NotNull
    public Optional<Integer> getLastWeeklySheetNumber(Result<List<Sheet>> sheets) {
        return Streams.findLast(sheets.getValue().stream()
                .map(this::getTitleFromSheet)
                .filter(title -> title.startsWith("Week"))
                .map(title -> Util.parseIntSafely(title.split(" ")[1]))
                .sorted());
    }

    @NotNull
    public List<String> getWeekSheets() {
        var sheets = sheetsAPI.getSheets();

        return sheets.getValue().stream().map(this::getTitleFromSheet)
                .filter(title -> title.startsWith("Week"))
                .sorted()
                .collect(Collectors.toList());
    }

    private String getTitleFromSheet(Sheet sheet) {
        SheetProperties properties = (SheetProperties) sheet.get("properties");
        return properties.getOrDefault("title", "").toString();
    }

    @Override
    public ImmutableMap<String, String> getKeys() {
        Result<ValueRange> response = sheetsAPI.getKeysSheet();

        if (response.isError()) {
            return ImmutableMap.of();
        }

        var values = response.getValue().getValues();

        return values.stream().collect(ImmutableMap.toImmutableMap(
                row -> getStringFromRowSafely(row, 0), //Key Name
                row -> getStringFromRowSafely(row, 1) //Key Value
        ));
    }

    private String getStringFromRowSafely(List<Object> row, int position) {
        if (position >= row.size()) {
            return "";
        } else {
            return row.get(position).toString();
        }
    }
}
