package service;

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
}
