package dataAccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() {
        auths.clear();
    }

    @Override
    public AuthData getAuth(String token) {
        return auths.get(token);
    }

    @Override
    public AuthData createAuth(String username) {
        String token = AuthToken.generateToken();

        AuthData auth = new AuthData(token, username);

        auths.put(token, auth);

        return auth;
    }

    @Override
    public void deleteAuth(String token) {
        auths.remove(token);
    }
}
