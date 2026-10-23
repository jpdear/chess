package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import dataAccess.UserNotFoundException;
import service.*;

public class LoginHandler implements ServerHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String handle(String requestData) throws BadRequestException, DataAccessException, UserNotFoundException {
        Gson gson = new Gson();
        LoginRequest request;

        try {
            request = gson.fromJson(requestData, LoginRequest.class);
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException("Error: bad request", ex);
        }

        if (request == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.username().isBlank() || request.password().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        LoginResult result = userService.login(request);

        return gson.toJson(result);
    }
}
