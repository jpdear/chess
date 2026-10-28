package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

public class ServerMain {
    public static void main(String[] args) {
        try {
            int port = 8080;
            Server server = new Server();

            DatabaseManager.createDatabase();
            server.run(port);

            System.out.println("♕ 240 Chess Server running on port " + port);
        } catch (DataAccessException ex) {
            System.out.printf("An error occurred while starting the server: %s", ex.getMessage());
        }
    }
}
