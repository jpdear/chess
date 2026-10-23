package server;

import com.google.gson.Gson;
import handler.BadRequestException;
import handler.RegisterHandler;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .exception(BadRequestException.class, (e, ctx) -> {
                    Map<String, String> body = Map.of("message", e.getMessage());
                    String json = new Gson().toJson(body);

                    ctx.contentType("application/json");
                    ctx.result(json);
                    ctx.status(400);
                });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);

        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) throws BadRequestException {
        RegisterHandler handler = new RegisterHandler();

        ctx.contentType("application/json");

        String body = ctx.body();

        handler.handle(body);
    }
}
