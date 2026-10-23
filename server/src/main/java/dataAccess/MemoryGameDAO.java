package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    private int getRandomID() {
        Random rand = new Random();
        int min = 1;
        int max = 9999;

        return rand.nextInt(max - min + 1) + min;
    }

    @Override
    public GameData createGame(String name) throws DataAccessException {
        int checkID = getRandomID();

        while (games.containsKey(checkID)) {
            checkID = getRandomID();
        }

        GameData game = new GameData(checkID, name, new ChessGame());

        games.put(checkID, game);

        return game;
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
