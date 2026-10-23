package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import dataAccess.UserNotFoundException;
import service.*;

public class LogoutHandler implements ServerHandler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String handle(String requestData) throws BadRequestException, DataAccessException, UserNotFoundException {
        if (requestData.isBlank()) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        Gson gson = new Gson();
        LogoutRequest request;

        try {
            request = gson.fromJson(requestData, LogoutRequest.class);
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException("Error: bad request", ex);
        }

        if (request == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.authToken() == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.authToken().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        userService.logout(request);

        return "";
    }
}
