package server;

import com.google.gson.Gson;
import dataAccess.*;
import handler.*;
import service.AlreadyTakenException;
import io.javalin.*;
import io.javalin.http.Context;
import service.AppService;
import service.UserService;

import java.util.Map;

public class Server {
    private final Javalin javalin;

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final AppService appService = new AppService(userDAO, authDAO, gameDAO);

    private final ClearHandler clearHandler = new ClearHandler(appService);
    private final RegisterHandler registerHandler = new RegisterHandler(userService);
    private final LoginHandler loginHandler = new LoginHandler(userService);
    private final LogoutHandler logoutHandler = new LogoutHandler(userService);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .exception(BadRequestException.class, (e, ctx) -> addMessageToContext(ctx, 400, e.getMessage()))
                .exception(AlreadyTakenException.class, (e, ctx) -> addMessageToContext(ctx, 403, e.getMessage()))
                .exception(DataAccessException.class, (e, ctx) -> addMessageToContext(ctx, 500, e.getMessage()))
                .exception(UserNotFoundException.class, (e, ctx) -> addMessageToContext(ctx, 401, e.getMessage()))
                .exception(Exception.class, (e, ctx) -> addMessageToContext(ctx, 500, String.format("Error: %s", e.getMessage())));
    }

    private void addMessageToContext(Context ctx, int status, String message) {
        Map<String, String> body = Map.of("message", message);
        String json = new Gson().toJson(body);

        ctx.contentType("application/json");
        ctx.result(json);
        ctx.status(status);
    }

    private void addSuccessToContext(Context ctx, String result) {
        ctx.contentType("application/json");
        ctx.result(result);
        ctx.status(200);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);

        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx) throws DataAccessException {
        clearHandler.handle(ctx);
        addSuccessToContext(ctx, new Gson().toJson(Map.of()));
    }

    private void register(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException {
        String result = registerHandler.handle(ctx);

        addSuccessToContext(ctx, result);
    }

    private void login(Context ctx) throws UserNotFoundException, BadRequestException, DataAccessException {
        String result = loginHandler.handle(ctx);

        addSuccessToContext(ctx, result);
    }

    private void logout(Context ctx) throws UserNotFoundException, BadRequestException, DataAccessException {
        logoutHandler.handle(ctx);
        addSuccessToContext(ctx, new Gson().toJson(Map.of()));
    }
}
