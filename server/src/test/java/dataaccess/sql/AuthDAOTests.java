package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class AuthDAOTests {
    private  SQLAuthDAO authDAO;

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
    public void clearDB() throws DataAccessException {
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
    }

    @Test
    public void clearSuccess() throws DataAccessException {
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
    public void createAuthSuccess() throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");

        Assertions.assertEquals("username", auth.username());
        Assertions.assertNotNull(auth.authToken());
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
