package backend.sheets;


import backend.sheets.response.Result;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import util.Constants;
import util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class HouseBotSheetsApi implements SheetsAPI {
    private static final String APPLICATION_NAME = Constants.getSheetsAppName();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = Constants.getSheetsCredentialsFilePath();

    private final Sheets service;

    public HouseBotSheetsApi() throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    @Override
    public Result<ValueRange> getMembersSheet() {
        return getValueRangeFromDataFile(Constants.getSheetsMemberRange());
    }

    @Override
    public Result<ValueRange> getKeysSheet() {
        return getValueRangeFromKeyFile(Constants.getSheetsKeyRange());
    }

    @Override
    public Result<ValueRange> getWeekSheet(String sheetTitle) {
        return getValueRangeFromDataFile(sheetTitle + "!A2:G");
    }

    @Override
    public Result<ValueRange> getCleanupHours() {
        return getValueRangeFromDataFile(Constants.getSheetsCleanupHourRange());
    }

    @Override
    public Result<List<Sheet>> getSheets() {
        try {
            Spreadsheet sp = service.spreadsheets().get(Constants.getSheetsDataFileId()).execute();

            if (sp == null) {
                return Result.error("Error getting values");
            }

            return Result.ok(sp.getSheets());

        } catch (IOException e) {
            Log.e(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> createNewSheet(String sheetTitle) {
        try {
            var addSheetRequest = new AddSheetRequest();
            var sheetProperties = new SheetProperties();
            sheetProperties.setTitle(sheetTitle);
            addSheetRequest.setProperties(sheetProperties);

            var batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
            batchUpdateSpreadsheetRequest.setRequests(Collections.singletonList(new Request().setAddSheet(addSheetRequest)));
            var batchUpdateRequest = service.spreadsheets().batchUpdate(Constants.getSheetsDataFileId(), batchUpdateSpreadsheetRequest).execute();

            if (batchUpdateRequest == null) {
                return Result.error("Error creating new tab");
            }

            return Result.ok(true);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result<ValueRange> getAssignments(String range) {
        return getSheetValueRange(Constants.getSheetsDataFileId(), range);
    }

    @Override
    public Result<Boolean> updateValueRange(String range, ValueRange body) {
        try {
            UpdateValuesResponse result =
                    service.spreadsheets().values().update(Constants.getSheetsDataFileId(), range, body)
                            .setValueInputOption("USER_ENTERED")
                            .execute();

            if (result == null) {
                return Result.error("Error getting values");
            }

            return Result.ok(true);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    private Result<ValueRange> getValueRangeFromDataFile(String range) {
        return getSheetValueRange(Constants.getSheetsDataFileId(), range);
    }

    private Result<ValueRange> getValueRangeFromKeyFile(String range) {
        return getSheetValueRange(Constants.getSheetsKeyFileId(), range);
    }

    private Result<ValueRange> getSheetValueRange(String sheetFile, String range) {
        try {
            var valueRange = service.spreadsheets().values().get(sheetFile, range).execute();

            if (valueRange == null) {
                return Result.error("Error getting values");
            }

            return Result.ok(valueRange);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
