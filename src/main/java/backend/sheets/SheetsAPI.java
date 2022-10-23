package backend.sheets;

import backend.sheets.response.Result;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public interface SheetsAPI {

    Result<ValueRange> getMembersSheet();

    Result<ValueRange> getKeysSheet();

    Result<ValueRange> getCleanupHours();

    Result<List<Sheet>> getSheets();

    Result<Boolean> createNewSheet(String sheetTitle);

    Result<ValueRange> getAssignments(String weekTab);

    Result<Boolean> updateValueRange(String range, ValueRange body);
}
