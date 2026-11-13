package client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.GameData;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverUrl;
    private final Gson gson = new Gson();
    private final Duration timeout = Duration.ofMillis(5000);

    public ServerFacade (String url) {
        this.serverUrl = url;
    }

    public Map<String, Object> register(String username, String password, String email) throws IOException, InterruptedException {
        String jsonBody = gson.toJson(Map.of(
                "username", username,
                "password", password,
                "email", email
        ));
        HttpResponse<String> response = sendRequest("/user", "POST", jsonBody);

        return handleResponse(response);
    }

    public Map<String, Object> login(String username, String password) throws IOException, InterruptedException {
        String jsonBody = gson.toJson(Map.of(
                "username", username,
                "password", password
        ));
        HttpResponse<String> response = sendRequest("/session", "POST", jsonBody);

        return handleResponse(response);
    }

    public Map<String, Object> logout(String authToken) throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/session", "DELETE", null, authToken);

        return handleResponse(response);
    }
    public Map<String, Object> createGame(String authToken, String name) throws IOException, InterruptedException {
        String jsonBody = gson.toJson(Map.of(
                "gameName", name
        ));
        HttpResponse<String> response = sendRequest("/game", "POST", jsonBody, authToken);

        return handleResponse(response);
    }

    public Map<String, Object> listGames(String authToken) throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/game", "GET", null, authToken);
        Map<String, Object> result = handleResponse(response);

        if (result.containsKey("games")) {
            GameData[] games = gson.fromJson(gson.toJsonTree(result.get("games")), GameData[].class);

            result.put("games", games);
        }

        return result;
    }

    public Map<String, Object> joinGame(String authToken, int gameID, String color) throws IOException, InterruptedException {
        String jsonBody = gson.toJson(Map.of(
                "playerColor", color,
                "gameID", gameID
        ));
        HttpResponse<String> response = sendRequest("/game", "PUT", jsonBody, authToken);

        return handleResponse(response);
    }

    private HttpResponse<String> sendRequest(String endpoint, String method, String body) throws IOException, InterruptedException {
        BodyPublisher publisher = body != null ? BodyPublishers.ofString(body) : BodyPublishers.noBody();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .timeout(timeout)
                .method(method, publisher)
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    private HttpResponse<String> sendRequest(
            String endpoint,
            String method,
            String body,
            String authToken
    ) throws IOException, InterruptedException {
        BodyPublisher publisher = body != null ? BodyPublishers.ofString(body) : BodyPublishers.noBody();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .timeout(timeout)
                .method(method, publisher)
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    private Map<String, Object> handleResponse(HttpResponse<String> response) {
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> responseBody = gson.fromJson(response.body(), mapType);
        int statusCode = response.statusCode();

        responseBody.put("success", statusCode < 300);

        return responseBody;
    }
}
