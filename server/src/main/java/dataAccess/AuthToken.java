package dataAccess;

import java.util.UUID;

public class AuthToken {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
