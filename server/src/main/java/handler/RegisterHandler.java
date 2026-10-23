package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import service.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.BadRequestException;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import service.UserService;

public class RegisterHandler implements ServerHandler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String handle(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException {
        Gson gson = new Gson();
        RegisterRequest request;

        try {
            request = gson.fromJson(ctx.body(), RegisterRequest.class);
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException("Error: bad request", ex);
        }

        if (request == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.username().isBlank() || request.password().isBlank() || request.email().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        RegisterResult result = userService.register(request);

        return gson.toJson(result);
    }
}
