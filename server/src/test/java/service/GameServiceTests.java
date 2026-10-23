package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import service.request.CreateGameRequest;
import service.result.CreateGameResult;
import service.result.GameListEntry;
import service.request.JoinGameRequest;
import service.request.ListGamesRequest;
import service.result.ListGamesResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private MemoryGameDAO gameDAO;
    private GameService gameService;
    private String aliceToken;
    private String bobToken;

    @BeforeEach
    public void setup() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);

        aliceToken = authDAO.createAuth("alice").authToken();
        bobToken = authDAO.createAuth("bob").authToken();
    }

    @Test
    public void createGameSuccess() throws Exception {
        CreateGameResult result = gameService.createGame(new CreateGameRequest(aliceToken, "myGame"));

        GameData stored = gameDAO.getGame(result.gameID());

        assertNotNull(stored);
        assertEquals("myGame", stored.gameName());
        assertNull(stored.whiteUsername());
        assertNull(stored.blackUsername());
        assertNotNull(stored.game());
    }

    @Test
    public void createGameBadToken() throws Exception {
        assertThrows(UserNotFoundException.class, () ->
                gameService.createGame(new CreateGameRequest("not-a-real-token", "myGame")));

        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void listGamesSuccess() throws Exception {
        int firstID = gameService.createGame(new CreateGameRequest(aliceToken, "first")).gameID();
        int secondID = gameService.createGame(new CreateGameRequest(aliceToken, "second")).gameID();

        gameService.joinGame(new JoinGameRequest(aliceToken, ChessGame.TeamColor.WHITE, firstID));

        ListGamesResult result = gameService.listGames(new ListGamesRequest(bobToken));

        assertEquals(2, result.games().size());

        GameListEntry first = findEntry(result, firstID);

        assertEquals("first", first.gameName());
        assertEquals("alice", first.whiteUsername());
        assertNull(first.blackUsername());

        GameListEntry second = findEntry(result, secondID);

        assertEquals("second", second.gameName());
        assertNull(second.whiteUsername());
        assertNull(second.blackUsername());
    }

    @Test
    public void listGamesBadToken() {
        assertThrows(UserNotFoundException.class, () ->
                gameService.listGames(new ListGamesRequest("not-a-real-token")));
    }

    @Test
    public void joinGameAsWhiteSuccess() throws Exception {
        int gameID = gameService.createGame(new CreateGameRequest(aliceToken, "myGame")).gameID();

        gameService.joinGame(new JoinGameRequest(aliceToken, ChessGame.TeamColor.WHITE, gameID));

        GameData stored = gameDAO.getGame(gameID);

        assertEquals("alice", stored.whiteUsername());
        assertNull(stored.blackUsername());
        assertEquals("myGame", stored.gameName());
    }

    @Test
    public void joinGameAsBlackSuccess() throws Exception {
        int gameID = gameService.createGame(new CreateGameRequest(aliceToken, "myGame")).gameID();

        gameService.joinGame(new JoinGameRequest(aliceToken, ChessGame.TeamColor.BLACK, gameID));

        GameData stored = gameDAO.getGame(gameID);

        assertEquals("alice", stored.blackUsername());
        assertNull(stored.whiteUsername());
        assertEquals("myGame", stored.gameName());
    }

    @Test
    public void joinGameWhiteTaken() throws Exception {
        int gameID = gameService.createGame(new CreateGameRequest(aliceToken, "myGame")).gameID();

        gameService.joinGame(new JoinGameRequest(bobToken, ChessGame.TeamColor.WHITE, gameID));

        assertThrows(AlreadyTakenException.class, () ->
                gameService.joinGame(new JoinGameRequest(aliceToken, ChessGame.TeamColor.WHITE, gameID)));

        assertEquals("bob", gameDAO.getGame(gameID).whiteUsername());
    }

    @Test
    public void joinGameBlackTaken() throws Exception {
        int gameID = gameService.createGame(new CreateGameRequest(aliceToken, "myGame")).gameID();

        gameService.joinGame(new JoinGameRequest(bobToken, ChessGame.TeamColor.BLACK, gameID));

        assertThrows(AlreadyTakenException.class, () ->
                gameService.joinGame(new JoinGameRequest(aliceToken, ChessGame.TeamColor.BLACK, gameID)));

        assertEquals("bob", gameDAO.getGame(gameID).blackUsername());
    }

    @Test
    public void joinGameNonexistentGame() {
        assertThrows(BadRequestException.class, () ->
                gameService.joinGame(new JoinGameRequest(aliceToken, ChessGame.TeamColor.WHITE, -1)));
    }

    @Test
    public void joinGameBadToken() throws Exception {
        int gameID = gameService.createGame(new CreateGameRequest(aliceToken, "myGame")).gameID();

        assertThrows(UserNotFoundException.class, () ->
                gameService.joinGame(new JoinGameRequest("not-a-real-token", ChessGame.TeamColor.WHITE, gameID)));

        assertNull(gameDAO.getGame(gameID).whiteUsername());
    }

    private GameListEntry findEntry(ListGamesResult result, int gameID) {
        return result.games().stream()
                .filter(entry -> entry.gameID() == gameID)
                .findFirst()
                .orElseThrow();
    }
}
