package service;

public record GameListEntry(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName
) {}
