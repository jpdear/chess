package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws AlreadyTakenException, DataAccessException {
        String username = request.username();
        UserData checkUser = userDAO.getUser(username);

        if (checkUser != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        userDAO.createUser(username, request.password(), request.email());

        AuthData newAuth = authDAO.createAuth(username);

        return new RegisterResult(username, newAuth.authToken());
    }
}
