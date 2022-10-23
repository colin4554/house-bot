package controller.actions;

import backend.DataRepositoryInterface;
import backend.models.CleanupHour;
import com.google.common.collect.ImmutableList;

import java.util.Set;

public class FakeDataRepository implements DataRepositoryInterface {
    @Override
    public void reloadData() {

    }

    @Override
    public TotalHoursSheetsModel getUsersHourCount(String userId) {
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
    public void reloadKeys() {

    }
}
