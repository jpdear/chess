package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;

public class BoardRenderer {
    private static final String[] COL_LABELS = {"a", "b", "c", "d", "e", "f", "g", "h"};

    public static void renderBoard(ChessBoard chessBoard, ChessGame.TeamColor color) {
        ChessPiece[][] board = chessBoard.getBoard();
        int rowStart = color == TeamColor.WHITE
                ? 7 : 0;
        int colStart = color == TeamColor.WHITE
                ? 0 : 7;
        int rowItr = color == TeamColor.WHITE
                ? -1 : 1;
        int colItr = color == TeamColor.WHITE
                ? 1 : -1;

        System.out.print("  ");

        for (int i = colStart; i >= 0 && i < board.length; i += colItr) {
            System.out.print("\u2003" + COL_LABELS[i] + "\u2003");
        }

        for (int row = rowStart; inBounds(board.length, row, color); row += rowItr) {
            System.out.println();
            System.out.print(row+1 + " ");
            for (int col = colStart; col >= 0 && col < board.length; col += colItr) {
                ChessPiece piece = board[row][col];
                boolean isDark = (row + col) % 2 == 0;
                String bg = isDark
                        ? EscapeSequences.SET_BG_COLOR_DARK_GREY
                        : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

                System.out.print(bg);

                if (piece == null) {
                    System.out.print(EscapeSequences.EMPTY);
                    continue;
                }

                PieceType pieceType = piece.getPieceType();
                TeamColor pieceColor = piece.getTeamColor();
                String textColor = pieceColor == TeamColor.WHITE
                        ? EscapeSequences.SET_TEXT_COLOR_WHITE
                        : EscapeSequences.SET_TEXT_COLOR_BLACK;

                String pieceIcon = switch (pieceType) {
                    case PieceType.PAWN -> pieceColor == TeamColor.WHITE
                            ? EscapeSequences.WHITE_PAWN
                            : EscapeSequences.BLACK_PAWN;
                    case PieceType.BISHOP -> pieceColor == TeamColor.WHITE
                            ? EscapeSequences.WHITE_BISHOP
                            : EscapeSequences.BLACK_BISHOP;
                    case PieceType.ROOK -> pieceColor == TeamColor.WHITE
                            ? EscapeSequences.WHITE_ROOK
                            : EscapeSequences.BLACK_ROOK;
                    case PieceType.KNIGHT -> pieceColor == TeamColor.WHITE
                            ? EscapeSequences.WHITE_KNIGHT
                            : EscapeSequences.BLACK_KNIGHT;
                    case PieceType.QUEEN -> pieceColor == TeamColor.WHITE
                            ? EscapeSequences.WHITE_QUEEN
                            : EscapeSequences.BLACK_QUEEN;
                    case PieceType.KING -> pieceColor == TeamColor.WHITE
                            ? EscapeSequences.WHITE_KING
                            : EscapeSequences.BLACK_KING;
                };

                System.out.print(textColor);
                System.out.print(pieceIcon);
            }

            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print(" " + (row+1));
        }

        System.out.println();
        System.out.print("  ");

        for (int i = colStart; i >= 0 && i < board.length; i += colItr) {
            System.out.print("\u2003" + COL_LABELS[i] + "\u2003");
        }

        System.out.println();
    }

    private static Boolean inBounds(int length, int index, TeamColor color) {
        if (color == TeamColor.WHITE) {
            return index+1 > 0;
        } else {
            return index < length;
        }
    }
}
