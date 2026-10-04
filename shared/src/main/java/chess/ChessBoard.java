package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private static final int boardSize = 8;
    private static final int minIndex = 1;
    private ChessPiece[][] board = new ChessPiece[boardSize][boardSize];
    private Map<ChessPosition, Collection<ChessMove>> currentValidMoves = new HashMap<>();
    private static final Map<List<Integer>, ChessPiece> standardLayout = Map.ofEntries(
        Map.entry(List.of(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)),
        Map.entry(List.of(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT)),
        Map.entry(List.of(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP)),
        Map.entry(List.of(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN)),
        Map.entry(List.of(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING)),
        Map.entry(List.of(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP)),
        Map.entry(List.of(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT)),
        Map.entry(List.of(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)),
        Map.entry(List.of(7, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(7, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(7, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(7, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(7, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(7, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(7, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(7, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(2, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(2, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(2, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(2, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(2, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(2, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(2, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(2, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)),
        Map.entry(List.of(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)),
        Map.entry(List.of(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT)),
        Map.entry(List.of(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP)),
        Map.entry(List.of(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN)),
        Map.entry(List.of(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING)),
        Map.entry(List.of(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP)),
        Map.entry(List.of(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT)),
        Map.entry(List.of(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK))
    );

    public ChessBoard() {
        updateValidMoves();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;

        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return String.format("ChessBoard%s", hashCode());
    }

    public static int getBoardSize() {
        return boardSize;
    }

    public static int getMinIndex() {
        return minIndex;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();

        board[row-1][col-1] = piece;
    }

    public void clearPosition(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();

        board[row-1][col-1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();

        return board[row-1][col-1];
    }

    public void setBoard(ChessPiece[][] newBoard) {
        if (newBoard.length != board.length) {
            return;
        }

        for (int i = 0; i < newBoard.length; i++) {
            if (newBoard[i].length != board[i].length) {
                return;
            }
        }

        board = newBoard;

        updateValidMoves();
    }

    public ChessPiece[][] getBoard() {
        ChessPiece[][] copy = new ChessPiece[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            copy[i] = Arrays.copyOf(board[i], boardSize);
        }

        return copy;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (ChessPiece[] chessPieces : board) {
            Arrays.fill(chessPieces, null);
        }

        for (Map.Entry<List<Integer>, ChessPiece> entry : standardLayout.entrySet()) {
            List<Integer> coord = entry.getKey();
            ChessPosition pos = new ChessPosition(coord.get(0), coord.get(1));
            addPiece(pos, entry.getValue());
        }
    }

    public void updateValidMoves() {
        currentValidMoves.clear();

        int row = 1;
        int col = 1;

        for (ChessPiece [] chessPieces : board) {
            for (ChessPiece piece : chessPieces) {
                if (piece != null && ChessPosition.isValidPosition(row, col)) {
                    ChessPosition checkPos = new ChessPosition(row, col);
                    currentValidMoves.put(checkPos, piece.pieceMoves(this, checkPos));
                }
                col++;
            }
            col = 1;
            row++;
        }
    }

    public Map<ChessPosition, Collection<ChessMove>> getAllValidMoves() {
        return currentValidMoves;
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor color) {
        int row = 1;
        int col = 1;

        for (ChessPiece[] pieces : board) {
            for (ChessPiece piece : pieces) {
                if (piece == null) {
                    col++;
                    continue;
                }

                if (piece.getTeamColor() == color && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(row, col);
                }

                col++;
            }

            col = 1;
            row++;
        }

        return null;
    }
}
