package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.UserNotFoundException;
import io.javalin.http.Context;
import model.GameData;
import service.*;

import java.util.ArrayList;

public class ListGamesHandler implements ServerHandler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String handle(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException, UserNotFoundException {
        Gson gson = new Gson();
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isBlank()) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = gameService.listGames(request);

        return gson.toJson(result);
    }
}
