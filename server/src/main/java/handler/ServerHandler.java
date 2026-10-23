package handler;

import dataAccess.UserNotFoundException;
import service.AlreadyTakenException;
import dataAccess.DataAccessException;
import io.javalin.http.Context;

public interface ServerHandler {
    public String handle(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException, UserNotFoundException;
}
