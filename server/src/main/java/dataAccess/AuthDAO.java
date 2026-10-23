package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException;
    public AuthData getAuth(String token) throws DataAccessException;
    public AuthData createAuth(String username) throws DataAccessException;
    public void deleteAuth(String token) throws DataAccessException, UserNotFoundException;
}
