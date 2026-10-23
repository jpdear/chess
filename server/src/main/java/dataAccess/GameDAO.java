package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public void clear() throws DataAccessException;
    public GameData createGame(String name) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public Collection<GameData> listGames() throws DataAccessException;
    public GameData updateGame(int gameID) throws DataAccessException;
}
