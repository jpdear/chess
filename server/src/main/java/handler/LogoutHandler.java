package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.*;
import service.request.LogoutRequest;

public class LogoutHandler implements ServerHandler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String handle(Context ctx) throws BadRequestException, DataAccessException, UserNotFoundException {
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isBlank()) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        LogoutRequest request = new LogoutRequest(authToken);

        userService.logout(request);

        return "";
    }
}
