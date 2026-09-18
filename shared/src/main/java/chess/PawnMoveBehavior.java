package chess;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class PawnMoveBehavior extends MoveBehavior {
    private final int directionModifier;
    private final int startingRowIndex;
    private final int promotionRowIndex;

    public PawnMoveBehavior(ChessBoard board, ChessPosition position) {
        super(board, position);

        directionModifier = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        startingRowIndex = color == ChessGame.TeamColor.WHITE ? 2 : 7;
        promotionRowIndex = color == ChessGame.TeamColor.WHITE ? 8 : 1;
    }

    private ArrayList<ChessMove> checkPawnDirection(int rowItr, int colItr, boolean capturing) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int checkRow = position.getRow() + rowItr;
        int checkCol = position.getColumn() + colItr;

        if (!ChessPosition.isValidPosition(checkRow, checkCol)) {
            return null;
        }

        ChessPosition checkPosition = new ChessPosition(checkRow, checkCol);
        ChessPiece checkPiece = board.getPiece(checkPosition);

        if (capturing) {
            if (checkPiece == null) {
                return null;
            }

            ChessGame.TeamColor checkColor = checkPiece.getTeamColor();

            if (checkColor == color) return null;
        } else {
            if (checkPiece != null) {
                return null;
            }

            if (abs(rowItr) == 2) {
                ChessPosition checkMidPosition = new ChessPosition(checkRow-directionModifier, checkCol);
                ChessPiece checkMidPiece = board.getPiece(checkMidPosition);

                if (checkMidPiece != null) return null;
            }
        }

        if (checkRow == promotionRowIndex) {
            moves.add(new ChessMove(position, checkPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(position, checkPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(position, checkPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(position, checkPosition, ChessPiece.PieceType.QUEEN));
        } else {
            moves.add(new ChessMove(position, checkPosition));
        }

        return moves;
    }

    public ArrayList<ChessMove> getValidMoves() {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ArrayList<ChessMove> forwardMoves = checkPawnDirection(directionModifier, 0, false);

        if (forwardMoves != null) {
            validMoves.addAll(forwardMoves);
        }

        if (position.getRow() == startingRowIndex) {
            ArrayList<ChessMove> firstMove = checkPawnDirection(2*directionModifier, 0, false);

            if (firstMove != null) {
                validMoves.addAll(firstMove);
            }
        }

        ArrayList<ChessMove> captureMovesLeft = checkPawnDirection(directionModifier, -1, true);

        if (captureMovesLeft != null) {
            validMoves.addAll(captureMovesLeft);
        }

        ArrayList<ChessMove> captureMovesRight = checkPawnDirection(directionModifier, 1, true);

        if (captureMovesRight != null) {
            validMoves.addAll(captureMovesRight);
        }

        return validMoves;
    }
}
