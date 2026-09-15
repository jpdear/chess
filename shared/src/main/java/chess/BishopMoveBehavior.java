package chess;

import java.util.ArrayList;

public class BishopMoveBehavior implements MoveBehavior {
    private ArrayList<ChessMove> checkDiagonal(ChessBoard board, ChessPosition position, int rowItr, int colItr) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int firstRow = position.getRow() + rowItr;
        int firstCol = position.getColumn() + colItr;

        if (!ChessPosition.isValidPosition(firstRow, firstCol)) {
            return moves;
        }

        ChessPosition currentCheck = new ChessPosition(firstRow, firstCol);

        while (board.getPiece(currentCheck).getPieceType() == null) {
            ChessMove validMove = new ChessMove(position, currentCheck);
            moves.add(validMove);

            int nextRow = currentCheck.getRow() + rowItr;
            int nextCol = currentCheck.getColumn() + colItr;

            if (!ChessPosition.isValidPosition(nextRow, nextCol)) {
                break;
            }

            currentCheck = new ChessPosition(nextRow, nextCol);
        }

        return moves;
    }

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        validMoves.addAll(checkDiagonal(board, position, 1, 1));
        validMoves.addAll(checkDiagonal(board, position, -1, 1));
        validMoves.addAll(checkDiagonal(board, position, 1, -1));
        validMoves.addAll(checkDiagonal(board, position, -1, -1));

        return validMoves;
    }
}
