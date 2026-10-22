package service;

import java.util.Objects;

public abstract class ServiceResult {
    protected final String message;

    public ServiceResult(String message) {
        this.message = message;
    }

    public ServiceResult() {
        this.message = "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceResult that = (ServiceResult) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message);
    }

    @Override
    public String toString() {
        return String.format("{'message': %s}", message);
    }

    public String getMessage() {
        return message;
    }
}
