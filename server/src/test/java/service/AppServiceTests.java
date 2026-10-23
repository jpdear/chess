package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private AppService appService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        appService = new AppService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void clearSuccess() throws Exception {
        userDAO.createUser("alice", "pass123", "alice@mail.com");
        String token = authDAO.createAuth("alice").authToken();
        int gameID = gameDAO.createGame("myGame").gameID();

        appService.clear();

        assertNull(userDAO.getUser("alice"));
        assertNull(authDAO.getAuth(token));
        assertNull(gameDAO.getGame(gameID));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}
