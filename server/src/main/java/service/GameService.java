package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;

public class GameService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
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
}
