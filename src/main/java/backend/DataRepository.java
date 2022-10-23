package backend;

import backend.models.Assignment;
import backend.models.CleanupHour;
import backend.models.Member;
import backend.sheets.HouseBotSheetsApi;
import backend.sheets.HouseBotSheetsDataSource;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import util.Constants;
import util.Log;

import java.util.List;

public class DataRepository implements DataRepositoryInterface {

    private HouseBotSheetsDataSource googleSheetsDataSource;
    private ImmutableMap<String, Member> slackIdToMemberMap;
    private ImmutableList<Member> membersList;
    private ImmutableList<CleanupHour> cleanupHours;

    public DataRepository() {
        try {
            googleSheetsDataSource = new HouseBotSheetsDataSource(new HouseBotSheetsApi());
            reloadKeysFromSheets();
            reloadDataFromSheets();
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private void reloadKeysFromSheets() {
        if (googleSheetsDataSource == null) {
            logGoogleSheetsNullError();
            return;
        }

        var keys = googleSheetsDataSource.getKeys();
        Constants.setSettings(keys);
    }

    private void reloadDataFromSheets() {
        if (googleSheetsDataSource == null) {
            logGoogleSheetsNullError();
            return;
        }

        membersList = googleSheetsDataSource.getMembersList();
        slackIdToMemberMap = membersList.stream().collect(ImmutableMap.toImmutableMap(Member::getSlackId, member -> member));
        cleanupHours = googleSheetsDataSource.getCleanupHours();
    }

    @Override
    public void reloadData() {
        if (googleSheetsDataSource == null) {
            logGoogleSheetsNullError();
            return;
        }

        reloadDataFromSheets();
    }

    @Override
    public Member getMember(String slackId) {
        if (slackIdToMemberMap.containsKey(slackId)) {
            return slackIdToMemberMap.get(slackId);
        } else {
            Log.e(String.format("Error getting cleanup hours for %s", slackId));
        }
        return Member.empty();
    }

    @Override
    public ImmutableList<CleanupHour> getCleanupHours() {
        return cleanupHours;
    }

    @Override
    public ImmutableSet<String> getUserIds() {
        return slackIdToMemberMap.keySet();
    }

    @Override
    public ImmutableList<Member> getMembers() {
        return membersList;
    }

    @Override
    public void reloadKeys() {
        reloadKeysFromSheets();
    }

    @Override
    public void saveNewAssignedHours(ImmutableList<Assignment> assignedHours) {
        googleSheetsDataSource.createNewAssignment(assignedHours);
    }

    @Override
    public void updateAssignments(ImmutableList<Assignment> assignments) {
        googleSheetsDataSource.updateAssignments(assignments);
    }

    @Override
    public List<String> getAvailableWeeks() {
        return googleSheetsDataSource.getWeekSheets();
    }

    @Override
    public ImmutableMap<String, Assignment> getCurrentAssignments() {
        return ImmutableMap.of();
    }

    private void logGoogleSheetsNullError() {
        Log.e("Google Sheets is not initialized - check credentials.json file");
    }
}
