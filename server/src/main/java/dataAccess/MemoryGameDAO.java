package dataAccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public GameData createGame() throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData updateGame(int gameID) throws DataAccessException {
        return null;
    }
}
