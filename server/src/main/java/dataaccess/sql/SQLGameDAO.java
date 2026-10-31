package dataaccess.sql;

import dataaccess.*;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private final Gson serializer = new Gson();

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE game");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }
    }

    @Override
    public GameData createGame(String name) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("""
            INSERT INTO game (gameName, game)
            VALUES (?, ?)
            """, Statement.RETURN_GENERATED_KEYS);

            ChessGame game = new ChessGame();
            String gameJson = serializer.toJson(game);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, gameJson);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                int gameID = resultSet.getInt(1);

                return new GameData(gameID, name, game);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        throw new DataAccessException("Error: failed to create game");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("""
            SELECT whiteUsername, blackUsername, gameName, game
            FROM game where id=?
            """);
            preparedStatement.setString(1, String.valueOf(gameID));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String whiteUsername = resultSet.getString(1);
                String blackUsername = resultSet.getString(2);
                String gameName = resultSet.getString(3);
                ChessGame game = serializer.fromJson(resultSet.getString(4), ChessGame.class);

                return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("""
            SELECT * FROM game
            """);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String whiteUsername = resultSet.getString(2);
                String blackUsername = resultSet.getString(3);
                String gameName = resultSet.getString(4);
                ChessGame game = serializer.fromJson(resultSet.getString(5), ChessGame.class);

                games.add(new GameData(id, whiteUsername, blackUsername, gameName, game));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("""
            UPDATE game
            set 
                whiteUsername = ?,
                blackUsername = ?,
                gameName = ?,
                game = ?
            WHERE id = ?
            """);
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, serializer.toJson(game.game()));
            preparedStatement.setInt(5, game.gameID());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }
    }
}
