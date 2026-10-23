package dataaccess;

import model.UserData;

public interface UserDAO {
    public void clear() throws DataAccessException;
    public UserData createUser(String username, String password, String email) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
}
