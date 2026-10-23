package server;

import com.google.gson.Gson;
import service.AlreadyTakenException;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import handler.BadRequestException;
import handler.RegisterHandler;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

import java.util.Map;

public class Server {
    private final Javalin javalin;

    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();

    private final UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);

    private final RegisterHandler registerHandler = new RegisterHandler(userService);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .exception(BadRequestException.class, (e, ctx) -> {
                    addMessageToContext(ctx, 400, e.getMessage());
                })
                .exception(AlreadyTakenException.class, (e, ctx) -> {
                    addMessageToContext(ctx, 403, e.getMessage());
                })
                .exception(DataAccessException.class, (e, ctx) -> {
                    addMessageToContext(ctx, 500, e.getMessage());
                })
                .exception(Exception.class, (e, ctx) -> {
                    addMessageToContext(ctx, 500, String.format("Error: %s", e.getMessage()));
                });
    }

    private void addMessageToContext(Context ctx, int status, String message) {
        Map<String, String> body = Map.of("message", message);
        String json = new Gson().toJson(body);

        ctx.contentType("application/json");
        ctx.result(json);
        ctx.status(status);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);

        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException {
        String body = ctx.body();
        String result = registerHandler.handle(body);

        ctx.contentType("application/json");
        ctx.result(result);
        ctx.status(200);
    }
}
