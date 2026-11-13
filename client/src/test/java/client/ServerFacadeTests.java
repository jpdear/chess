package client;

import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        String serverUrl = String.format("http://localhost:%d", port);
        facade = new ServerFacade(serverUrl);

        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void resetDB() throws IOException, InterruptedException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerSuccess() throws IOException, InterruptedException {
        Map<String, Object> result = facade.register(
                "username",
                "password",
                "email"
        );

        Assertions.assertTrue((Boolean) result.get("success"));
        Assertions.assertEquals("username", result.get("username"));
        Assertions.assertNotNull(result.get("authToken"));
    }

    @Test
    public void registerMissingFieldReturnsError() throws IOException, InterruptedException {
        Map<String, Object> result = facade.register(
                "username",
                "password",
                ""
        );

        Assertions.assertFalse((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("message"));
    }

    @Test
    public void registerTakenUsernameReturnsError() throws IOException, InterruptedException {
        facade.register(
                "username",
                "password",
                "email"
        );

        Map<String, Object> result = facade.register(
                "username",
                "password2",
                "email2"
        );

        Assertions.assertFalse((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("message"));
    }

    @Test
    public void loginSuccess() throws IOException, InterruptedException {
        Assertions.assertTrue(registerUser());

        Map<String, Object> loginResult = facade.login("username", "password");

        Assertions.assertTrue((Boolean) loginResult.get("success"));
        Assertions.assertNotNull(loginResult.get("authToken"));
    }

    @Test
    public void loginWrongPasswordReturnsError() throws IOException, InterruptedException {
        Assertions.assertTrue(registerUser());

        Map<String, Object> result = facade.login("username", "wrong");

        Assertions.assertFalse((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("message"));
    }

    @Test
    public void loginWrongUsernameReturnsError() throws IOException, InterruptedException {
        Assertions.assertTrue(registerUser());

        Map<String, Object> result = facade.login("wrong", "password");

        Assertions.assertFalse((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("message"));
    }

    @Test
    public void loginMissingArgsReturnsError() throws IOException, InterruptedException {
        Assertions.assertTrue(registerUser());

        Map<String, Object> result = facade.login("", "password");

        Assertions.assertFalse((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("message"));
    }

    @Test
    public void logoutSuccess() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        Map<String, Object> logoutResult = facade.logout(authToken);

        Assertions.assertTrue((Boolean) logoutResult.get("success"));
    }

    @Test
    public void logoutUnauthorizedReturnsError() throws IOException, InterruptedException {
        Assertions.assertNotNull(registerAndLogin("username", "password"));

        Map<String, Object> logoutResult = facade.logout("");

        Assertions.assertFalse((Boolean) logoutResult.get("success"));
        Assertions.assertNotNull(logoutResult.get("message"));
    }

    @Test
    public void createGameSuccess() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        Map<String, Object> result = facade.createGame(authToken, "gameName");

        Assertions.assertTrue((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("gameID"));
    }

    @Test
    public void createGameMissingArgsReturnsError() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        Map<String, Object> result = facade.createGame(authToken, "");

        Assertions.assertFalse((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("message"));
    }

    @Test
    public void createGameUnauthorizedReturnsError() throws IOException, InterruptedException {
        Assertions.assertNotNull(registerAndLogin("username", "password"));

        Map<String, Object> result = facade.createGame("", "gameName");

        Assertions.assertFalse((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("message"));
    }

    @Test
    public void listGamesSuccess() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        facade.createGame(authToken, "gameName");

        Map<String, Object> result = facade.listGames(authToken);

        Assertions.assertTrue((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("games"));
    }

    @Test
    public void listGamesUnauthorizedReturnsError() throws IOException, InterruptedException {
        Assertions.assertNotNull(registerAndLogin("username", "password"));

        Map<String, Object> result = facade.listGames("");

        Assertions.assertFalse((Boolean) result.get("success"));
        Assertions.assertNotNull(result.get("message"));
    }

    @Test
    public void joinEmptyGameWhiteSuccess() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        Map<String, Object> createGameResult = facade.createGame(authToken, "gameName");
        int gameID = ((Number) createGameResult.get("gameID")).intValue();

        Assertions.assertTrue((Boolean) createGameResult.get("success"));

        Map<String, Object> joinGameResult = facade.joinGame(authToken, gameID, "white");

        Assertions.assertTrue((Boolean) joinGameResult.get("success"));
    }

    @Test
    public void joinEmptyGameBlackSuccess() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        Map<String, Object> createGameResult = facade.createGame(authToken, "gameName");
        int gameID = ((Number) createGameResult.get("gameID")).intValue();

        Assertions.assertTrue((Boolean) createGameResult.get("success"));

        Map<String, Object> joinGameResult = facade.joinGame(authToken, gameID, "black");

        Assertions.assertTrue((Boolean) joinGameResult.get("success"));
    }

    @Test
    public void joinFullGameWhiteReturnsError() throws IOException, InterruptedException {
        String authTokenOne = registerAndLogin("userOne", "password");
        String authTokenTwo = registerAndLogin("userTwo", "password");
        String authTokenThree = registerAndLogin("userThree", "password");
        Assertions.assertNotNull(authTokenOne);
        Assertions.assertNotNull(authTokenTwo);
        Assertions.assertNotNull(authTokenThree);

        Map<String, Object> createGameResult = facade.createGame(authTokenOne, "gameName");
        int gameID = ((Number) createGameResult.get("gameID")).intValue();

        Assertions.assertTrue((Boolean) createGameResult.get("success"));

        Map<String, Object> joinResultOne = facade.joinGame(authTokenOne, gameID, "white");

        Assertions.assertTrue((Boolean) joinResultOne.get("success"));

        Map<String, Object> joinResultTwo = facade.joinGame(authTokenTwo, gameID, "black");

        Assertions.assertTrue((Boolean) joinResultTwo.get("success"));

        Map<String, Object> joinResultThree = facade.joinGame(authTokenThree, gameID, "white");

        Assertions.assertFalse((Boolean) joinResultThree.get("success"));
        Assertions.assertNotNull(joinResultThree.get("message"));
    }

    @Test
    public void joinFullGameBlackReturnsError() throws IOException, InterruptedException {
        String authTokenOne = registerAndLogin("userOne", "password");
        String authTokenTwo = registerAndLogin("userTwo", "password");
        String authTokenThree = registerAndLogin("userThree", "password");
        Assertions.assertNotNull(authTokenOne);
        Assertions.assertNotNull(authTokenTwo);
        Assertions.assertNotNull(authTokenThree);

        Map<String, Object> createGameResult = facade.createGame(authTokenOne, "gameName");
        int gameID = ((Number) createGameResult.get("gameID")).intValue();

        Assertions.assertTrue((Boolean) createGameResult.get("success"));

        Map<String, Object> joinResultOne = facade.joinGame(authTokenOne, gameID, "white");

        Assertions.assertTrue((Boolean) joinResultOne.get("success"));

        Map<String, Object> joinResultTwo = facade.joinGame(authTokenTwo, gameID, "black");

        Assertions.assertTrue((Boolean) joinResultTwo.get("success"));

        Map<String, Object> joinResultThree = facade.joinGame(authTokenThree, gameID, "black");

        Assertions.assertFalse((Boolean) joinResultThree.get("success"));
        Assertions.assertNotNull(joinResultThree.get("message"));
    }

    @Test
    public void joinGameUnauthorizedReturnsError() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        Map<String, Object> createGameResult = facade.createGame(authToken, "gameName");
        int gameID = ((Number) createGameResult.get("gameID")).intValue();

        Assertions.assertTrue((Boolean) createGameResult.get("success"));

        Map<String, Object> joinGameResult = facade.joinGame("", gameID, "white");

        Assertions.assertFalse((Boolean) joinGameResult.get("success"));
        Assertions.assertNotNull(joinGameResult.get("message"));
    }

    @Test
    public void joinGameMissingArgsReturnsError() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        Map<String, Object> createGameResult = facade.createGame(authToken, "gameName");
        int gameID = ((Number) createGameResult.get("gameID")).intValue();

        Assertions.assertTrue((Boolean) createGameResult.get("success"));

        Map<String, Object> joinGameResult = facade.joinGame(authToken, gameID, "");

        Assertions.assertFalse((Boolean) joinGameResult.get("success"));
        Assertions.assertNotNull(joinGameResult.get("message"));
    }

    @Test
    public void joinInvalidGameReturnsError() throws IOException, InterruptedException {
        String authToken = registerAndLogin("username", "password");
        Assertions.assertNotNull(authToken);

        Map<String, Object> joinGameResult = facade.joinGame("", 1, "white");

        Assertions.assertFalse((Boolean) joinGameResult.get("success"));
        Assertions.assertNotNull(joinGameResult.get("message"));
    }

    private Boolean registerUser() throws IOException, InterruptedException {
        Map<String, Object> registerResult = facade.register(
                "username",
                "password",
                "email"
        );

        if (!(Boolean) registerResult.get("success")) {
            return false;
        }

        Map<String, Object> logoutResult = facade.logout(registerResult.get("authToken").toString());

        return (Boolean) logoutResult.get("success");
    }

    private String registerAndLogin(String username, String password) throws IOException, InterruptedException {
        Map<String, Object> registerResult = facade.register(
                username,
                password,
                "email"
        );

        if (!(Boolean) registerResult.get("success")) {
            return null;
        }

        return registerResult.get("authToken").toString();
    }
}
