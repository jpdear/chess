package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import service.RegisterRequest;
import service.ServiceResult;

public class RegisterHandler implements ServerHandler {
    @Override
    public ServiceResult handle(String requestData) throws BadRequestException {
        Gson gson = new Gson();
        RegisterRequest request;

        try {
            request = gson.fromJson(requestData, RegisterRequest.class);
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException("Error: bad request", ex);
        }

        if (request == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.username().isBlank() || request.password().isBlank() || request.email().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        return null;
    }
}
