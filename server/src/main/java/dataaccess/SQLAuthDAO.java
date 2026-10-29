package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT authToken, username FROM auth WHERE authToken=?"
            );
            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String authToken = resultSet.getString("authToken");
                String username = resultSet.getString("username");

                return new AuthData(authToken, username);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        return null;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = AuthToken.generateToken();

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO auth (authToken, username) VALUES (?, ?)"
            );
            preparedStatement.setString(1, authToken);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM auth WHERE authToken=?"
            );
            preparedStatement.setString(1, token);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }
    }
}
