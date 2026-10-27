package passoff.service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerSuccess() throws Exception {
        RegisterResult result = userService.register(new RegisterRequest("alice", "pass123", "alice@mail.com"));

        assertEquals("alice", result.username());
        assertNotNull(result.authToken());

        UserData stored = userDAO.getUser("alice");

        assertNotNull(stored);
        assertEquals("pass123", stored.password());
        assertEquals("alice@mail.com", stored.email());

        AuthData auth = authDAO.getAuth(result.authToken());

        assertNotNull(auth);
        assertEquals("alice", auth.username());
    }

    @Test
    public void loginUnknownUser() {
        assertThrows(UserNotFoundException.class, () ->
                userService.login(new LoginRequest("nobody", "pass123")));
    }

    @Test
    public void loginWrongPassword() throws Exception {
        userService.register(new RegisterRequest("alice", "pass123", "alice@mail.com"));

        assertThrows(UserNotFoundException.class, () ->
                userService.login(new LoginRequest("alice", "wrongpass")));
    }

    @Test
    public void logoutSuccess() throws Exception {
        RegisterResult registered = userService.register(new RegisterRequest("alice", "pass123", "alice@mail.com"));

        userService.logout(new LogoutRequest(registered.authToken()));

        assertNull(authDAO.getAuth(registered.authToken()));
    }

    @Test
    public void logoutBadToken() {
        assertThrows(UserNotFoundException.class, () ->
                userService.logout(new LogoutRequest("not-a-real-token")));
    }
}
