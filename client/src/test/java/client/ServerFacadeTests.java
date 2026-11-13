package client;

import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;
    private static String serverUrl;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        serverUrl = String.format("http://localhost:%d", port);
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
}
