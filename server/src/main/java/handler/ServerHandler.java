package handler;

import service.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UserNotFoundException;
import service.BadRequestException;

public interface ServerHandler {
    public String handle(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException, UserNotFoundException;
}
