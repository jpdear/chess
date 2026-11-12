package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade (String url) {
        this.serverUrl = url;
    }

    public void register(String username, String password, String email) throws URISyntaxException, IOException, InterruptedException {
        String jsonBody = gson.toJson(Map.of(
                "username", username,
                "password", password,
                "email", email
        ));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + "/user"))
                .timeout(Duration.ofMillis(5000))
                .POST(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        System.out.println(response.body());
    }
    public void login(String username, String password) {}
    public void logout() {}
    public void createGame(String name) {}
    public void listGames() {}
    public void joinGame(String gameID) {}
}
