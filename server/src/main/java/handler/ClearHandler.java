package handler;

import dataAccess.DataAccessException;
import service.AppService;

public class ClearHandler implements ServerHandler {
    private final AppService appService;

    public ClearHandler(AppService appService) {
        this.appService = appService;
    }

    @Override
    public String handle(String requestData) throws DataAccessException {
        appService.clear();

        return "";
    }
}
