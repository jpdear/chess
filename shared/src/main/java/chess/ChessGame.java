package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTeam;

    public ChessGame() {
        currentTeam = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);

        for (ChessMove move : pieceMoves) {
            ChessPosition end = move.getEndPosition();
            ChessPiece captured = board.getPiece(end);

            board.clearPosition(startPosition);
            board.addPiece(end, piece);

            ChessPosition kingPos = board.getKingPosition(piece.getTeamColor());

            if (kingPos == null || isSafeKingPosition(kingPos)) {
                validMoves.add(move);
            }

            board.addPiece(startPosition, piece);
            board.addPiece(end, captured);
        }

        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPos);

        if (piece == null) {
            throw new InvalidMoveException();
        }

        if (piece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> validMoves = piece.pieceMoves(board, startPos);
        boolean found = false;

        for (ChessMove checkMove : validMoves) {
            if (checkMove.equals(move)) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new InvalidMoveException();
        }

        ChessPiece[][] prevState = board.getBoard();

        board.clearPosition(startPos);

        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        if (promotionPiece != null) {
            piece = new ChessPiece(currentTeam, promotionPiece);
        }

        board.addPiece(move.getEndPosition(), piece);

        if (isInCheck(currentTeam)) {
            board.setBoard(prevState);
            throw new InvalidMoveException();
        }

        boolean isWhiteTurn = currentTeam == TeamColor.WHITE;
        currentTeam = isWhiteTurn ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private boolean isSafeKingPosition(ChessPosition position) {
        board.updateValidMoves();
        TeamColor kingColor = board.getPiece(position).getTeamColor();
        Map<ChessPosition, Collection<ChessMove>> allValidMoves = board.getAllValidMoves();

        for (Map.Entry<ChessPosition, Collection<ChessMove>> entry : allValidMoves.entrySet()) {
            if (board.getPiece(entry.getKey()).getTeamColor() == kingColor) {
                continue;
            }

            for (ChessMove move : entry.getValue()) {
                if (move.getEndPosition().equals(position)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = board.getKingPosition(teamColor);

        if (kingPos == null) {
            return false;
        }

        return !isSafeKingPosition(kingPos);
    }

    private boolean hasAnyValidMove(TeamColor team) {
        int size = ChessBoard.getBoardSize();
        int min = ChessBoard.getMinIndex();

        for (int row = min; row <= size; row++) {
            for (int col = min; col <= size; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == team && !validMoves(pos).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasAnyValidMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasAnyValidMove(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
