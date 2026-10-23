package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.AppService;

public class ClearHandler implements ServerHandler {
    private final AppService appService;

    public ClearHandler(AppService appService) {
        this.appService = appService;
    }

    @Override
    public String handle(Context ctx) throws DataAccessException {
        appService.clear();

        return "";
    }
}
