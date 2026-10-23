package dataAccess;

import model.UserData;

public interface UserDAO {
    public void clear();
    public UserData createUser(String username, String password, String email);
    public UserData getUser(String username);
}
