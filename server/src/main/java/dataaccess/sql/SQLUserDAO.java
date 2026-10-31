package dataaccess.sql;

import dataaccess.*;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE user");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }
    }

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("""
            INSERT INTO user (username, password, email)
            VALUES (?, ?, ?)
            """);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        return new UserData(username, password, email);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("""
            SELECT username, password, email FROM user where username=?
            """);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");

                return new UserData(username, password, email);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        return null;
    }
}
