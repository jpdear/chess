package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import io.javalin.http.Context;
import service.*;

public class CreateGameHandler implements ServerHandler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String handle(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException, UserNotFoundException {
        Gson gson = new Gson();
        String authToken = ctx.header("authorization");
        CreateGameRequest request;

        if (authToken == null || authToken.isBlank()) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        try {
            request = gson.fromJson(ctx.body(), CreateGameRequest.class);
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException("Error: bad request", ex);
        }

        if (request == null || request.gameName() == null || request.gameName().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        CreateGameRequest fullRequest = new CreateGameRequest(authToken, request.gameName());

        CreateGameResult result = gameService.createGame(fullRequest);

        return gson.toJson(result);
    }
}
