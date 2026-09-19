package chess;

import java.util.ArrayList;

public abstract class MoveBehavior {
    protected final ChessBoard board;
    protected final ChessPosition position;
    protected final ChessGame.TeamColor color;

    public MoveBehavior(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        color = board.getPiece(position).getTeamColor();
    }

    private boolean isValidMove(int row, int col) {
        if (!ChessPosition.isValidPosition(row, col)) {
            return false;
        }

        ChessPosition checkPosition = new ChessPosition(row, col);
        ChessPiece checkPiece = board.getPiece(checkPosition);
        ChessGame.TeamColor checkColor;

        if (checkPiece == null) {
            checkColor = null;
        } else {
            checkColor = checkPiece.getTeamColor();
        }

        return checkColor != color;
    }

    protected ArrayList<ChessMove> checkDirection(int rowItr, int colItr, boolean continuous) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int checkRow = position.getRow() + rowItr;
        int checkCol = position.getColumn() + colItr;

        if (!continuous && isValidMove(checkRow, checkCol)) {
            moves.add(new ChessMove(position, new ChessPosition(checkRow, checkCol)));
        } else {
            while (isValidMove(checkRow, checkCol)) {
                ChessPosition newPos = new ChessPosition(checkRow, checkCol);

                moves.add(new ChessMove(position, newPos));

                ChessPiece newPiece = board.getPiece(newPos);
                ChessGame.TeamColor newColor;

                if (newPiece == null) {
                    newColor = null;
                } else {
                    newColor = newPiece.getTeamColor();
                }

                if (newColor != color && newColor != null) {
                    break;
                }

                checkRow += rowItr;
                checkCol += colItr;
            }
        }

        return moves;
    }

    /**
     * Gets all valid moves based on the position on the board.
     *
     * @return ArrayList of valid positions to move to
     */
    public abstract ArrayList<ChessMove> getValidMoves();
}
