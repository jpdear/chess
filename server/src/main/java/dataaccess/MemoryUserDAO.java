package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public UserData createUser(String username, String password, String email) {
        UserData user = new UserData(username, password, email);

        users.put(username, user);

        return user;
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }
}
