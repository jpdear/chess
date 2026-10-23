package handler;

import dataAccess.UserNotFoundException;
import service.AlreadyTakenException;
import dataAccess.DataAccessException;

public interface ServerHandler {
    public String handle(String requestData) throws BadRequestException, AlreadyTakenException, DataAccessException, UserNotFoundException;
}
