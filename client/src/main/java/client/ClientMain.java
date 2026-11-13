package client;

import chess.*;

import java.io.IOException;
import java.net.URISyntaxException;
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

    private static final String invalidArgsMessage =
            "Invalid number of arguments. "
                    + "Type 'help' to see a list of valid commands.";
    private static final String unknownCommandMessage = "Unknown command. "
            + "Type 'help' to see a list of valid commands.";
    private static State userState = State.SIGNEDOUT;
    private static String userAuth;
    private static final Scanner scanner = new Scanner(System.in);
    private static final ServerFacade facade = new ServerFacade("http://localhost:8080");

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Welcome to Chess. Type 'help' to get started.");

        while (userState != State.QUIT) {
            System.out.print("[" + userState + "] >>> ");
            String input = scanner.nextLine();
            String[] inputArgs = input.split(" ");

            if (userState == State.SIGNEDOUT) {
                handlePreLogin(inputArgs);
            } else {
                handlePostLogin(inputArgs);
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

        switch (commandName) {
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

                if (isInvalidResult(result)) break;

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

                if (isInvalidResult(result)) break;

                System.out.println("Account successfully created! Log in to continue.");

                break;
            default:
                System.out.println(unknownCommandMessage);
        }
    }

    private static void handlePostLogin(String[] args) {
        String commandName = args[0];
        ArrayList<String> commandArgs = new ArrayList<>();

        if (args.length > 1) {
            commandArgs = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
        }

        switch (commandName) {
            case "create":
                if (isInvalidArgsLength(commandArgs.size(), 1)) break;

                System.out.println("Creating game...");

                break;
            case "list":
                System.out.println("All Games:");

                break;
            case "join":
                if (commandArgs.isEmpty() || commandArgs.size() > 2) {
                    System.out.println(invalidArgsMessage);

                    break;
                }

                System.out.println("Joining game...");

                break;
            case "observe":
                if (isInvalidArgsLength(commandArgs.size(), 1)) break;

                System.out.println("Observing game...");

                break;
            case "logout":
                System.out.println("Logging you out...");
                userState = State.SIGNEDOUT;

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
                System.out.println(unknownCommandMessage);
        }
    }

    private static boolean isInvalidArgsLength(int size, int requiredArgs) {
        if (size != requiredArgs) {
            System.out.println(invalidArgsMessage);

            return true;
        }

        return false;
    }

    private static boolean isInvalidResult(Map<String, Object> result) {
        if (result.get("success") == "false") {
            System.out.println(result.get("message"));

            return true;
        }

        return false;
    }
}
