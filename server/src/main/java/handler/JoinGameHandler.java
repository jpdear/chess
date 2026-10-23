package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import io.javalin.http.Context;
import service.UserNotFoundException;
import service.BadRequestException;
import service.AlreadyTakenException;
import service.GameService;
import service.JoinGameRequest;

public class JoinGameHandler implements ServerHandler {
    private record JoinGameBody(String playerColor, Integer gameID) {
    }

    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String handle(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException, UserNotFoundException {
        Gson gson = new Gson();
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isBlank()) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        JoinGameBody request;

        try {
            request = gson.fromJson(ctx.body(), JoinGameBody.class);
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException("Error: bad request", ex);
        }

        if (request == null || request.gameID() == null || request.playerColor() == null) {
            throw new BadRequestException("Error: bad request");
        }

        ChessGame.TeamColor color;

        switch (request.playerColor().toUpperCase()) {
            case "WHITE" -> color = ChessGame.TeamColor.WHITE;
            case "BLACK" -> color = ChessGame.TeamColor.BLACK;
            default -> throw new BadRequestException("Error: bad request");
        }

        JoinGameRequest fullRequest = new JoinGameRequest(authToken, color, request.gameID());
        gameService.joinGame(fullRequest);

        return "";
    }
}
