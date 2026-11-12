package client;

import chess.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ClientMain {
    public enum State {
        QUIT,
        SIGNEDOUT,
        SIGNEDIN
    }
    private static final String invalidArgsMessage =
            "Invalid number of arguments. "
                    + "Type 'help' to see a list of valid commands.";
    private static final String unknownCommandMessage = "Unknown command. "
            + "Type 'help' to see a list of valid commands.";
    private static State userState = State.SIGNEDOUT;
    private static final Scanner scanner = new Scanner(System.in);
    private static final ServerFacade facade = new ServerFacade("http://localhost:8080");

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Welcome to Chess. Type 'help' to get started.");

        while (userState != State.QUIT) {
            System.out.print(">>> ");
            String input = scanner.nextLine();
            String[] inputArgs = input.split(" ");

            if (userState == State.SIGNEDOUT) {
                handlePreLogin(inputArgs);
            } else {
                handlePostLogin(inputArgs);
            }
        }
    }

    private static void handlePreLogin(String[] args) throws URISyntaxException, IOException, InterruptedException {
        String commandName = args[0];
        ArrayList<String> commandArgs = new ArrayList<>();

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
                if (commandArgs.size() != 2) {
                    System.out.println(invalidArgsMessage);

                    break;
                }

                System.out.println("Logging you in...");
                userState = State.SIGNEDIN;

                break;
            case "register":
                if (commandArgs.size() != 3) {
                    System.out.println(invalidArgsMessage);

                    break;
                }

                System.out.println("Creating your account...");
                facade.register(commandArgs.get(0), commandArgs.get(1), commandArgs.get(2));

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
                if (commandArgs.size() != 1) {
                    System.out.println(invalidArgsMessage);

                    break;
                }

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
                if (commandArgs.size() != 1) {
                    System.out.println(invalidArgsMessage);

                    break;
                }

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
}
