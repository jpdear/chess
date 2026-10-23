package handler;

import service.ServiceResult;

public interface ServerHandler {
    public ServiceResult handle(String requestData) throws BadRequestException;
}
