package service;

import java.util.Objects;

public class RegisterResult extends ServiceResult {
    private final String username;
    private final String password;
    private final String email;

    public RegisterResult(String username, String password, String email, String message) {
        super(message);
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public RegisterResult(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RegisterResult that = (RegisterResult) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, password, email);
    }

    @Override
    public String toString() {
        return String.format(
                "{'username': '%s', 'password': '%s', 'email': '%s'%s}",
                username,
                password,
                email,
                message.isEmpty() ? "" : String.format("'message': %s", message)
        );
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
