package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SQLDataAccessTests {
    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeAll
    public static void initDB() throws DataAccessException {
        Properties testProps = new Properties();
        testProps.setProperty("db.host", "localhost");
        testProps.setProperty("db.port", "3306");
        testProps.setProperty("db.name", "chessTest");
        testProps.setProperty("db.user", "test");
        testProps.setProperty("db.password", "test");

        DatabaseManager.switchDatabases(testProps);
    }

    @BeforeEach
    public void resetDB() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement clearAuth = conn.prepareStatement("TRUNCATE TABLE auth");
            PreparedStatement clearUser = conn.prepareStatement("TRUNCATE TABLE user");
            PreparedStatement clearGame = conn.prepareStatement("TRUNCATE TABLE game");

            clearAuth.executeUpdate();
            clearUser.executeUpdate();
            clearGame.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
    }

    @Test
    public void clearAuthSuccess() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement init = conn.prepareStatement("""
            INSERT INTO auth (authToken, username)
            VALUES
                ("123456789", "user1"),
                ("987654321", "user2"),
                ("192837465", "user3")
            """);
            init.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        Assertions.assertEquals(3, countRows("auth"));
        authDAO.clear();
        Assertions.assertEquals(0, countRows("auth"));
    }

    @Test
    public void clearUserSuccess() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement init = conn.prepareStatement("""
            INSERT INTO user (username, password, email)
            VALUES
                ("user1", "pass1", "email1@email.com"),
                ("user2", "pass2", "email2@email.com"),
                ("user3", "pass3", "email3@email.com")
            """);
            init.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        Assertions.assertEquals(3, countRows("user"));
        userDAO.clear();
        Assertions.assertEquals(0, countRows("user"));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement init = conn.prepareStatement("""
            INSERT INTO auth (authToken, username)
            VALUES ("123456789", "user")
            """);

            init.executeUpdate();

            AuthData auth = authDAO.getAuth("123456789");

            Assertions.assertEquals("user", auth.username());
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }
    }

    @Test
    public void getAuthFail() throws DataAccessException {
        AuthData auth = authDAO.getAuth("notrealtoken");

        Assertions.assertNull(auth);
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement init = conn.prepareStatement("""
            INSERT INTO user (username, password, email)
            VALUES ("user1", "pass1", "email1@email.com")
            """);

            init.executeUpdate();

            UserData user = userDAO.getUser("user1");

            Assertions.assertNotNull(user);
            Assertions.assertEquals("pass1", user.password());
            Assertions.assertEquals("email1@email.com", user.email());
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }
    }

    @Test
    public void getUserFail() throws DataAccessException {
        UserData user = userDAO.getUser("norealuser");

        Assertions.assertNull(user);
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");

        Assertions.assertEquals("username", auth.username());
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        String hash = BCrypt.hashpw("pass1", BCrypt.gensalt());
        UserData user = userDAO.createUser("user1", hash, "email1@email.com");

        Assertions.assertNotNull(user);
        Assertions.assertEquals("user1", user.username());
        Assertions.assertEquals(hash, user.password());
        Assertions.assertEquals("email1@email.com", user.email());
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement init = conn.prepareStatement("""
            INSERT INTO auth (authToken, username)
            VALUES ("123456789", "user")
            """);
            init.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }

        Assertions.assertEquals(1, countRows("auth"));

        authDAO.deleteAuth("123456789");

        Assertions.assertEquals(0, countRows("auth"));
    }

    private int countRows(String table) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM " + table);
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()), ex);
        }
    }
}
