package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.request.LoginRequest;
import service.result.LoginResult;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.RegisterResult;

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

    public LoginResult login(LoginRequest request) throws DataAccessException, UserNotFoundException {
        String username = request.username();
        UserData user = userDAO.getUser(username);

        if (user == null) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        if (!user.password().equals(request.password())) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        AuthData auth = authDAO.createAuth(username);

        return new LoginResult(username, auth.authToken());
    }

    public void logout(LogoutRequest request) throws DataAccessException, UserNotFoundException {
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null) {
            throw new UserNotFoundException("Error: unauthorized");
        }

        authDAO.deleteAuth(request.authToken());
    }
}
