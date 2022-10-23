package controller.actions;

import backend.DataRepositoryInterface;
import frontend.SlackInterface;

public class ReloadSheetsKeysAction extends ActionRunner.Action {
    @Override
    protected void run(DataRepositoryInterface dataRepository, SlackInterface slackInterface) {
        dataRepository.reloadKeys();
    }
}
