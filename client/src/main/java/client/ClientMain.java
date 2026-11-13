package client;

import chess.ChessGame;
import model.GameData;
import ui.BoardRenderer;

import java.io.IOException;
import java.util.*;

public class ClientMain {
    private enum State {
        QUIT("Quit"),
        SIGNEDOUT("LOGGED OUT"),
        SIGNEDIN("LOGGED IN");

        private final String label;

        State(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final String INVALID_ARGS_MESSAGE =
            "Invalid number of arguments. "
                    + "Type 'help' to see a list of valid commands.";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command. "
            + "Type 'help' to see a list of valid commands.";
    private static final String INVALID_GAME_ID_MESSAGE =
            "Error: Invalid game ID. Please enter a valid ID. Type 'list' to see games.";
    private static final String SESSION_EXPIRED_MESSAGE =
            "Your session has expired. Please log in again.";
    private static State userState = State.SIGNEDOUT;
    private static String userAuth;
    private static ArrayList<Integer> gamesList = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final ServerFacade facade = new ServerFacade("http://localhost:8080");

    public static void main(String[] args) {
        System.out.println("Welcome to Chess. Type 'help' to get started.");

        while (userState != State.QUIT) {
            System.out.print("[" + userState + "] >>> ");
            String input = scanner.nextLine();

            if (input.isBlank()) continue;

            String[] inputArgs = input.trim().split("\\s+");

            try {
                if (userState == State.SIGNEDOUT) {
                    handlePreLogin(inputArgs);
                } else {
                    handlePostLogin(inputArgs);
                }
            } catch (IOException e) {
                System.out.println("Error: Could not connect to the server. Make sure it is running and try again.");
            } catch (InterruptedException e) {
                System.out.println("Error: The request was cancelled. Please try again.");
            } catch (Exception e) {
                System.out.println("Error: Something went wrong. Please restart the application.");
            }
        }
    }

    private static void handlePreLogin(String[] args) throws IOException, InterruptedException {
        String commandName = args[0];
        ArrayList<String> commandArgs = new ArrayList<>();
        Map<String, Object> result;

        if (args.length > 1) {
            commandArgs = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
        }

        switch (commandName.toLowerCase()) {
            case "help":
                System.out.println("register <username> <password> <email> - create an account");
                System.out.println("login <username> <password> - log into an existing account");
                System.out.println("quit - exit the program");
                System.out.println("help - list valid commands");

                break;
            case "quit":
                System.out.println("Goodbye!");
                userState = State.QUIT;

                break;
            case "login":
                if (isInvalidArgsLength(commandArgs.size(), 2)) break;

                System.out.println("Logging you in...");

                result = facade.login(
                        commandArgs.get(0),
                        commandArgs.get(1)
                );

                if (isInvalidResult(result, Map.of(
                        400, "Please enter both a username and password.",
                        401, "Incorrect username or password."
                ))) break;

                System.out.println("Successfully logged in!");

                userAuth = result.get("authToken").toString();
                userState = State.SIGNEDIN;

                break;
            case "register":
                if (isInvalidArgsLength(commandArgs.size(), 3)) break;

                System.out.println("Creating your account...");

                result = facade.register(
                        commandArgs.get(0),
                        commandArgs.get(1),
                        commandArgs.get(2)
                );

                if (isInvalidResult(result, Map.of(
                        400, "Please enter a username, password, and email.",
                        403, "That username is already taken."
                ))) break;

                userAuth = result.get("authToken").toString();
                userState = State.SIGNEDIN;

                System.out.println("Account successfully created!");

                break;
            default:
                System.out.println(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    private static void handlePostLogin(String[] args) throws IOException, InterruptedException {
        String commandName = args[0];
        ArrayList<String> commandArgs = new ArrayList<>();
        Map<String, Object> result;

        if (args.length > 1) {
            commandArgs = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
        }

        switch (commandName.toLowerCase()) {
            case "create":
                if (isInvalidArgsLength(commandArgs.size(), 1)) break;

                System.out.println("Creating game...");

                result = facade.createGame(userAuth, commandArgs.getFirst());

                if (isInvalidResult(result, Map.of(
                        400, "Please enter a game name.",
                        401, SESSION_EXPIRED_MESSAGE
                ))) break;

                System.out.println("Game successfully created!");

                break;
            case "list":
                result = facade.listGames(userAuth);

                if (isInvalidResult(result, Map.of(
                        401, SESSION_EXPIRED_MESSAGE
                ))) break;

                gamesList.clear();
                System.out.println("All Games:");

                GameData[] games = (GameData[]) result.get("games");

                for (int i = 0; i < games.length; i++) {
                    GameData game = games[i];

                    gamesList.add(game.gameID());

                    String whiteName = game.whiteUsername() != null ? game.whiteUsername() : "N/A";
                    String blackName = game.blackUsername() != null ? game.blackUsername() : "N/A";

                    System.out.println(i+1 + ". " + game.gameName() + " - WHITE: " + whiteName + ", BLACK: " + blackName);
                }

                break;
            case "join":
                if (isInvalidArgsLength(commandArgs.size(), 2)) break;

                Integer joinGameID = getGameID(commandArgs.getFirst());

                if (joinGameID == null) break;

                System.out.println("Joining game...");

                String color = commandArgs.get(1);
                result = facade.joinGame(userAuth, joinGameID, color);

                if (isInvalidResult(result, Map.of(
                        400, "Please choose WHITE or BLACK.",
                        401, SESSION_EXPIRED_MESSAGE,
                        403, "That color is already taken for this game."
                ))) break;

                System.out.println("Success!");

                ChessGame.TeamColor teamColor = color.equalsIgnoreCase("white")
                        ? ChessGame.TeamColor.WHITE
                        : ChessGame.TeamColor.BLACK;

                BoardRenderer.renderBoard(new ChessGame().getBoard(), teamColor);

                break;
            case "observe":
                if (isInvalidArgsLength(commandArgs.size(), 1)) break;

                Integer observeGameID = getGameID(commandArgs.getFirst());

                if (observeGameID == null) break;

                System.out.println("Observing game...");

                BoardRenderer.renderBoard(new ChessGame().getBoard(), ChessGame.TeamColor.WHITE);

                break;
            case "logout":
                System.out.println("Logging you out...");

                result = facade.logout(userAuth);

                if (isInvalidResult(result, Map.of(
                        401, SESSION_EXPIRED_MESSAGE
                ))) break;

                userAuth = null;
                userState = State.SIGNEDOUT;

                gamesList.clear();
                System.out.println("Successfully logged out.");

                break;
            case "quit":
                System.out.println("Goodbye!");

                userState = State.QUIT;

                break;
            case "help":
                System.out.println("create <name> - create a new game");
                System.out.println("list - list all games");
                System.out.println("join <ID> [WHITE|BLACK] - join a game");
                System.out.println("observe <ID> - observe a game");
                System.out.println("logout - logout from current session");
                System.out.println("quit - exit the program");
                System.out.println("help - list valid commands");

                break;
            default:
                System.out.println(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    private static boolean isInvalidArgsLength(int size, int requiredArgs) {
        if (size != requiredArgs) {
            System.out.println(INVALID_ARGS_MESSAGE);

            return true;
        }

        return false;
    }

    private static Integer getGameID(String arg) {
        try {
            int requestedID = Integer.parseInt(arg);

            if (requestedID < 1 || requestedID > gamesList.size()) {
                System.out.println(INVALID_GAME_ID_MESSAGE);

                return null;
            }

            return gamesList.get(requestedID - 1);
        } catch (NumberFormatException e) {
            System.out.println(INVALID_GAME_ID_MESSAGE);

            return null;
        }
    }

    private static boolean isInvalidResult(Map<String, Object> result, Map<Integer, String> messages) {
        if (!(boolean) result.get("success")) {
            int status = (int) result.get("status");

            System.out.println(
                    "Error: " + messages.getOrDefault(
                            status,
                            "Something went wrong. Please try again."
                    )
            );

            return true;
        }

        return false;
    }
}
