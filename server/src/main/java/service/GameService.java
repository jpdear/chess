package service;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException, UserNotFoundException {
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        GameData game = gameDAO.createGame(request.gameName());

        return new CreateGameResult(game.gameID());
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException, UserNotFoundException {
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        ArrayList<GameListEntry> allGames = new ArrayList<>();

        for (GameData fullData : gameDAO.listGames()) {
            GameListEntry entry = new GameListEntry(
                    fullData.gameID(),
                    fullData.whiteUsername(),
                    fullData.blackUsername(),
                    fullData.gameName()
            );

            allGames.add(entry);
        }

        return new ListGamesResult(allGames);
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException, UserNotFoundException, BadRequestException, AlreadyTakenException {
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        int gameID = request.gameID();
        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }

        String username = auth.username();
        GameData updatedGame;

        if (request.playerColor() == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }

            updatedGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }

            updatedGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
        }

        gameDAO.updateGame(updatedGame);
    }
}
